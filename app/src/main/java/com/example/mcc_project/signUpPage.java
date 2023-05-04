package com.example.mcc_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mcc_project.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

public class signUpPage extends AppCompatActivity {

    EditText mName, mSBUID, mEmail, mPassword, mPhone;
    Button mRegeisterButton;
    TextView mLoginButton;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseDatabase db;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up_page);

        mName = findViewById(R.id.editTextPersonName);
        mSBUID = findViewById(R.id.editTextSBUID);
        mEmail = findViewById(R.id.editTextTextEmailAddress2);
        mPassword = findViewById(R.id.editTextTextPassword2);
        mPhone = findViewById(R.id.editTextPhone);
        mRegeisterButton = findViewById(R.id.buttonRegister);
        mLoginButton = findViewById(R.id.existingUserID);

        fAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressBar);

        if(fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        mRegeisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mName.getText().toString().trim();
                String sbuID = mSBUID.getText().toString().trim();
                String phone = mPhone.getText().toString().trim();
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(name)) {
                    mName.setError("Name is Required");
                }

                if(TextUtils.isEmpty(sbuID)) {
                    mSBUID.setError("Stony Brook ID is Required");
                }

                if(TextUtils.isEmpty(phone)) {
                    mPhone.setError("Phone Number is Required");
                }

                if(TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is Required");
                    return;
                }

                if(TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required");
                    return;
                }

                if(password.length() < 6) {
                    mPassword.setError("Password must be >= 6 Characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //SAVING USER DETAILS IN FIREBASE DATABASE ////////////////////////////

                if(!name.isEmpty() && !sbuID.isEmpty() && !phone.isEmpty()) {

                    Users users = new Users(name,sbuID,phone);
                    db = FirebaseDatabase.getInstance();
                    reference = db.getReference("Users");
                    reference.child(name).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            name.isEmpty();
                            sbuID.isEmpty();
                            phone.isEmpty();
                        }
                    });

                }

                // REGISTER THE USER IN FIREBASE ///////////////////////////////////////

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(signUpPage.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}