package com.example.helloworld;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.application.HelloWorldLogic;

import fm.liveswitch.Promise;
import fm.liveswitch.Future;

public class StartingFragment extends Fragment {

    private HelloWorldLogic appInstance;
    private Button joinButton;
    private Button leaveButton;

    public StartingFragment() {
    }

    public static StartingFragment newInstance() {
        StartingFragment fragment = new StartingFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        appInstance = HelloWorldLogic.getInstance(getActivity().getBaseContext());

        View startingView = inflater.inflate(R.layout.fragment_starting, container, false);

        joinButton = (Button) startingView.findViewById(R.id.joinButton);
        leaveButton = (Button) startingView.findViewById(R.id.leaveButton);
        joinButton.setClickable(true);
        leaveButton.setClickable(false);

        return startingView;
    }

    @Override
    public void onStart() {
        super.onStart();
        setUpButtons();
    }

    public RelativeLayout getVideoContainer() {
        return (RelativeLayout) getActivity().findViewById(R.id.videoContainer);
    }

    public void setStatusText(String text) {
        if (getView() != null) {
            getView().post(() -> {
                TextView statusText = (TextView) getView().findViewById(R.id.appStatusText);
                statusText.setText(text);
            });
        }
    }

    public void setButtonJoinClickable(boolean clickable) {
        if (getView() != null) {
            getView().post(() -> joinButton.setClickable(clickable));
        }
    }

    public void setButtonStopClickable(boolean clickable) {
        if (getView() != null) {
            getView().post(() -> leaveButton.setClickable(clickable));
        }
    }

    public void setUpButtons() {
        joinButton.setOnClickListener(view -> start().then(result -> {
            setButtonJoinClickable(false);
            setButtonStopClickable(true);
        }));

        leaveButton.setOnClickListener(view -> stop().then(result -> {
            setButtonJoinClickable(true);
            setButtonStopClickable(false);
        }));
    }

    public Future<Object> start() {
        Promise<Object> promise = new Promise<>();
        // <LocalMedia>
        appInstance.startLocalMedia(getActivity(), getVideoContainer()).then(resultStart -> {
        // </LocalMedia>

            appInstance.joinAsync().then(resultJoin -> {
                String message = String.format("Client %s has successfully joined channel %s.",
                        appInstance.getClient().getId(),
                        appInstance.getChannel().getId());
                setStatusText(message);
                promise.resolve(null);
            }).fail(ex -> {
                setStatusText("Unable to join channel.");
                promise.reject(ex);
            });

        // <LocalMedia>
        }).fail(ex -> {
            setStatusText("Unable to start local media.");
            promise.reject(null);
        });
        // </LocalMedia>

        return promise;
    }

    public Future<Object> stop() {
        Promise<Object> promise = new Promise<>();

        if (appInstance.getClient() != null) {
            // <Unregistered>
            appInstance.leaveAsync().then(resultLeave -> {
            // </Unregistered>
                // <LocalMedia>
                appInstance.stopLocalMedia().then(resultStop -> {
                    setStatusText("Application successfully stopped local media.");
                    promise.resolve(null);
                }).fail(ex -> {
                    setStatusText("Unable to stop local media.");
                    promise.reject(ex);
                });
                // </LocalMedia>
           // <Unregistered>
             }).fail(ex -> {
                 setStatusText(String.format("Unable to leave channel %s.", appInstance.getChannel().getId()));
                 promise.reject(ex);
             });
            // </Unregistered>
        } else {
            promise.resolve(null);
        }

        return promise;
    }
}