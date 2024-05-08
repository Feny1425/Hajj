package feny.job.hajj.Utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    public static File getFileFromUri(Context context, Uri uri) throws IOException {
        File outputFile = createTempFile(context);
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        if (inputStream != null) {
            try {
                copyInputStreamToFile(inputStream, outputFile);
            } finally {
                inputStream.close();
            }
        }
        return outputFile;
    }

    private static File createTempFile(Context context) throws IOException {
        String fileName = "temp_file";
        File outputDir = context.getCacheDir(); // You can also use getFilesDir() if you want to store the file persistently
        return File.createTempFile(fileName, null, outputDir);
    }

    private static void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {
        OutputStream outputStream = new FileOutputStream(file);
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            outputStream.close();
        }
    }
    public static String getFileNameFromUri(Context context, Uri uri) {
        String fileName = null;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    fileName = cursor.getString(displayNameIndex);
                }
            } finally {
                cursor.close();
            }
        }
        return fileName;
    }
}
