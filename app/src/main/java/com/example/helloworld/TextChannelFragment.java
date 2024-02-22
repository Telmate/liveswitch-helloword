package com.example.helloworld;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.application.HelloWorldLogic;

public class TextChannelFragment extends DialogFragment {

    private HelloWorldLogic appInstance;
    private static String messages = "";
    private EditText inputField;
    private TextView chatMessages;

    public TextChannelFragment() {
    }

    public static TextChannelFragment newInstance() {
        TextChannelFragment fragment = new TextChannelFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_text_channel, null);

        appInstance = HelloWorldLogic.getInstance(getActivity().getBaseContext());

        setUpTextMessaging(view);
        builder.setView(view);

        return builder.create();
    }

    public void addTextChatMessage(String message) {
        messages += message;
        chatMessages.setText(messages);
    }

    @NonNull
    @Override
    public String toString() {
        return "Text Chat";
    }

    private void setUpTextMessaging(View view) {
        inputField = (EditText) view.findViewById(R.id.chatMessageInput);
        inputField.setOnEditorActionListener((textView, i, keyEvent) -> {
//            if (i == EditorInfo.IME_ACTION_DONE) {
//                appInstance.sendMessage(inputField.getText().toString());
//                inputField.setText("");
//                return true;
//            }
            return false;
        });

//        appInstance.setOnMessage(this::addTextChatMessage);
//
//        chatMessages = (TextView) view.findViewById(R.id.chatMessagesContainer);
//        chatMessages.setMovementMethod(new ScrollingMovementMethod());
//        chatMessages.setText(messages);

        Button leaveButton = (Button) view.findViewById(R.id.exitChat);
        leaveButton.setOnClickListener(v -> getDialog().dismiss());
    }
}