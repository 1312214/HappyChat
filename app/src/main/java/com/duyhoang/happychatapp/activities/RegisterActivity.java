package com.duyhoang.happychatapp.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.google.android.gms.common.SignInButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jaeger.library.StatusBarUtil;

import java.util.Arrays;
import java.util.List;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {


    public static String TAG = "RegisterActivity";
    public static final int RC_GOOGLE_LOGIN = 1001;

    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;

    private EditText etEmail, etPassword, etRetypePassword, etUsername;
    private CheckBox cbAgreedTerms;
    private Button btnRegister;
    private SignInButton btnGoogleLogin;
    private LoginButton btnFacebookLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        initUI();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_register_register:
                if(ConnectionUtil.isAppOnline(this)) {
                    registerEmailPassword();
                } else {
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.button_register_google_login:
                if(ConnectionUtil.isAppOnline(this)) {
                    loginWithGoogleSignin();
                } else {
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_LOGIN) {
            if (resultCode == RESULT_OK) {
                showBusyDialog(null, "Authenticating...");
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initUI() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
            actionBar.setTitle("Registration");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getWindow().setBackgroundDrawableResource(R.drawable.daniel_von);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        etUsername = findViewById(R.id.edit_text_register_username);
        etEmail = findViewById(R.id.edit_text_register_email);
        etPassword = findViewById(R.id.edit_text_register_password);
        etRetypePassword = findViewById(R.id.edit_text_register_confir_password);
        btnRegister = findViewById(R.id.button_register_register);
        btnFacebookLogin = findViewById(R.id.button_register_facebook_login);
        btnGoogleLogin = findViewById(R.id.button_register_google_login);
        cbAgreedTerms = findViewById(R.id.check_box_register_terms);

        btnRegister.setOnClickListener(this);
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
                if (etPassword.length() > 0 && etRetypePassword.length() > 0) {
                    if (!etRetypePassword.getText().toString().equals(etPassword.getText().toString())) {
                        etRetypePassword.setError("Do not match");
                    }
                }


            }
        });


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
                Log.e(TAG, "facebook:onError", error);
                if (!ConnectionUtil.isAppOnline(RegisterActivity.this)) {
                    Toast.makeText(RegisterActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void handleFacebookAccessToken(AccessToken token) {

        Log.d(TAG, "handleFacebookAccessToken:" + token);
        showBusyDialog(null, "Authenticating...");
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


    private void registerEmailPassword() {
        String username = etUsername.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (cbAgreedTerms.isChecked()) {
            if (ValidationUtil.isUsernameValid(username) == null) {
                if (ValidationUtil.isEmailValid(email) == null) {
                    if (ValidationUtil.isPasswordValid(password) == null) {
                        attemptRegister(username, email, password);
                    } else {
                        etPassword.setError(ValidationUtil.isPasswordValid(password));
                    }
                } else {
                    etEmail.setError(ValidationUtil.isEmailValid(email));
                }

            } else {
                etUsername.setError(ValidationUtil.isUsernameValid(username));
            }

        } else {
            cbAgreedTerms.setError("You do not check this");
        }

    }

    private void attemptRegister(final String username, final String email, final String password) {

        showBusyDialog("Please wait...", "Registering");
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail:success, then having been signed in");
                    Toast.makeText(RegisterActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                    handleWhenLoginSuccessfully(username);
                } else if (task.getException() != null) {
                    Log.e(TAG, "createUserWithEmail:failed");
                    task.getException().printStackTrace();
                    Toast.makeText(RegisterActivity.this, "Authentication failed by: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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


    private void handleWhenLoginSuccessfully(String username) {
        dismissBusyDialog();
        FirebaseUser loginedUser = FirebaseAuth.getInstance().getCurrentUser();
        if (loginedUser != null) {
            AppConfig.saveLocalUserAccount(loginedUser);
            ChattingUser user = ChattingUser.valueOf(loginedUser);
            user.setName(username);
            RealTimeDataBaseUtil.getInstance().addUserToChatRoom(user);
            RealTimeDataBaseUtil.getInstance().addNewUsertoUsers(user);
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }

    }

    private void handleWhenLoginSuccessfully() {
        dismissBusyDialog();
        FirebaseUser loginedUser = FirebaseAuth.getInstance().getCurrentUser();
        if (loginedUser != null) {
            AppConfig.saveLocalUserAccount(loginedUser);
            ChattingUser user = ChattingUser.valueOf(loginedUser);
            RealTimeDataBaseUtil.getInstance().addUserToChatRoom(user);
            RealTimeDataBaseUtil.getInstance().addNewUsertoUsers(user);
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }

    }


}
