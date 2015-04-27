package com.mindsmack.ribbit.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mindsmack.ribbit.R;
import com.mindsmack.ribbit.RibbitAplication;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class LoginActivity extends Activity {
    protected TextView mSignUpTextView;
    protected EditText mUserName;
    protected EditText mPassword;
    protected Button mLoginButton;
    public static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_login);

        mSignUpTextView = (TextView) findViewById(R.id.signUpTextView);
        mUserName = (EditText) findViewById(R.id.userNameFieldLogin);
        mPassword = (EditText) findViewById(R.id.passwordFieldLogin);
        mLoginButton = (Button) findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String username = mUserName.getText().toString();
                        String password = mPassword.getText().toString();

                        username = username.trim();// Corta todos los espacios en blanco dentro del nombre.
                        password = password.trim();

                        if (username.isEmpty() || password.isEmpty()) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setTitle(getString(R.string.error_login_titulo_dialogo))
                                    .setMessage(getString(R.string.error_login_mensaje_dialogo))
                                    .setPositiveButton(android.R.string.ok, null);
                            AlertDialog dialogo = builder.create();
                            dialogo.show();

                        } else {
                            setProgressBarIndeterminateVisibility(true);
                            ParseUser.logInInBackground(username, password, new LogInCallback() {
                                @Override
                                public void done(ParseUser parseUser, ParseException e) {
                                    setProgressBarIndeterminateVisibility(false);

                                    if (e == null){
                                        RibbitAplication.updateParseInstallation(ParseUser.getCurrentUser());

                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    } else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder
                                                (LoginActivity.this);
                                        builder.setTitle(getString(R.string.error_login_titulo_dialogo))
                                                .setMessage(e.getMessage())
                                                .setPositiveButton(android.R.string.ok, null);
                                        AlertDialog dialogo = builder.create();
                                        dialogo.show();
                                    }
                                }
                            });
                        }


                    }
                });

        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);}
            });

    }
}
