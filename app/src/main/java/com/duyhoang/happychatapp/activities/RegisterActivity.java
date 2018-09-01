package com.duyhoang.happychatapp.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    // Add busy dialog to attemptRegister
    public static String TAG = "RegisterActivity";
    public static final int RC_GOOGLE_LOGIN = 1001;
    public static final int RC_FACEBOOK_LOGIN = 1002;

    private FirebaseAuth mAuth;

    private EditText etEmail, etPassword, etRetypePassword;
    private CheckBox cbAgreedTerms;
    private Button btnRegister;
    private SignInButton btnGoogleLogin;
    private LoginButton btnFBLogin;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        initUI();
    }


    private void initUI() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        actionBar.setTitle("Registration");
        actionBar.setDisplayHomeAsUpEnabled(true);

        getWindow().setBackgroundDrawableResource(R.drawable.background_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        etEmail =  findViewById(R.id.edit_text_register_email);
        etPassword =  findViewById(R.id.edit_text_register_password);
        etRetypePassword =  findViewById(R.id.edit_text_register_confir_password);
        btnRegister =  findViewById(R.id.button_register_register);
        btnFBLogin =   findViewById(R.id.button_register_facebook_login);
        btnGoogleLogin =  findViewById(R.id.button_register_google_login);
        cbAgreedTerms =  findViewById(R.id.check_box_register_terms);

        btnRegister.setOnClickListener(this);
        btnFBLogin.setOnClickListener(this);
        btnGoogleLogin.setOnClickListener(this);

        etRetypePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(etPassword.length() > 0 && etRetypePassword.length() > 0) {
                    if(!etRetypePassword.getText().toString().equals(etPassword.getText().toString())){
                        etRetypePassword.setError("Do not match");
                    }
                }


            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_register_register: registerEmailPassword();
                break;
            case R.id.button_register_facebook_login: loginWithFacebookSignIn();
                break;
            case R.id.button_register_google_login: loginWithGoogleSignin();
                break;
        }
    }



    private void registerEmailPassword() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();


        if(cbAgreedTerms.isChecked()) {
            if(ValidationUtil.isEmailValid(email) == null) {
                if(ValidationUtil.isPasswordValid(password) == null) {
                    attemptRegister(email, password);
                } else {
                    etPassword.setError(ValidationUtil.isPasswordValid(password));
                }
            } else {
                etEmail.setError(ValidationUtil.isEmailValid(email));
            }
        } else {
            cbAgreedTerms.setError("You do not check this");
        }


    }

    private void attemptRegister(final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "createUserWithEmail:success");
                FirebaseUser user = mAuth.getCurrentUser();
                if(user != null) {
                    AppConfig.saveLocalUserAccount(user);
                    loginAccount(email, password);
                }else {
                    Log.e(TAG, "createUserWithEmail:failed");
                    if(task.getException() != null){
                        task.getException().printStackTrace();
                        Toast.makeText(getApplicationContext(), "Authentication failed by: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }


    private void loginAccount(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    startActivity(new Intent(RegisterActivity.this, ContactActivity.class));
                } else {
                    Log.e(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
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
