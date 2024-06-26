package com.example.reciperecomendation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView registration=findViewById(R.id.register);
        FirebaseAuth auth=FirebaseAuth.getInstance();
        Button login=findViewById(R.id.login);
        EditText user_email=findViewById(R.id.emailLogin);
        EditText password=findViewById(R.id.passwordLogin);
        if(auth.getCurrentUser().getUid().toString().equals(""))
        {

        }
        else{
            startActivity(new Intent(MainActivity.this, home.class));
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signInWithEmailAndPassword(user_email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Login Successfull", Toast.LENGTH_LONG).show(); 
                            startActivity(new Intent(getApplicationContext(), home.class));
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Wrong password or username",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), com.example.reciperecomendation.registration.class));
            }
        });
    }
}