package de.androidcrypto.googledriveplayground.bamusic;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.api.services.drive.DriveScopes;

import de.androidcrypto.googledriveplayground.R;

public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient loginClient;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginClient = createGoogleSignInClient();
        MaterialButton loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(v -> logIn());
    }

    private GoogleSignInClient createGoogleSignInClient() {
        GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        return GoogleSignIn.getClient(LoginActivity.this, googleSignInOptions);
    }

    public void logIn() {
        Log.d(TAG, "login() invoked");
        Intent loginIntent = this.loginClient.getSignInIntent();
        logInActivityResultLauncher.launch(loginIntent);
    }

    ActivityResultLauncher<Intent> logInActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        MainActivity.userAccount = task.getResult(ApiException.class);
                        Log.d(TAG, "Now finish LoginActivity");
                        LoginActivity.this.finish();
                    } catch (ApiException e) {
                        Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                        Utilities.showToast(this, e.getMessage(), Toast.LENGTH_LONG);
                    }
                } else {
                    Log.d(TAG, "GoogleSignIn failed " + result.getResultCode());
                    Utilities.showToast(this, "Google Sign In Canceled", Toast.LENGTH_LONG);
                }
            }
    );
}