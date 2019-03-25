package com.example.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecommerce.Model.CurrentUser;
import com.example.ecommerce.Model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class loginActivity extends AppCompatActivity {

    private EditText InputNumber, InputPassword;
    private Button LoginButton;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginButton = (Button) findViewById(R.id.login_page_join_btn);
        InputNumber = (EditText) findViewById(R.id.login_phone_number);
        InputPassword = (EditText) findViewById(R.id.login_password_input);
        progressDialog = new ProgressDialog(this);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }


        });

    }
    private void LoginUser() {

        String phoneNumber = InputNumber.getText().toString();
        String password = InputPassword.getText().toString();
        // Toast.makeText(this,"make sure you fill all required feilds",Toast.LENGTH_LONG);
        if(TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(password)) {
            //Toast.mak;
             Toast.makeText(this, "make sure you fill all required feilds", Toast.LENGTH_LONG);
        }else {
            progressDialog.setTitle("Login Activity");
            progressDialog.setMessage("Please wait while we create dialog");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            AllowAccessToAccount(phoneNumber, password);
        }

    }

    private void AllowAccessToAccount(String phoneNumber, String password) {
        final DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.print(dataSnapshot.child("Users"));
                if(dataSnapshot.child("Users").child(phoneNumber).exists()){
                    Users usersData = dataSnapshot.child("Users").child(phoneNumber).getValue(Users.class);
                    if(usersData.getPhone().equals(phoneNumber)){
                        if(usersData.getPassword().equals(password)){
                            Toast.makeText(loginActivity.this,"Logged In successfully", Toast.LENGTH_LONG);
                            progressDialog.dismiss();
                            CurrentUser.name = usersData.getName();
                            CurrentUser.phoneNumber =usersData.getPhone();
                            Intent intent = new Intent(loginActivity.this, UserHomeActivity.class);
                            startActivity(intent);
                        }
                    }
                }
                else {
                   Toast.makeText(loginActivity.this,"Account with this phone number does not exists", Toast.LENGTH_LONG);
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
