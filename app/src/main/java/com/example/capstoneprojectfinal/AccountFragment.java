package com.example.capstoneprojectfinal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class AccountFragment extends Fragment {

    private TextView acc_fullname, acc_studentID, acc_email,
            acc_year, acc_corurse, acc_contact,
            acc_password;

    private TextView btnEditProfile;
    private Button signout;
    private ImageView item_about;
    private ImageView userProfilePicture;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    FirebaseFirestore db;
    FirebaseUser user;
    StorageReference storageReference;
    String userID;

    Uri imageURI;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_fragment, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = firebaseAuth.getCurrentUser().getUid();

        userProfilePicture = view.findViewById(R.id.accUSERProfile);

        acc_fullname = view.findViewById(R.id.edit_acc_fname);
        acc_studentID = view.findViewById(R.id.edit_acc_studentID);
        acc_email = view.findViewById(R.id.edit_acc_email);
        acc_year = view.findViewById(R.id.edit_acc_year);
        acc_corurse = view.findViewById(R.id.edit_acc_course);
        acc_contact = view.findViewById(R.id.edit_acc_contact);
        acc_password = view.findViewById(R.id.edit_acc_password);


        btnEditProfile = view.findViewById(R.id.editProfileButton);
        signout = view.findViewById(R.id.buttonSignout);

        item_about = view.findViewById(R.id.item_about);


        DocumentReference documentReference = firestore.collection("student")
                .document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                acc_fullname.setText(value.getString("FullName"));
                acc_studentID.setText(value.getString("StudentID"));
                acc_email.setText(value.getString("Email"));
                acc_year.setText(value.getString("YearLvl"));
                acc_corurse.setText(value.getString("Course"));
                acc_contact.setText(value.getString("Contact"));
                acc_password.setText(value.getString("Password"));
            }
        });
        item_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), item_about);
                popupMenu.getMenuInflater().inflate(R.menu.item_about, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        startActivity(new Intent(getActivity(), About.class));
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                signUserOUT();
                Log.d("TAG", "onClick: Sign OUT!");
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfile.class);
                intent.putExtra("Edit", "Edit ");
                intent.putExtra("fullname", acc_fullname.getText().toString());
                intent.putExtra("studentID", acc_studentID.getText().toString());
                intent.putExtra("email", acc_email.getText().toString());
                intent.putExtra("year", acc_year.getText().toString());
                intent.putExtra("course", acc_corurse.getText().toString());
                intent.putExtra("contact", acc_contact.getText().toString());
                intent.putExtra("password", acc_password.getText().toString());
                startActivity(intent);
            }
        });

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        retrieveImageFromFirebase();
//        PrefManager manager = new PrefManager(getContext());
//        String imageuri = manager.getImageURI();
//        if (imageuri != "") {
//            new ImageLoadTask(imageuri, userProfilePicture).execute();
//        }

        DocumentReference docuRef = db.collection("student").document(userID);
        docuRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> mapData = document.getData();

                        acc_fullname.setText(mapData.get("FullName").toString());
                        acc_studentID.setText(mapData.get("StudentID").toString());
                        acc_email.setText(mapData.get("Email").toString());
                        acc_year.setText(mapData.get("YearLvl").toString());
                        acc_corurse.setText(mapData.get("Course").toString());
                        acc_contact.setText(mapData.get("Contact").toString());
                        acc_password.setText(mapData.get("Password").toString());


                    } else {
                        System.out.println("Error _____");

                    }
                }
            }
        });
    }

    class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }

    private void retrieveImageFromFirebase(){

        final StorageReference fileRef = storageReference.child("student/" +
                user.getUid());
        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(getActivity()).load(uri).into(userProfilePicture);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void signUserOUT() {
        Intent intent = new Intent(getActivity(), LogIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage("Are you sure you want to Exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().finish();
                        System.exit(0);
                    }
                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

