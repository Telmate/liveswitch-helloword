package com.example.application;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.projection.MediaProjection;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import fm.liveswitch.*;
import fm.liveswitch.android.LayoutManager;
import fm.liveswitch.android.LogProvider;


public class HelloWorldLogic {

    private final Context context;
    private final Handler handler;
    private static HelloWorldLogic app;

    private HelloWorldLogic(Context context)
    {
        this.context = context.getApplicationContext();
        this.handler = new Handler(context.getMainLooper());
    }

    public static synchronized  HelloWorldLogic getInstance(Context context){
        if(app == null){
            app = new HelloWorldLogic(context);
        }
        return app;
    }

    static {
        Log.setProvider(new LogProvider(LogLevel.Debug));
    }

    private String applicationId = Config.applicationId;
    private String channelId = Config.channelId;
    private String gatewayUrl = Config.gatewayUrl;
    private String sharedSecret = Config.sharedSecret;

    private Client client;
    private Channel channel;

    public Channel getChannel() {
        return channel;
    }

    public Client getClient() {
        return client;
    }

    // Register the client with token.
    private void onClientRegistered(Channel[] channels) {
        // Store our channel reference.
        channel = channels[0];

        // Open a new SFU downstream connection when a new remote upstream connection is opened.
        channel.addOnRemoteUpstreamConnectionOpen(connectionInfo -> {
            Log.info("A remote upstream connection has opened.");
            openSfuDownstreamConnection(connectionInfo);
        });

        // Open a new SFU upstream connection.
        upstreamConnection = openSfuUpstreamConnection(localMedia);

        // Check for existing remote upstream connections and open a downstream connection for
        // each of them.
        for (ConnectionInfo connectionInfo : channel.getRemoteUpstreamConnectionInfos()) {
            openSfuDownstreamConnection(connectionInfo);
        }
    }

    int reRegisterBackoff = 200;
    int maxRegisterBackoff = 60000;
    boolean unregistering = false;

    // Make a registration request.
    public Future<Channel[]> joinAsync() {
        // Create a client.
        client = new Client(gatewayUrl, applicationId);

        // Create a token (do this on the server to avoid exposing your shared secret).
        String token = Token.generateClientRegisterToken(applicationId, client.getUserId(), client.getDeviceId(), client.getId(), null, new ChannelClaim[]{new ChannelClaim(channelId)}, sharedSecret);

        // Allow re-register.
        unregistering = false;

        client.addOnStateChange(client -> {
            if (client.getState() == ClientState.Unregistered) {
                Log.debug("Client has been unregistered.");

                if (!unregistering) {
                    Log.debug(String.format(Locale.US, "Registering with backoff = %d.", reRegisterBackoff));

                    // Incrementally increase register backoff to prevent runaway process.
                    ManagedThread.sleep(reRegisterBackoff);
                    if (reRegisterBackoff < maxRegisterBackoff) {
                        reRegisterBackoff += reRegisterBackoff;
                    }

                    client.register(token).then(channels -> {
                        // Reset re-register backoff after successful registration.
                        reRegisterBackoff = 200;
                        onClientRegistered(channels);
                    }, ex -> Log.error("ERROR: Client unable to register with the gateway.", ex));
                }
            }
        });

        // Register client with token.
        return client.register(token).then(this::onClientRegistered, ex -> Log.error("ERROR: Client unable to register with the gateway.", ex));
    }

    public Future<Object> leaveAsync() {
        if (this.client != null) {
            // Disable re-register.
            unregistering = true;
            return this.client.unregister().fail(ex -> {
                Log.error("ERROR: Unable to unregister client.", ex);
            });
        }
        return null;
    }

    private LocalMedia<View> localMedia;
    private LayoutManager layoutManager;
    private final AecContext aecContext = new AecContext();

