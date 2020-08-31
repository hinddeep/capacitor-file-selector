package com.bkon.capacitor.fileselector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.getcapacitor.FileUtils.getFileUrlForUri;


//  https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types
@NativePlugin(
        requestCodes={FileSelector.PICKFILE_RESULT_CODE} // register request code(s) for intent results
)
public class FileSelector extends Plugin {
    static final int PICKFILE_RESULT_CODE = 5;
    PluginCall call;

    @PluginMethod()
    public void fileSelector(PluginCall call) {
        this.call = call;
        Boolean value = call.getBoolean("multiple_selection");
        JSArray extensions = call.getArray("ext");
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT); // Intent.ACTION_OPEN_DOCUMENT, Intent.ACTION_OPEN_DOCUMENT_TREE

            String[] supportedMimeTypes = new String[extensions.length()];
            String type;
            for(int i = 0; i < extensions.length(); i++)
            {
                try {
                    if(extensions.getString(i) == "images")
                    {
                        supportedMimeTypes[i] = "image/*";
                    }
                    else if(extensions.getString(i) == "videos")
                    {
                        supportedMimeTypes[i] = "videos/*";
                    }
                    else if(extensions.getString(i) == "audios")
                    {
                        supportedMimeTypes[i] = "audios/*";
                    }
                    else if(extensions.getString(i) == "*")
                    {
                        supportedMimeTypes[i] = "*/*";
                        break;
                    }
                    else
                    {
                        supportedMimeTypes[i] = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extensions.getString(i));
                    }
                } catch (JSONException e) {
                    Log.i("capacitor",e.getMessage());
                }
            }
        chooseFile.putExtra(Intent.EXTRA_MIME_TYPES,supportedMimeTypes);
        chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, value);
        chooseFile.setType("*/*");
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile = Intent.createChooser(chooseFile, "Choose file(s)");
        startActivityForResult(call, chooseFile, PICKFILE_RESULT_CODE);
    }

    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        super.handleOnActivityResult(requestCode, resultCode, data);
        Log.i("capacitor","handling result");
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                JSONArray pathArray = new JSONArray();
                JSONArray extensionArray = new JSONArray();
                JSONArray org_name_Array = new JSONArray();
                Context context = getBridge().getActivity().getApplicationContext();
                // https://stackoverflow.com/questions/19513556/select-multiple-files-with-intent-action-get-content/48824844
                if (data.getClipData() != null) {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                        String[] fileInfo = getCopyFilePath(uri, context);
                        pathArray.put(fileInfo[0]);
                        org_name_Array.put(fileInfo[1]);
                        extensionArray.put(fileInfo[2]);
                    }
                } else {
                    Uri uri = data.getData();
                    String[] fileInfo = getCopyFilePath(uri, context);
                    pathArray.put(fileInfo[0]);
                    org_name_Array.put(fileInfo[1]);
                    extensionArray.put(fileInfo[2]);
                }

                if (pathArray.length() != 0){
                    JSObject ret = new JSObject();
                    ret.put("paths", pathArray.toString());
                    ret.put("original_names", org_name_Array.toString());
                    ret.put("extensions", extensionArray.toString());
                    call.success(ret);
                } else {
                    call.error("No paths found...");
                }
            }
        }
    }

    private static String[] getCopyFilePath(Uri uri, Context context) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
       if(cursor == null)
       {
           return null;
       }
        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        if (!cursor.moveToFirst())
        {
            return null;
        }
        String  name = (cursor.getString(nameIndex));
        File file = new File(context.getCacheDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1024 * 1024;
            int bufferSize = Math.min(inputStream.available(), maxBufferSize);
            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            // Log.i("myfile",e.toString());
            return null;
        } finally {
            if (cursor != null)
                cursor.close();
        }
        /*
         * Path returned by Camera API : http://localhost/_capacitor_file_/data/user/0/io.ionic.starter/cache/12315.1594373585807.jpeg
         * Path returned by file.getPath(): /data/user/0/io.ionic.starter/cache/IMG-20200708-WA0004.jpg
         * So we need to make some corrections here for the fetch API on the web layer to be able to fetch a blob from this file
         * https://github.com/ionic-team/capacitor/blob/cdd317f82828c319e4249716a5fa4a9e6bdf6201/android/capacitor/src/main/java/com/getcapacitor/plugin/Camera.java
         */
        String fileInfo[] = new String[3];
        String modifiedPath = "_capacitor_file_" + file.getPath();
        fileInfo[0] = modifiedPath;
        Log.i("capacitor",fileInfo[0]);
        fileInfo[1] = name.substring(0,name.indexOf('.'));
        fileInfo[2] = name.substring(name.indexOf('.'));
        return fileInfo;
    }

}
