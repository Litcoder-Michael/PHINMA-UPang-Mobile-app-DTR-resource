package com.example.capstoneprojectfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.Inet4Address;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUp extends AppCompatActivity {

    /*AutoCompleteTextView txt_signup_scholarship;
    AutoCompleteTextView txt_signup_department;*/
    AutoCompleteTextView edit_signup_year;
    AutoCompleteTextView edit_signup_course;

    EditText edit_signup_Email;
    EditText edit_signup_StudentID;
    EditText edit_signup_Fullname;
    EditText edit_signup_Contact;
    EditText edit_signup_Password;;

    //Buttons
    TextView txt_signup;
    Button btnSignup;

    //Firebase
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userID;

    PrefManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //pref manager
        manager = new PrefManager(this);
        //Drop Down Sign Up
        edit_signup_year = findViewById(R.id.tv_Year);
        edit_signup_course = findViewById(R.id.tv_course);
        /*txt_signup_department = findViewById(R.id.tv_Department);
        txt_signup_scholarship = findViewById(R.id.tv_Scholarship);*/

        edit_signup_Email = findViewById(R.id.tv_email);
        edit_signup_StudentID = findViewById(R.id.tv_studentNumber);
        edit_signup_Fullname = findViewById(R.id.tv_FullName);
        edit_signup_Contact = findViewById(R.id.tv_ContactNumber);
        edit_signup_Password = findViewById(R.id.tv_Password);

        btnSignup = findViewById(R.id.btnSignup);
/*        txt_signup = findViewById(R.id.tv_signup);*/


        //Array Adapter for Sign Up
        ArrayAdapter<String> myAdapter_Year = new ArrayAdapter<>(SignUp.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Year_Level));
        ArrayAdapter<String> myAdapter_Course = new ArrayAdapter<>(SignUp.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Courses));
        ArrayAdapter<String> myAdapter_Scholarship = new ArrayAdapter<>(SignUp.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Scholarship));
        ArrayAdapter<String> myAdapter_Department = new ArrayAdapter<>(SignUp.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Department));

        //Setting Drop down Resource
        myAdapter_Course.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        myAdapter_Year.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        myAdapter_Scholarship.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        myAdapter_Department.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        //Setting Adapter
        edit_signup_year.setAdapter(myAdapter_Year);
        edit_signup_course.setAdapter(myAdapter_Course);
        /*txt_signup_scholarship.setAdapter(myAdapter_Scholarship);
        txt_signup_department.setAdapter(myAdapter_Department);*/


        // Action for AutoComplete
        edit_signup_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_signup_year.showDropDown();
            }
        });
        edit_signup_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_signup_course.showDropDown();
            }
        });
       /* txt_signup_department.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_signup_department.showDropDown();
            }
        });
        txt_signup_scholarship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_signup_scholarship.showDropDown();
            }
        });
*/

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

/*        if (firebaseAuth.getCurrentUser() !=  null){
            startActivity(new Intent(getApplicationContext(), LogIn.class));
        }*/

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edit_signup_Email.getText().toString();
                String password = edit_signup_Password.getText().toString().trim();
                String fullname = edit_signup_Fullname.getText().toString();
                String studentId = edit_signup_StudentID.getText().toString();
                String contact = edit_signup_Contact.getText().toString();

                String year = edit_signup_year.getText().toString();
                String course = edit_signup_course.getText().toString();
                /*String department = txt_signup_department.getText().toString();
                String scholarship = txt_signup_scholarship.getText().toString();*/

                if (TextUtils.isEmpty(email)){
                    edit_signup_Email.setError("Email is Required!");
                    return;
                }
                else if(TextUtils.isEmpty(password)){
                    edit_signup_Password.setError("Password is Required!");
                    return;
                }
                else if (TextUtils.isEmpty(fullname)){
                    edit_signup_Fullname.setError("Full Name is Required!");
                    return;
                }
                else if (TextUtils.isEmpty(contact)){
                    edit_signup_Contact.setError("Contact Number is Required!");
                    return;
                }
                else if (TextUtils.isEmpty(year)){
                    edit_signup_year.setError("Year Level is Required!");
                    return;
                }
                else if (TextUtils.isEmpty(course)){
                    edit_signup_course.setError("Course is Required!");
                    return;
                }

                //For Account Page
                String fullnameAdd = edit_signup_Fullname.getText().toString();
                String courseAdd = edit_signup_course.getText().toString();

/*                String studentIDAdd = edit_signup_StudentID.getText().toString();
                String yearAdd = edit_signup_year.getText().toString();
                String contactAdd = edit_signup_Contact.getText().toString();
                String emailAdd = edit_signup_Email.getText().toString();
                String passwordAdd = edit_signup_Password.getText().toString();*/



                SharedPreferences sharedPreferences = getSharedPreferences("key_name",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("fnameAdd", fullnameAdd);
                editor.putString("courseAdd", courseAdd);
/*                editor.putString("studentIDAdd", studentIDAdd);
                editor.putString("emailAdd", emailAdd);
                editor.putString("yearAdd", yearAdd);
                editor.putString("contactAdd", contactAdd);
                editor.putString("passwordAdd", passwordAdd);*/
                editor.apply();

                //Register user in firebase

                firebaseAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(SignUp.this, "User Created", Toast.LENGTH_SHORT).show();

                                    userID = firebaseAuth.getCurrentUser().getUid();
                                    DocumentReference documentReference = firestore.collection("student").document(userID);
                                    Map<String, Object> student  = new HashMap<>();
                                    student.put("Email", email);
                                    student.put("StudentID", studentId);
                                    student.put("FullName", fullname);
                                    student.put("Contact", contact);
                                    student.put("Password", password);
                                    student.put("YearLvl", year);
                                    student.put("Course", course);
                                    /*student.put("Scholarship", scholarship);
                                    student.put("Department", department);*/
                                    documentReference.set(student).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("TAG", "onSuccess: User Profile us created for " + userID);
                                            startActivity(new Intent(getApplicationContext(), LogIn.class));
                                            finish();
                                        }
                                    });
/*                                    startActivity(new Intent(getApplicationContext(), LogIn.class));
                                    finish();*/
                                }
                                else {
                                    Toast.makeText(SignUp.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });
    }
    /*private Dialog dialog;*/

    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setMessage("Are you sure you want to Cancel?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(SignUp.this, LogIn.class);
                        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
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