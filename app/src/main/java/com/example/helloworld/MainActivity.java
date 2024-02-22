package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.TableRow;

import com.example.application.HelloWorldLogic;

import java.util.ArrayList;
import java.util.List;

import fm.liveswitch.Future;

public class MainActivity extends AppCompatActivity {

    // Fragments
    private StartingFragment startingFragment;
    private BroadcastFragment broadcastFragment;
    private DeviceSwitchingFragment deviceSwitchingFragment;
    private ScreenShareFragment screenShareFragment;
    private TextChannelFragment textChannelFragment;
    private MutingFragment mutingFragment;
    private FileSelectionFragment fileSelectionFragment;
    private OnFileReceiveFragment onFileReceiveFragment;

    HelloWorldLogic appInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appInstance = HelloWorldLogic.getInstance(getBaseContext());
        setupAllFragments();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermissions();
    }

    // <CheckingPermissions>
    /*
        IMPORTANT: The client needs to set certain permissions,
        these can be be found in the "AndroidManifest.xml"
        file in "manifests" folder. This needs to occur
        prior to starting our media as the LiveSwitch SDK
        needs access to such media.
     */
    private void checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            List<String> requiredPermissions = new ArrayList<>(2);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(Manifest.permission.RECORD_AUDIO);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(Manifest.permission.CAMERA);
            }

            if (requiredPermissions.size() != 0) {
                requestPermissions(requiredPermissions.toArray(new String[0]), 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    // </CheckingPermissions>

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fileSelectionFragment.onActivityResult(requestCode, resultCode, data);
        onFileReceiveFragment.onActivityResult(requestCode, resultCode, data);
    }


    // <CallingStartAndStopUI>

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    public void addFragment(Fragment fragment, int id) {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(id, fragment)
                .setReorderingAllowed(true)
                .commit();
    }

    public void removeFragment(int id) {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragmentToRemove = manager.findFragmentById(id);
        if (fragmentToRemove != null) {
            manager.beginTransaction()
                    .remove(fragmentToRemove)
                    .commit();
        }
    }

    public void setupAllFragments() {
        startingFragment = StartingFragment.newInstance();
        broadcastFragment = BroadcastFragment.newInstance();
        deviceSwitchingFragment = DeviceSwitchingFragment.newInstance();
        screenShareFragment = ScreenShareFragment.newInstance();
        textChannelFragment = TextChannelFragment.newInstance();
        mutingFragment = MutingFragment.newInstance();
        fileSelectionFragment = FileSelectionFragment.newInstance();
        onFileReceiveFragment = OnFileReceiveFragment.newInstance();

        addDefaultVideoButtons();

        setupAccessoryFragment(broadcastFragment, view ->
                addBroadcastingButtons()
        );

        setupAccessoryFragment(deviceSwitchingFragment, view -> {
            addFragment(deviceSwitchingFragment, R.id.accessoryContainer);
            addDefaultVideoButtons();
        });

        setupAccessoryFragment(screenShareFragment, view -> {
            addFragment(screenShareFragment, R.id.accessoryContainer);
            addDefaultVideoButtons();
        });

        setupAccessoryFragment(mutingFragment, view -> {
            addFragment(mutingFragment, R.id.accessoryContainer);
            addDefaultVideoButtons();
        });

        setupAccessoryFragment(fileSelectionFragment, view -> {
            addFragment(fileSelectionFragment, R.id.accessoryContainer);
            addDefaultVideoButtons();
        });

        setupAccessoryFragment(textChannelFragment, view -> {
            textChannelFragment.show(getSupportFragmentManager(), textChannelFragment.toString());
            addDefaultVideoButtons();
        });

        // <FileTransfer>
//        appInstance.setFileReceiveEvent(() -> onFileReceiveFragment.show(getSupportFragmentManager(), onFileReceiveFragment.toString()));
        // </FileTransfer>
    }

    public void addDefaultVideoButtons() {
        if (!startingFragment.isResumed()) {
            if (broadcastFragment.isResumed()) {
                broadcastFragment.stop().then(result -> {
                    addFragment(startingFragment, R.id.videoButtons);
                });
            } else {
                addFragment(startingFragment, R.id.videoButtons);
            }
        }
    }

    public void addBroadcastingButtons() {
        if (!broadcastFragment.isResumed()) {
            if (startingFragment.isResumed()) {
                startingFragment.stop().then(result -> {
                    addFragment(broadcastFragment, R.id.videoButtons);
                    removeFragment(R.id.accessoryContainer);
                });
            } else {
                addFragment(broadcastFragment, R.id.videoButtons);
                removeFragment(R.id.accessoryContainer);
            }
        }
    }

    public void setupAccessoryFragment(Fragment fragment, View.OnClickListener action) {
        TableRow buttonRow = (TableRow) findViewById(R.id.fragmentButtonRow);
        Button button = new Button(this);
        button.setText(fragment.toString());
        button.setOnClickListener(action);
        buttonRow.addView(button);
    }
}