    public Future<Object> startLocalMedia(final Activity activity, final RelativeLayout container) {
        final Promise<Object> promise = new Promise<>();

        activity.runOnUiThread(() -> {
            // Create a new local media with audio and video enabled.
            localMedia = new CameraLocalMedia(context, false, false, aecContext);

            // Set local media in the layout.
            layoutManager = new LayoutManager(container);
            layoutManager.setLocalView(localMedia.getView());

            // Start capturing local media.
            localMedia.start().then(localMedia -> {
                promise.resolve(null);

            }, promise::reject);
        });

        return promise;
    }

    public Future<Object> stopLocalMedia() {
        final Promise<Object> promise = new Promise<>();

        if (localMedia == null) {
            promise.resolve(null);
        } else {
            // Stop capturing local media.
            localMedia.stop().then(result -> {
                if (layoutManager != null) {
                    // Remove views from the layout.
                    layoutManager.removeRemoteViews();
                    layoutManager.unsetLocalView();
                    layoutManager = null;
                }

                if (localMedia != null) {
                    localMedia.destroy();
                    localMedia = null;
                }

                promise.resolve(null);

            }, promise::reject);
        }

        return promise;
    }

    private SfuUpstreamConnection upstreamConnection;

    private SfuUpstreamConnection openSfuUpstreamConnection(LocalMedia localMedia) {
        // Create audio and video streams from local media.
        AudioStream audioStream = (localMedia.getAudioTrack() != null) ? new AudioStream(localMedia) : null;
        VideoStream videoStream = (localMedia.getVideoTrack() != null) ? new VideoStream(localMedia) : null;

        // Create a SFU upstream connection with local audio and video.
        SfuUpstreamConnection connection = channel.createSfuUpstreamConnection(audioStream, videoStream);

        connection.addOnStateChange((ManagedConnection conn) -> {
            Log.info(String.format("Upstream connection %s is in a %s state.", conn.getId(), conn.getState().toString()));

            if (conn.getState() == ConnectionState.Closing || conn.getState() == ConnectionState.Failing) {
                if (conn.getRemoteClosed()) {
                    Log.info(String.format("Media server has closed the upstream connection %s.", conn.getId()));
                }
            } else if (connection.getState() == ConnectionState.Failed) {
                // Reconnect if the connection failed.
                openSfuUpstreamConnection(localMedia);
            }
        });

        connection.open();
        return connection;
    }

    private final HashMap<String, SfuDownstreamConnection> downstreamConnections = new HashMap<>();

    private SfuDownstreamConnection openSfuDownstreamConnection(final ConnectionInfo remoteConnectionInfo) {
        // Create remote media.
        final RemoteMedia remoteMedia = new RemoteMedia(context, false, false, aecContext);

        // Adding remote view to UI.
        handler.post(() -> layoutManager.addRemoteView(remoteMedia.getId(), remoteMedia.getView()));

        // Create audio and video streams from remote media.
        AudioStream audioStream = (remoteConnectionInfo.getHasAudio()) ? new AudioStream(remoteMedia) : null;
        VideoStream videoStream = (remoteConnectionInfo.getHasVideo()) ? new VideoStream(remoteMedia) : null;

        // Create a SFU downstream connection with remote audio and video and data streams.
        SfuDownstreamConnection connection = channel.createSfuDownstreamConnection(remoteConnectionInfo, audioStream, videoStream);

        // Store the downstream connection.
        downstreamConnections.put(remoteMedia.getId(), connection);

        connection.addOnStateChange((ManagedConnection conn) -> {
            Log.info(String.format("Downstream connection %s is currently in a %s state.", conn.getId(), conn.getState().toString()));

            if (conn.getState() == ConnectionState.Closing || conn.getState() == ConnectionState.Failing) {
                if (conn.getRemoteClosed()) {
                    Log.info(String.format("Media server has closed the downstream connection %s.", conn.getId()));
                }

                // Removing remote view from UI.
                handler.post(() -> {
                    layoutManager.removeRemoteView(remoteMedia.getId());
                    remoteMedia.destroy();
                });

                downstreamConnections.remove(remoteMedia.getId());

            } else if (conn.getState() == ConnectionState.Failed) {
                // Reconnect if the connection failed.
                openSfuDownstreamConnection(remoteConnectionInfo);
            }
        });

        connection.open();
        return connection;
    }
}