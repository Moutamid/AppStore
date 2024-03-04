package com.moutamid.gitweb;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadFileFromURL extends AsyncTask<String, String, String> {
    private static final String TAG = "DownloadFileFromURL";

    private Context context;
    private Activity activity;
    //    private ProgressDialog progressDialog;
    Dialog dialog;

    TextView nameText;
    TextView progressText;
    Button button;
    boolean isCancelled = false;

    public DownloadFileFromURL(Context context, Activity activity, String suggestedFilename, long downloadID, DownloadManager downloadManager) {
        this.context = context;
        this.activity = activity;
        isCancelled = false;

//        progressDialog = new ProgressDialog(activity);
//        progressDialog.setCancelable(false);
//        progressDialog.setMessage("Loading...");
//        progressDialog.show();

        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog);
        dialog.setCancelable(false);

        nameText = dialog.findViewById(R.id.name);
        progressText = dialog.findViewById(R.id.progressText);
        button = dialog.findViewById(R.id.cancelBtn);

        nameText.setText(suggestedFilename.replace(".apk", ""));
        progressText.setText("Downloading...0%");

        button.setOnClickListener(v -> {
            downloadManager.remove(downloadID);
            isCancelled = true;
            dialog.dismiss();
        });

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    /**
     * Before starting background thread
     * Show Progress Bar Dialog
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Show progress dialog here
    }

    /**
     * Downloading file in background thread
     */
    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            URL url = new URL(f_url[0]);
            URLConnection connection = url.openConnection();
            connection.connect();

            // Get file length
            int lengthOfFile = connection.getContentLength();

            // Input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream to write file
            File file;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
                file = new File(context.getExternalFilesDir(null), "Spotify.apk");
            } else {
                file = new File(Environment.getExternalStorageDirectory(), "Spotify.apk");
            }
            OutputStream output = new FileOutputStream(file);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                if (isCancelled)
                    break;

                total += count;
                // Publishing the progress
//                publishProgress("" + (int) ((total * 100) / lengthOfFile));

                long finalTotal = total;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        private ProgressDialog progressDialog;
                        progressText.setText("Downloading..." + (int) ((finalTotal * 100) / lengthOfFile) + "%");

                    }
                });

                Log.d(TAG, "doInBackground/68:  : " + (int) ((total * 100) / lengthOfFile));

                // Writing data to file
                output.write(data, 0, count);
            }

            // Flushing output
            output.flush();

            // Closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return null;
    }

    /**
     * Updating progress bar
     */
    protected void onProgressUpdate(String... progress) {
        // Set progress percentage
        // progress[0] is the percentage completion
        Log.d(TAG, "onProgressUpdate/92:  : " + progress);
    }

    /**
     * After completing background task
     **/
    @Override
    protected void onPostExecute(String file_url) {
        // Dismiss the progress dialog after the file has been downloaded
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                if (!isCancelled)
                    Toast.makeText(context, "finished", Toast.LENGTH_SHORT).show();
            }
        }, 15000);
    }
}
