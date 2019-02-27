/*
 * Made by Bartosz Lewandowski
 * Copyright (c) Lodz, Poland 2019.
 */

package com.bartoszlewandowski.instaclone.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bartoszlewandowski.instaclone.R;
import com.bartoszlewandowski.instaclone.social.SocialMediaActivity;
import com.bartoszlewandowski.instaclone.registration.SignUpActivity;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.edtLoginEmailOrUsername)
    EditText edtLoginEmailOrUsername;
    @BindView(R.id.edtLoginPassword)
    EditText edtLoginPassword;
    @BindView(R.id.btnLogin)
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        if (ParseUser.getCurrentUser() != null) {
            ParseUser.logOutInBackground();
        }
        registerOnEnterListenerInPassword();
    }

    private void registerOnEnterListenerInPassword() {
        edtLoginPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    onCLickLogIn(btnLogin);
                }
                return false;
            }
        });
    }


    @OnClick(R.id.btnLogin)
    public void onCLickLogIn(View v) {
        if (edtLoginEmailOrUsername.getText().toString().equals("") ||
                edtLoginPassword.getText().toString().equals("")) {
            FancyToast.makeText(LoginActivity.this, "Email and Password is required!",
                    Toast.LENGTH_LONG, FancyToast.ERROR, false).show();
        } else {
            logIn(edtLoginEmailOrUsername.getText().toString(), edtLoginPassword.getText().toString());
        }
    }

    private void logIn(String email, String password) {
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null && e == null) {
                    FancyToast.makeText(LoginActivity.this, user.getUsername() + " logged in",
                            Toast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                    startSocialMediaActivity();
                } else {
                    FancyToast.makeText(LoginActivity.this, e.getMessage(),
                            Toast.LENGTH_LONG, FancyToast.ERROR, false).show();
                }
            }
        });
    }

    @OnClick(R.id.btnGoToSignUp)
    public void startSignUpActivity(View v) {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.rootLoginLayout)
    public void rootLoginLayoutClick(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startSocialMediaActivity() {
        Intent intent = new Intent(LoginActivity.this, SocialMediaActivity.class);
        startActivity(intent);
        finish();
    }
}
