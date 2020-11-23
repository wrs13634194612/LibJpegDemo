package com.example.imgb;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Author: 柏洲
 * Email:  baizhoussr@gmail.com
 * Date:   2020/7/20 15:45
 * Desc:
 */
public class CompressBitmapActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 1001;

    private static final int REQUEST_CODE_IMAGE = 1002;

    private static final int REQUEST_CODE_KITKAT_IMAGE = 1003;

    TextView pathTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compress_bitmap);

        pathTv = findViewById(R.id.tv_path);
    }

    public void selectPhoto(View view) {
        // 动态申请权限
        int result = ContextCompat.checkSelfPermission(CompressBitmapActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (result != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CompressBitmapActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION);
        } else {
            pickPhoto();
        }
    }

    private void pickPhoto() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),
                    REQUEST_CODE_IMAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_KITKAT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0) {
                pickPhoto();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_IMAGE:
                    if (data != null) {
                        Uri uri = data.getData();
                        compressImage(uri);
                    }
                    break;
                case REQUEST_CODE_KITKAT_IMAGE:
                    if (data != null) {
                        Uri uri = ensureUriPermission(this, data);
                        compressImage(uri);
                    }
                    break;
            }
        }
    }

    @SuppressWarnings("ResourceType")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private Uri ensureUriPermission(Context context, Intent intent) {
        Uri uri = intent.getData();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final int takeFlags = intent.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
            context.getContentResolver().takePersistableUriPermission(uri, takeFlags);
        }
        return uri;
    }

    private void compressImage(final Uri uri) {
        Toast.makeText(CompressBitmapActivity.this, "start compress...", Toast.LENGTH_LONG).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                    File fromQualityFile = new File(getExternalCacheDir(), "FromQuality.jpg");
                   EffectiveBitmapUtils.compressByQuality(bitmap, fromQualityFile);

                    File fromSizeFile = new File(getExternalCacheDir(), "FromSize.jpg");
                   EffectiveBitmapUtils.compressBySize(bitmap, fromSizeFile);

                    File fromSample = new File(getExternalCacheDir(), "FromSample.jpg");
                    File f = new File(Objects.requireNonNull(FileUtil.getFilePathForN(CompressBitmapActivity.this, uri)));
                  EffectiveBitmapUtils.compressBySample(f.getAbsolutePath(), fromSample);

                    File fromJniFile = new File(getExternalCacheDir(), "FromJNI.jpg");
                EffectiveBitmapUtils.compressByJNI(bitmap, fromJniFile.getAbsolutePath(), true);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pathTv.setText(getExternalCacheDir().getAbsolutePath());
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String getPathByUri(Context context, Uri uri) {
        String path;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            path = UriToPathUtils.getPath(context, uri);
        } else {
            path = UriToPathUtils.getEncodedPath(context, uri);
        }
        return path;
    }

}
