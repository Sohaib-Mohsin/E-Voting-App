package com.vote.E_Voting_App.User.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.vote.E_Voting_App.User.Adapters.GraphicOverlay;
import com.vote.E_Voting_App.R;
import com.vote.E_Voting_App.User.Adapters.RectOverlay;

import java.util.List;

public class Facial_Recognization extends AppCompatActivity {

    ImageView cameraView;
    GraphicOverlay Overlay;
    int count = 0;
    ProgressDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facial_recognization);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        cameraView = findViewById(R.id.cameraView);
        Overlay = findViewById(R.id.overlay);

        alert = new ProgressDialog(Facial_Recognization.this);
        alert.setCancelable(false);
        alert.setMessage("Processing...");

        getPermission();

    }

    private void runDetector(Bitmap bitmap) {

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionFaceDetectorOptions options = new FirebaseVisionFaceDetectorOptions.Builder().build();

        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance().getVisionFaceDetector(options);

        detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
            @Override
            public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {

//                Toast.makeText(Facial_Recognization.this, "Face Detected!", Toast.LENGTH_SHORT).show();

                processResult(firebaseVisionFaces);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Facial_Recognization.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void processResult(List<FirebaseVisionFace> firebaseVisionFaces) {

        for (FirebaseVisionFace face : firebaseVisionFaces) {

            Rect rect = face.getBoundingBox();

            RectOverlay overlay = new RectOverlay(Overlay, rect);
            Overlay.add(overlay);

            count++;

        }

        if(count==1){
            Toast.makeText(Facial_Recognization.this, "Voter Verified", Toast.LENGTH_SHORT).show();

        }
        else {
            Toast.makeText(Facial_Recognization.this, "Voter not Verified", Toast.LENGTH_SHORT).show();

        }

        alert.dismiss();

    }

    public void VoteNow(View view) {

//        cameraView.start();
//        cameraView.captureImage();
        //     Overlay.clear();

        if (count == 1) {
            startActivity(new Intent(Facial_Recognization.this, Party_Selection.class));
            finish();

        } else {
            Toast.makeText(Facial_Recognization.this, "Face detection failed", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
//        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        cameraView.stop();
    }


    public void getPermission() {
        ActivityCompat.requestPermissions(Facial_Recognization.this, new String[]{Manifest.permission.CAMERA}, 10);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 20);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Bitmap bitmap = Bitmap.createScaledBitmap(photo, cameraView.getWidth(), cameraView.getHeight(), false);
            runDetector(bitmap);

            Overlay.setVisibility(View.INVISIBLE);
            cameraView.setImageBitmap(photo);
            alert.show();
        }
    }
}