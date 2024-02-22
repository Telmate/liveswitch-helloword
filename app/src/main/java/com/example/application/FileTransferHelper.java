package com.example.application;

import android.database.Cursor;
import android.provider.OpenableColumns;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import fm.liveswitch.Log;

final public class FileTransferHelper {

    private FileTransferHelper() {
    }

    // Getting file bytes from the file input stream.
    public static byte[] getFileBytes(InputStream inputStream) {
        byte[] fileBytes = null;
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        // From: https://stackoverflow.com/questions/10296734/image-uri-to-bytesarray
        try {
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            fileBytes = byteBuffer.toByteArray();

        } catch (Exception ex) {
            Log.error(Arrays.toString(ex.getStackTrace()));

        } finally {
            try {
                byteBuffer.close();
            } catch (Exception ex) {
                Log.error(Arrays.toString(ex.getStackTrace()));
            }
        }
        return fileBytes;
    }

    /*
        Getting name and name length from
        Android File Cursor

        - Note, make sure to close the cursor
        when done with it
     */

    public static String getFileName(Cursor fileCursor) {
        int nameIndex = fileCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        fileCursor.moveToFirst();
        return fileCursor.getString(nameIndex);
    }

    public static byte[] getFileNameSizeInBytes(Cursor fileCursor) {
        int size = getFileName(fileCursor).length();
        return ByteBuffer
                .allocate(4)
                .order(ByteOrder.BIG_ENDIAN)
                .putInt(size)
                .array();
    }

    public static byte[] getFileNameInBytes(Cursor fileCursor) {
        return getFileName(fileCursor).getBytes(StandardCharsets.US_ASCII);
    }

    /*
        Converting bytes into string or int. We use
        ASCII for strings and Big Endian for integers
     */

    public static String getStringFromBytes(byte[] bytes, int offset, int length){
        byte[] stringSubArray = Arrays.copyOfRange(bytes, offset, length + offset);
        return new String(stringSubArray, StandardCharsets.US_ASCII);
    }

    public static int getIntFromBytes(byte[] bytes, int offset) {
        byte[] intStringSubArray = Arrays.copyOfRange(bytes, offset, bytes.length);
        return ByteBuffer
                .wrap(intStringSubArray)
                .order(ByteOrder.BIG_ENDIAN)
                .getInt();
    }

}
