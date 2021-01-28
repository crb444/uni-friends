package com.example.unifriends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

/**
 *  Author: Li He
 *  Email: lhe3@student.unimelb.edu.au
 *  Class Profile displays the user's information registered when sign up
 */
public class Profile extends AppCompatActivity {
    private final String TAG = "Profile";
    private String facialID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String userID = getIntent().getStringExtra("userID");
        getData(userID);
    }

    /**
     * function retrieve the information from the backend
     * @param userID the id of the user
     */
    private void getData(String userID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(userID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        setText(document.get("name").toString(), document.get("uni").toString(),
                                document.get("major").toString(), document.get("degree").toString()
                                , document.get("email").toString());
                        setFacialID(document.get("facialID").toString());
                        setPhoto(document.get("photo").toString());

                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                    } else {
                        Log.d(TAG, "No such document");
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                }
            }
        });
    }

    private void setText(String name, String uni, String major, String degree, String email) {
        TextView tvName, tvUni, tvMajor, tvdegree, tvEmail;

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvUni = findViewById(R.id.tvUni);
        tvMajor = findViewById(R.id.tvMajor);
        tvdegree = findViewById(R.id.tvDegree);

        tvName.setText(name);
        tvEmail.setText(email);
        tvUni.setText(uni);
        tvMajor.setText(major);
        tvdegree.setText(degree);

    }

    private void setFacialID(String facialID) {
        this.facialID = facialID;
    }

    /**
     * download the photo from firebase storage and set accordingly
     * @param source
     */
    private void setPhoto(String source) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference pathReference = storageRef.child(source);

        final long ONE_MEGABYTE = 1024 * 1024 * 5;
        pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ImageView imageView = findViewById(R.id.profileImage);
                imageView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d(TAG, exception.toString());
            }
        });
    }

    /**
     * onClick method navigate the user to the verification activity
     * @param view the clicked view
     */
    public void goToVerification(View view) {
        Intent intent = new Intent(Profile.this, Verification.class);
        intent.putExtra("facialID", facialID);
        startActivity(intent);
    }


}
