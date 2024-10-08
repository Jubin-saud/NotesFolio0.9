package com.saharia.notesell.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.saharia.notesell.ForgetPasswordActivity;
import com.saharia.notesell.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth auth;
    ActivityLoginBinding binding;

    private ProgressDialog pd2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pd2=new ProgressDialog(this);
        pd2.setTitle("Loging into your account");
        pd2.setCanceledOnTouchOutside(false);

        auth= FirebaseAuth.getInstance();

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd2.setMessage("Please wait");
                pd2.show();
                String email=binding.email.getText().toString();
                String password=binding.password.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()){
                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                  pd2.dismiss();
                                Toast.makeText(LoginActivity.this, " Login Successful! ", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(LoginActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }else {
                    Toast.makeText(LoginActivity.this, "Fill the empty field ", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    public void gotoRegister(View e){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity( intent);
    }
    public void forgotpassword(View v){
        Intent intent2 = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
        startActivity( intent2);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}