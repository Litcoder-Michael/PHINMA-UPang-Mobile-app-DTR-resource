package com.example.capstoneprojectfinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private AutoCompleteTextView yearProfile;
    private AutoCompleteTextView courseProfile;

    private EditText fullnameProfile, studentIDProfile, emailProfile
                    , contactProfile, passwordProfile;

    private Button updateButton;
    private  ImageView userProfilePicture;
    private ImageView cameraButton;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    StorageReference storageReference;
    FirebaseUser user;

    ImageView backtoAccount;
    PrefManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = new PrefManager(this);

        setContentView(R.layout.activity_edit_profile);



        //Initialization for Variables

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user = firebaseAuth.getCurrentUser();

        //Initialization fot fields and Buttons
        backtoAccount = findViewById(R.id.backtoAccount);
        updateButton = findViewById(R.id.btnSaveProfile);
        userProfilePicture = findViewById(R.id.userProfilePicture);
        cameraButton = findViewById(R.id.cameraButton);

        fullnameProfile = findViewById(R.id.profile_edit_fullname);
        studentIDProfile = findViewById(R.id.profile_edit_studentID);
        emailProfile = findViewById(R.id.profile_edit_email);
        courseProfile = findViewById(R.id.profile_edit_course);
        yearProfile = findViewById(R.id.profile_edit_year);
        contactProfile = findViewById(R.id.profile_edit_contact);
        passwordProfile = findViewById(R.id.profile_edit_password);

        //Getting the array-list in the String XML for AutocompleteTextview
        ArrayAdapter<String> myAdapter_Year = new ArrayAdapter<>(EditProfile.this, android.R.layout.simple_list_item_activated_1, getResources().getStringArray(R.array.Year_Level));
        ArrayAdapter<String> myAdapter_Course = new ArrayAdapter<>(EditProfile.this, android.R.layout.simple_list_item_activated_1, getResources().getStringArray(R.array.Courses));

        myAdapter_Course.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        myAdapter_Year.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        yearProfile.setAdapter(myAdapter_Year);
        courseProfile.setAdapter(myAdapter_Course);


        //Setting Gathered Data to Signup
        Intent data = getIntent();
        String fullname = data.getStringExtra("fullname");
        String studentID = data.getStringExtra("studentID");
        String email = data.getStringExtra("email");
        String year = data.getStringExtra("year");
        String course = data.getStringExtra("course");
        String contact = data.getStringExtra("contact");
        String password = data.getStringExtra("password");

        //StorageReference profileRef = storageReference.child("")
        StorageReference profileRef = storageReference.child("student/" +
                firebaseAuth.getCurrentUser().getUid());
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(userProfilePicture);
            }
        });

        //From account fragment
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("Edit") != null) {
                Toast.makeText(getApplicationContext(), "Profile" +
                        (bundle.getString("Edit")), Toast.LENGTH_SHORT).show();
            }
        }

        //Action for the Updating the Data

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fullnameProfile.getText().toString().isEmpty() || studentIDProfile.getText().toString().isEmpty()
                        || emailProfile.getText().toString().isEmpty() || yearProfile.getText().toString().isEmpty()
                        || courseProfile.getText().toString().isEmpty() || contactProfile.getText().toString().isEmpty()
                        || passwordProfile.getText().toString().isEmpty()) {
                    Toast.makeText(EditProfile.this, "There are Empty Fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                String email = emailProfile.getText().toString();
                String password = passwordProfile.getText().toString();
                user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference documentReference = firestore.collection("student").document(user.getUid());
                        Map<String, Object> edited = new HashMap<>();
                        edited.put("Email", email);
                        edited.put("FullName", fullnameProfile.getText().toString());
                        edited.put("StudentID", studentIDProfile.getText().toString());
                        edited.put("YearLvl", yearProfile.getText().toString());
                        edited.put("Course", courseProfile.getText().toString());
                        edited.put("Contact", contactProfile.getText().toString());
                        //edited.put("Password", passwordProfile.getText().toString());
                        documentReference.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditProfile.this, "Profile is Updated", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(EditProfile.this, AccountFragment.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                user.updatePassword(password).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference documentReference = firestore.collection("student").document(user.getUid());
                        Map<String, Object> edited = new HashMap<>();
                        edited.put("Password", passwordProfile.getText().toString());

                        documentReference.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditProfile.this, "Profile is Updated", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(EditProfile.this, AccountFragment.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                String fullnameAdd = fullnameProfile.getText().toString();
                String courseAdd = courseProfile.getText().toString();

                SharedPreferences sharedPreferences = getSharedPreferences("key_name",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("fnameAdd", fullnameAdd);
                editor.putString("courseAdd", courseAdd);
                editor.apply();

            }
        });
        //Setting the updated Text on account Fragment

        fullnameProfile.setText(fullname);
        studentIDProfile.setText(studentID);
        emailProfile.setText(email);
        courseProfile.setText(course);
        yearProfile.setText(year);
        contactProfile.setText(contact);
        passwordProfile.setText(password);

        //Actions for Button

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, 1000);
            }
        });

        yearProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yearProfile.showDropDown();
            }
        });
        courseProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                courseProfile.showDropDown();
            }
        });

        backtoAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                //fragmentTransaction.replace(R.id.editProfileAccount, new AccountFragment()).commit();
                //finish();
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);

                builder.setMessage("Are you sure you want to Cancel?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
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
        });

    }
    // setting the Image for Profile Picture

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000){
            if (resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();

                uploadImageToFirebase(imageUri);
            }
        }
    }

    // uploading image to Firebase



    private void uploadImageToFirebase(Uri imageUri) {
        final StorageReference fileRef = storageReference.child("student/" +
                firebaseAuth.getCurrentUser().getUid());
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        //manager.saveImageURI(uri.toString());
                        Glide.with(getApplicationContext()).load(uri).into(userProfilePicture);

                         }
                    });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);

        builder.setMessage("Are you sure you want to Cancel?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
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
    public void alertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);

        builder.setMessage("Are you sure you want to Save?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
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