/*
 * Made by Bartosz Lewandowski
 * Copyright (c) Lodz, Poland 2019.
 */

package com.bartoszlewandowski.instaclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUp extends AppCompatActivity {

    //butterknife implementation
    @BindView(R.id.edtSignUpEmail)
    EditText edtSignUpEmail;
    @BindView(R.id.edtSignUpUsername)
    EditText edtSignUpUsername;
    @BindView(R.id.edtSignUpPassword)
    EditText edtSignUpPassword;
    @BindView(R.id.btnSignUp)
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ButterKnife.bind(this);
        edtSignUpPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    btnSignUpClick(btnSignUp);
                }
                return false;
            }
        });
        if(ParseUser.getCurrentUser() != null){
            startSocialMediaActivity();
        }
    }


    @OnClick(R.id.btnSignUp)
    public void btnSignUpClick(View v) {
        final ParseUser parseUser = new ParseUser();
        parseUser.setEmail(edtSignUpEmail.getText().toString());
        parseUser.setUsername(edtSignUpUsername.getText().toString());
        parseUser.setPassword(edtSignUpPassword.getText().toString());
        if (edtSignUpEmail.getText().toString().equals("") ||
                edtSignUpUsername.getText().toString().equals("") ||
                edtSignUpPassword.getText().toString().equals("")) {
            FancyToast.makeText(SignUp.this, "Email, Username and Password is required!",
                    Toast.LENGTH_LONG, FancyToast.INFO, false).show();
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Signing up " + parseUser.getUsername());
            progressDialog.show();
            parseUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        FancyToast.makeText(SignUp.this, parseUser.getUsername() + " is signed up",
                                Toast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                    } else {
                        FancyToast.makeText(SignUp.this, e.getMessage(),
                                Toast.LENGTH_LONG, FancyToast.ERROR, false).show();
                    }
                    progressDialog.dismiss();
                }
            });
            startSocialMediaActivity();
        }
    }

    @OnClick(R.id.btnGoToLogIn)
    public void startLoginActivity(View v) {
        Intent intent = new Intent(SignUp.this, LoginActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.rootSignUpLayout)
    public void rootLayoutTapped(View v){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        try{
            inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(),0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void startSocialMediaActivity(){
        Intent intent = new Intent(SignUp.this,SocialMediaActivity.class);
        startActivity(intent);
    }
}
