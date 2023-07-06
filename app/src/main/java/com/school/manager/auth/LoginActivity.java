package com.school.manager.auth;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.school.manager.R;
import com.school.manager.StartActivity;
import com.school.manager.util.ToastHelper;

import static com.school.manager.Application.prefs;

public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    ActivityResultLauncher<Intent> startAccountTask = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getData() != null) {
            GoogleSignInResult loginResult = Auth.GoogleSignInApi.getSignInResultFromIntent(result.getData());
            if (loginResult != null && loginResult.isSuccess()) {
                GoogleSignInAccount account = loginResult.getSignInAccount();
                assert account != null;
                firebaseAuthWithGoogle(account);
            }
        }
    });

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        ToastHelper.show(this, "Google 로그인 실패!", "확인", ToastHelper.LENGTH_SHORT);
                    } else if (mAuth.getCurrentUser() != null) {
                        prefs.edit().putString("UUID", mAuth.getUid()).apply();
                        startActivity(new Intent(this, StartActivity.class));
                        finish();
                    }
                });
    }

    private void login() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startAccountTask.launch(signInIntent);
        } else {
            ToastHelper.show(this, "인터넷 확인 후 재시도 바랍니다.", "확인", ToastHelper.LENGTH_SHORT);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        MaterialButton loginButton = findViewById(R.id.item_login_button);
        loginButton.setOnClickListener(v -> login());
    }
}
