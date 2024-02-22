package com.example.helloworld;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.application.HelloWorldLogic;

import java.io.OutputStream;
import java.util.concurrent.Executors;

public class OnFileReceiveFragment extends DialogFragment {

    HelloWorldLogic appInstance;
    Context context;

    public OnFileReceiveFragment() {
    }

    // TODO: Rename and change types and number of parameters.
    public static OnFileReceiveFragment newInstance() {
        return new OnFileReceiveFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        context = getActivity().getBaseContext();
        return inflater.inflate(R.layout.fragment_on_file_receive, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        appInstance = HelloWorldLogic.getInstance(getActivity().getBaseContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("New File received, Download?")
                .setPositiveButton("Yes", (dialog, which) -> {
//                    newFile(appInstance.getFileName());
                })
                .setNegativeButton("No", (dialog, which) -> {
//                    appInstance.setFileData(null);
//                    appInstance.setFileName("");
                });

        return builder.create();
    }

    final int NEW_FILE = 104;

    public void newFile(String name) {
//        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("*/*");
//        intent.putExtra(Intent.EXTRA_TITLE, name);
//        getActivity().startActivityForResult(intent, NEW_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
//                final Uri fileUri = data.getData();
//                final byte[] fileData = appInstance.getFileData();
//                final ContentResolver resolver = context.getContentResolver();
//                if (fileData != null) {
//                    Thread runnable = new Thread(() -> {
//                        try {
//                            OutputStream outputStream = resolver.openOutputStream(fileUri);
//                            outputStream.write(fileData);
//                            outputStream.flush();
//                            outputStream.close();
//                        } catch (Exception ex) {
//                            ex.printStackTrace();
//                        }
//                    });
//                    Executors.newSingleThreadExecutor().execute(runnable);
//                }
            }
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "File Received";
    }
}