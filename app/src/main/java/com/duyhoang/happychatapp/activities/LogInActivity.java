package com.duyhoang.happychatapp.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.duyhoang.happychatapp.AppConfig;
import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.utils.ConnectionUtil;
import com.duyhoang.happychatapp.utils.RealTimeDataBaseUtil;
import com.duyhoang.happychatapp.utils.ValidationUtil;
import com.duyhoang.happychatapp.models.ChattingUser;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.jaeger.library.StatusBarUtil;

import java.util.Arrays;
import java.util.List;

import static com.duyhoang.happychatapp.activities.RegisterActivity.RC_GOOGLE_LOGIN;

public class LogInActivity extends BaseActivity implements View.OnClickListener {

    public static String TAG = "LogInActivity";

    private EditText etEmail,etPassword;
    private Button btnLogin;
    private SignInButton btnGoogleLogin;
    private LoginButton btnFacebookLogin;
    private TextView txtRegisterNow;

    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StatusBarUtil.setTranslucent(this, 10);
        mAuth = FirebaseAuth.getInstance();
        initUI();

        if(!ConnectionUtil.isAppOnline(this)) {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG, "Error: " + connectionResult.getErrorCode() + " - " + connectionResult.getErrorMessage());
                        Toast.makeText(LogInActivity.this, "Connection Failed: " + connectionResult.getErrorCode() + " - " + connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();



    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_login_login:
                if(ConnectionUtil.isAppOnline(this)) {
                    loginWithEmailAndPassword();
                } else {
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.button_login_google_login:
                if(ConnectionUtil.isAppOnline(this)) {
                    loginWithGoogleSignin();
                } else {
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.text_view_login_register: openRegisterScreen();
                break;
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_GOOGLE_LOGIN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this, "Google sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                // ...
            }

        } else {
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void initUI() {
        getWindow().setBackgroundDrawableResource(R.drawable.casey_horner);
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
        btnFacebookLogin.setOnClickListener(this);

        mCallbackManager = CallbackManager.Factory.create();
        btnFacebookLogin.setReadPermissions("email", "public_profile");
        btnFacebookLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                showBusyDialog(null, "Authenticating...");
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "facebook:onError", error);
                if(!ConnectionUtil.isAppOnline(LogInActivity.this)) {
                    Toast.makeText(LogInActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
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
        showBusyDialog(null, "Logging In");
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
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_LOGIN);
    }



    private void handleWhenLoginSuccessfully() {
        dismissBusyDialog();
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


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        showBusyDialog(null, "Authenticating...");

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
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
                            Snackbar.make(findViewById(android.R.id.content), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();

                        }

                    }
                });
    }

}
