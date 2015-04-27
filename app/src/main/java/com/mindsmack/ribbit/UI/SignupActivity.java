package com.mindsmack.ribbit.UI;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.mindsmack.ribbit.R;
import com.mindsmack.ribbit.RibbitAplication;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class SignupActivity extends ActionBarActivity {
    protected EditText mUserName;
    protected EditText mPassword;
    protected EditText mEmail;
    protected Button mSignUpButton;
    protected Button mBotonCancelar;
    public static final String TAG = SignupActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_signup);




        mUserName = (EditText) findViewById(R.id.usernameFieldSignup);
        mPassword = (EditText) findViewById(R.id.passwordFieldLogin);
        mEmail = (EditText) findViewById(R.id.emailField);
        mSignUpButton = (Button) findViewById(R.id.signUpButton);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUserName.getText().toString();
                String password = mPassword.getText().toString();
                String email = mEmail.getText().toString();

                username= username.trim();// Corta todos los espacios en blanco dentro del nombre.
                password= password.trim();
                email= email.trim();

                if (username.isEmpty() || password.isEmpty() || email.isEmpty()){

                    AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                    builder.setTitle(getString(R.string.dialogo_error_ingreso_datos_signup_titulo))
                            .setMessage(getString(R.string.dialogo_error_ingreso_signup_mensaje))
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialogo = builder.create();
                    dialogo.show();

                } else {
                    setProgressBarIndeterminateVisibility(true);
                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            setProgressBarIndeterminateVisibility(false);

                            if (e == null ){

                                RibbitAplication.updateParseInstallation(ParseUser.getCurrentUser());

                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                                builder.setTitle(getString(R.string.dialogo_error_ingreso_datos_signup_titulo))
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

        mBotonCancelar = (Button) findViewById(R.id.botonCancelar);
        mBotonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }



}
