package com.duyhoang.happychatapp.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.duyhoang.happychatapp.AppConfig;
import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.Utils.ValidationUtil;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import static com.duyhoang.happychatapp.activities.RegisterActivity.RC_FACEBOOK_LOGIN;
import static com.duyhoang.happychatapp.activities.RegisterActivity.RC_GOOGLE_LOGIN;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    public static String TAG = "LogInActivity";

    private EditText etEmail,etPassword;
    private Button btnLogin;
    private SignInButton btnGoogleLogin;
    private LoginButton btnFacebookLogin;
    private TextView txtRegisterNow;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, ContactActivity.class));
        }
        setContentView(R.layout.activity_login);
        initUI();

    }


    private void initUI() {
        getSupportActionBar().hide();
        getWindow().setBackgroundDrawableResource(R.drawable.background_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        etEmail =   findViewById(R.id.edit_text_login_email);
        etPassword =   findViewById(R.id.edit_text_login_password);
        btnLogin =   findViewById(R.id.button_login_login);
        btnFacebookLogin =   findViewById(R.id.button_login_facebook_login);
        btnGoogleLogin =   findViewById(R.id.button_login_google_login);
        txtRegisterNow =   findViewById(R.id.text_view_login_register);

        btnLogin.setOnClickListener(this);
        btnGoogleLogin.setOnClickListener(this);
        btnFacebookLogin.setOnClickListener(this);
        txtRegisterNow.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_login_login: loginWithEmailAndPassword();
                break;
            case R.id.button_login_facebook_login: loginWithFacebookSignIn();
                break;
            case R.id.button_login_google_login: loginWithGoogleSignin();
                break;
            case R.id.text_view_login_register:
                openContactScreen();
                break;
        }
    }

    private void openContactScreen() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private void loginWithEmailAndPassword() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        String emailError = ValidationUtil.isEmailValid(email);
        String passwordError = ValidationUtil.isPasswordValid(password);
        if(emailError == null) {
            if(passwordError == null) {
                attemptLogin(email, password);
            } else {
                etPassword.setError(passwordError);
            }
        } else {
            etEmail.setError(emailError);
        }

    }



    private void attemptLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser loginedUser = mAuth.getCurrentUser();
                    if(loginedUser != null) AppConfig.saveLocalUserAccount(loginedUser);
                    startActivity(new Intent(LogInActivity.this, ContactActivity.class));
                } else {
                    Toast.makeText(LogInActivity.this, "Your Login falied: Password/Emali is not correct", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }





    private void loginWithGoogleSignin() {
        List<AuthUI.IdpConfig> provider = Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(provider)
                .build(), RC_GOOGLE_LOGIN);
    }

    private void loginWithFacebookSignIn() {
        List<AuthUI.IdpConfig> provider = Arrays.asList(new AuthUI.IdpConfig.FacebookBuilder().build());

        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(provider)
                .build(), RC_FACEBOOK_LOGIN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_GOOGLE_LOGIN) {
//            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null) AppConfig.saveLocalUserAccount(user);
                startActivity(new Intent(this, ContactActivity.class));
            } else {
                Log.e(TAG, "Google Login failed");
                Toast.makeText(getApplicationContext(), "Google sign-in failed", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == RC_FACEBOOK_LOGIN) {
            if(resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null) AppConfig.saveLocalUserAccount(user);
                startActivity(new Intent(this, ContactActivity.class));
            } else {
                Log.e(TAG, "Facebook Login failed");
                Toast.makeText(getApplicationContext(), "Facebook Login failed", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
