package com.example.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

   private Button createAccountButton;
   private ProgressDialog progressDialog;
   private EditText inputName, inputPhoneNumber, inputPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        createAccountButton = (Button) findViewById(R.id.register_page_join_btn);
        inputName = (EditText) findViewById(R.id.register_name_input);
        inputPhoneNumber = (EditText) findViewById(R.id.register_phone_number);
        inputPassword = (EditText) findViewById(R.id.register_password_input);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount() {
        String name = inputName.getText().toString();
        String phoneNumber = inputPhoneNumber.getText().toString();
        String password = inputPassword.getText().toString();

        progressDialog = new ProgressDialog(this);
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(password)){
            Toast.makeText(this,"make sure you fill all required feilds",Toast.LENGTH_LONG);
        }
        else {
            progressDialog.setTitle("Creating Acoount");
            progressDialog.setMessage("Please wait while we create dialog");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();
            
            ValidatePhoneNumber(name,phoneNumber,password);
        }
    }

    private void ValidatePhoneNumber(final String name, final String phoneNumber, final String password) {
        final DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("Users").child(phoneNumber).exists())){
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("phone",phoneNumber);
                    userDataMap.put("name", name);
                    userDataMap.put("password", password);
                    databaseReference.child("Users").child(phoneNumber).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(RegisterActivity.this,"data inserted properly",Toast.LENGTH_LONG);
                            progressDialog.dismiss();
                            Intent intent = new Intent(RegisterActivity.this, loginActivity.class);
                            startActivity(intent);
                        }
                    });
                }
                else {
                    Toast.makeText(RegisterActivity.this,"already existing user",Toast.LENGTH_LONG);
                    progressDialog.dismiss();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
