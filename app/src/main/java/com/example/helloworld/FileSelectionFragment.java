package com.example.helloworld;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.application.HelloWorldLogic;

import java.io.InputStream;
import java.util.concurrent.Executors;

public class FileSelectionFragment extends Fragment {

    HelloWorldLogic appInstance;

    public FileSelectionFragment() {
    }

    // TODO: Rename and change types and number of parameters.
    public static FileSelectionFragment newInstance() {
        return new FileSelectionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        appInstance = HelloWorldLogic.getInstance(getActivity().getBaseContext());
        return inflater.inflate(R.layout.fragment_file_selection, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setupButtons();
    }

    Button fileSelect;
    final static int OPEN_FILE = 101;

    public void setupButtons() {
        fileSelect = getView().findViewById(R.id.selectFileButton);
        fileSelect.setOnClickListener(view -> openDirectory());
    }

    public void openDirectory() {
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("*/*");
//        startActivityForResult(intent, OPEN_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OPEN_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
//                final Uri fileUri = data.getData();
//                final Cursor returnCursor = getActivity().getContentResolver().query(fileUri, null, null, null, null);
//                Thread runnable = new Thread(() -> {
//                    try {
//                        InputStream inputStream = getActivity().getContentResolver().openInputStream(fileUri);
//                        appInstance.sendFile(inputStream, returnCursor);
//                        inputStream.close();
//                        returnCursor.close();
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                });
//                Executors.newSingleThreadExecutor().execute(runnable);
            }
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "Send File";
    }
}