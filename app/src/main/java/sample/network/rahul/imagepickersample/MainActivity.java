package sample.network.rahul.imagepickersample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton fabScanImageButton, fabScanQRButton;
    private ImageView mSelectedImageView;
    private final String[] mCameraPermission = {Manifest.permission.CAMERA};
    private final String[] mStoragePermission = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private static final int TAKE_PICTURE = 150, SELECT_PICTURE = 151;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fabScanImageButton = findViewById(R.id.fab_scan_image);
        fabScanQRButton = findViewById(R.id.fab_scan_qr);
        mSelectedImageView = findViewById(R.id.imageView);
        fabScanQRButton.setOnClickListener(this);
        fabScanImageButton.setOnClickListener(this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_scan_image: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(mCameraPermission[0]) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(mStoragePermission[1]) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(mStoragePermission[0]) == PackageManager.PERMISSION_GRANTED) {
                        takePhoto();
                    } else {
                        //request permission
                        requestPermissions(new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, TAKE_PICTURE);
                    }
                } else {
                    takePhoto();
                }
            }
            break;
            case R.id.fab_scan_qr: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (checkSelfPermission(mStoragePermission[1]) == PackageManager.PERMISSION_GRANTED) {
                        chooseFromLibrary();
                    } else {
                        //request permission
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, SELECT_PICTURE);
                    }
                } else {
                    chooseFromLibrary();
                }
            }
            break;

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == TAKE_PICTURE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                takePhoto();
            }
        } else if (requestCode == SELECT_PICTURE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                chooseFromLibrary();
            }
        }
    }
    private Uri tempfileUri;
    private void chooseFromLibrary() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_PICTURE);
    }
    private void takePhoto() {
        Intent intent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        Intent intents = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        tempfileUri = Uri.fromFile(AppUtils.getOutputMediaFile());
        intents.putExtra(MediaStore.EXTRA_OUTPUT, tempfileUri);

        // start the image capture Intent
        startActivityForResult(intents, TAKE_PICTURE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SELECT_PICTURE:
                Bitmap bitmap = null;
                if (resultCode == RESULT_OK) {
                    if (data != null) {

                        try {
                            File file = new File(AppUtils.getPath(getApplicationContext(), data.getData()));
                            tempfileUri = Uri.fromFile(file);
                            previewCapturedImage();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

                break;
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
                    File file = new File(AppUtils.getPath(getApplicationContext(), tempfileUri));
                    tempfileUri = Uri.fromFile(file);
                    previewCapturedImage();
                }

                break;
        }

    }
    private void previewCapturedImage() {
        try {
            Glide.with(getApplicationContext())
                    .load(tempfileUri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE).
                    into(mSelectedImageView);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

}
