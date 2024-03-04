package com.moutamid.gitweb;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.webkit.CookieManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.moutamid.gitweb.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import im.delight.android.webview.AdvancedWebView;

public class MainActivity extends AppCompatActivity implements AdvancedWebView.Listener {

    private ActivityMainBinding b;
    public ProgressDialog progressDialog;
    DownloadManager downloadManager;
    long downloadID;

    public void checkApp(Activity activity) {
        String appName = "AppStore"; //TODO: CHANGE APP NAME

        new Thread(() -> {
            URL google = null;
            try {
                google = new URL("https://raw.githubusercontent.com/Moutamid/Moutamid/main/apps.txt");
            } catch (final MalformedURLException e) {
                e.printStackTrace();
            }
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(google != null ? google.openStream() : null));
            } catch (final IOException e) {
                e.printStackTrace();
            }
            String input = null;
            StringBuffer stringBuffer = new StringBuffer();
            while (true) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        if ((input = in != null ? in.readLine() : null) == null) break;
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                }
                stringBuffer.append(input);
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
            String htmlData = stringBuffer.toString();

            try {
                JSONObject myAppObject = new JSONObject(htmlData).getJSONObject(appName);

                boolean value = myAppObject.getBoolean("value");
                String msg = myAppObject.getString("msg");

                if (value) {
                    activity.runOnUiThread(() -> {
                        new Builder(activity)
                                .setMessage(msg)
                                .setCancelable(false)
                                .show();
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        checkApp(this);

        run();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        String url = "https://yb9587.github.io/appstore/";
        b.webview.setListener(this, this);
        b.webview.loadUrl(url);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1111);
            }
        }, 3000);

    }

    private void run() {
        /*Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(MainActivity.this, getPackageName() + ".provider",
                    new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile() + "/" + "Spotify.apk"));

            List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        } else {
            uri = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile() + "/" + "Spotify.apk"));
        }

        intent.setDataAndType(uri, "application/vnd.android.package-archive");

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);*/
        //-----------------
        /*Intent install = new Intent(Intent.ACTION_VIEW);
        install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri apkURI = FileProvider.getUriForFile(
                MainActivity.this,
                getApplicationContext()
                        .getPackageName() + ".provider", new File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile() + "/" + "Spotify.apk"));
        install.setDataAndType(apkURI, ".apk");
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(install);*/
        //---------------------------
        /*Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        intent.setData(Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile() + "/" + "Spotify.apk"));
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
        intent.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, getPackageName());

        // Launch the Intent
        startActivity(intent);*/
        //---------------------------
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile() + "/" + "Spotify.apk")), "application/vnd.android.package-archive");
        startActivity(installIntent);
    }


    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        progressDialog.show();
    }

    @Override
    public void onPageFinished(String url) {
        progressDialog.dismiss();
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        progressDialog.dismiss();
        Toast.makeText(this, "" + description, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
//        downloadUsingPR(url, suggestedFilename);

        new Builder(MainActivity.this)
                .setMessage("Do you want to download " + suggestedFilename.replace(".apk", "") + " ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        //cookie
                        String cookie = CookieManager.getInstance().getCookie(url);
                        //Add cookie and User-Agent to request
                        request.addRequestHeader("Cookie", cookie);
                        request.addRequestHeader("User-Agent", userAgent);
                        //file scanned by MediaScannar
                        request.allowScanningByMediaScanner();
                        //Download is visible and its progress, after completion too.
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
                        //DownloadManager created
                        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        //Saving files in Download folder
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, suggestedFilename);
                        //download enqued
                        downloadID = downloadManager.enqueue(request);

                        new DownloadFileFromURL(
                                MainActivity.this, MainActivity.this,
                                suggestedFilename, downloadID, downloadManager)
                                .execute(url);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    @Override
    public void onExternalPageRequest(String url) {

    }

    private void downloadUsingPR(String url, String suggestedFilename) {
        int downloadId = PRDownloader.download(url, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), suggestedFilename)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        Log.d("TAGGER", "onStartOrResume/288:  : ");
                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {
                        Log.d("TAGGER", "onPause/296: ");
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {
                        Log.d("TAGGER", "onCancel/302:  : ");
                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        Log.d("TAGGER", "onProgress/305:  : " + progress);
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Log.d("TAGGER", "onDownloadComplete/314:  : ");
                    }

                    @Override
                    public void onError(com.downloader.Error error) {
                        Log.d("TAGGER", "onError/319:  : " + error.getResponseCode());
                    }
                });
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        b.webview.onResume();
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        b.webview.onPause();
        // ...
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        b.webview.onDestroy();
        super.onDestroy();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        b.webview.onActivityResult(requestCode, resultCode, intent);
        // ...
    }

    @Override
    public void onBackPressed() {
        if (!b.webview.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
