package com.example.capstoneprojectfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class LogIn extends AppCompatActivity {

    EditText editEmail;
    EditText editPassword;
    TextView tv_createAccount;
    Button btn_login;

    //Firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser !=  null){
            startActivity(new Intent(LogIn.this, MainSystem.class));
            finish();
        }

        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        tv_createAccount = findViewById(R.id.tv_create);
        btn_login = findViewById(R.id.btn_login);



        //Action Listeners

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();

                if (TextUtils.isEmpty(email)){
                    editEmail.setError("Email is Required");
                    return;
                }
                else if (TextUtils.isEmpty(password)){
                    editPassword.setError("Password is Required");
                    return;
                }
                else if (password.length() < 5){
                    editPassword.setError("Password must be greater than 5 characters");
                    return;
                }

                //Authentication of account

                firebaseAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(LogIn.this, "Log in Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), MainSystem.class));
                                }
                                else {
                                    Toast.makeText(LogIn.this, "Account Not Registered, Try Again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                //startActivity(new Intent(getApplicationContext(),HomeDrawer.class));
            }
        });
        tv_createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(getApplicationContext(), SignUp.class));
                Intent intent = new Intent(LogIn.this, SignUp.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you sure you want to Exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LogIn.super.onBackPressed();
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