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
import com.duyhoang.happychatapp.Utils.RealTimeDataBaseUtil;
import com.duyhoang.happychatapp.Utils.ValidationUtil;
import com.duyhoang.happychatapp.models.ChattingUser;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import static com.duyhoang.happychatapp.activities.RegisterActivity.RC_GOOGLE_LOGIN;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    public static String TAG = "LogInActivity";

    private EditText etEmail,etPassword;
    private Button btnLogin;
    private SignInButton btnGoogleLogin;
    private LoginButton btnFacebookLogin;
    private TextView txtRegisterNow;

    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        initUI();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_login_login: loginWithEmailAndPassword();
                break;
            case R.id.button_login_google_login: loginWithGoogleSignin();
                break;
            case R.id.text_view_login_register: openRegisterScreen();
                break;
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_GOOGLE_LOGIN) {
            if(resultCode == RESULT_OK) {
                handleWhenLoginSuccessfully();
            } else {
                Log.e(TAG, "Google Login failed");
                Toast.makeText(getApplicationContext(), "Google sign-in failed", Toast.LENGTH_SHORT).show();
            }

        } else {
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void initUI() {
        if(getSupportActionBar() != null) getSupportActionBar().hide();
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
        txtRegisterNow.setOnClickListener(this);

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        btnFacebookLogin.setReadPermissions("email", "public_profile");
        btnFacebookLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                Toast.makeText(LogInActivity.this, "facebook:onError: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

        private void handleFacebookAccessToken(AccessToken token) {
            Log.d(TAG, "handleFacebookAccessToken:" + token);

            AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCredential:success");
                                handleWhenLoginSuccessfully();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }




    private void openRegisterScreen() {
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
                etPassword.setFocusable(true);
            }
        } else {
            etEmail.setError(emailError);
            etEmail.setFocusable(true);
        }

    }



    private void attemptLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    handleWhenLoginSuccessfully();
                } else {
                    Toast.makeText(LogInActivity.this, "Login falied: Password or Email is not correct", Toast.LENGTH_SHORT).show();
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



    private void handleWhenLoginSuccessfully() {
        FirebaseUser loginedUser = mAuth.getCurrentUser();
        if(loginedUser != null) {
            AppConfig.saveLocalUserAccount(loginedUser);
            ChattingUser user = ChattingUser.valueOf(loginedUser);
            RealTimeDataBaseUtil.getInstance().addUserToChatRoom(user);
            RealTimeDataBaseUtil.getInstance().addNewUsertoUsers(user);
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }

    }


}
