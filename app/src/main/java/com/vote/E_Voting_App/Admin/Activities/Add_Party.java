package com.vote.E_Voting_App.Admin.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vote.E_Voting_App.R;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Add_Party extends AppCompatActivity {

    CircleImageView Image;
    EditText Candidate, Party, Description;
    TextView Part_txt, Des_txt;
    DatabaseReference databaseReference, Reference;
    StorageReference storageReference;
    String party_name = "";
    Uri img = null;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_party);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        databaseReference = FirebaseDatabase.getInstance("https://e-voting-android-app-default-rtdb.firebaseio.com/").getReference().child("All_Candidates");
        Reference = FirebaseDatabase.getInstance("https://e-voting-android-app-default-rtdb.firebaseio.com/").getReference().child("Candidate_Votes");
        storageReference = FirebaseStorage.getInstance("gs://e-voting-android-app.appspot.com").getReference();

        Candidate = findViewById(R.id.candidate_name);
        Party = findViewById(R.id.party_name);
        Description = findViewById(R.id.party_description);
        Part_txt = findViewById(R.id.partytv);
        Des_txt = findViewById(R.id.partydestv);
        Image = findViewById(R.id.image);

    }

    public void Submit(View view) {

        if (Candidate.length() > 0 && Party.length() > 0 && Description.length() > 0 && img != null) {

            Reference.child(Party.getText().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        Display_Dialog();
                        StorageReference reference = storageReference.child("Party_Images").child(Party.getText().toString() + ".jpg");
                        reference.putFile(img).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isSuccessful()) ;
                                Uri uri = uriTask.getResult();

                                if (uriTask.isSuccessful()) {

                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("PartyName", Party.getText().toString());
                                    map.put("PartyDescription", Description.getText().toString());
                                    map.put("ImageURL", uri.toString());
                                    map.put("Candidate", Candidate.getText().toString());

                                    Reference.child(Party.getText().toString()).child("Votes").setValue("0");
                                    databaseReference.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                Part_txt.setText(Party.getText().toString());
                                                Des_txt.setText(Description.getText().toString());

                                                dialog.dismiss();
                                                Toast.makeText(Add_Party.this, "Party Submitted!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                dialog.dismiss();
                                                Toast.makeText(Add_Party.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(Add_Party.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else if (snapshot.exists()) {
                        Toast.makeText(Add_Party.this, "Party exists", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        } else {
            Toast.makeText(Add_Party.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        }

    }

    public void upload(View view) {
        if (ActivityCompat.checkSelfPermission(Add_Party.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Add_Party.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
        } else {
            Pick();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10 && grantResults.length > 0) {
            boolean StorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (StorageAccepted) {
                Pick();
            } else {
                Toast.makeText(Add_Party.this, "Please Enable Storage Permission!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20 && resultCode == RESULT_OK && data != null) {
            img = data.getData();
            Image.setImageURI(img);
            Toast.makeText(Add_Party.this, "Captured", Toast.LENGTH_SHORT).show();
        }
    }

    private void Pick() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 20);
    }

    public void Display_Dialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Add_Party.this, android.R.style.Theme_Material_Dialog_Alert);
        View v = getLayoutInflater().inflate(R.layout.custom_progressdialog, null);
        alertDialog.setCancelable(false);
        alertDialog.setView(v);
        dialog = alertDialog.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

}