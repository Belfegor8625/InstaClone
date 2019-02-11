package com.bartoszlewandowski.instaclone;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpLoginActivity extends AppCompatActivity {

    @BindView(R.id.edtUserNameSignUp)
    EditText edtUserNameSignUp;
    @BindView(R.id.edtPasswordSignUp)
    EditText edtPasswordSignUp;
    @BindView(R.id.edtUserNameLogin)
    EditText edtUserNameLogin;
    @BindView(R.id.edtPasswordLogin)
    EditText edtPasswordLogin;
    @BindView(R.id.btnSignUp)
    Button btnSignUp;
    @BindView(R.id.btnLogin)
    Button btnLogin;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_login_activity);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnSignUp)
    public void signUp(View v) {
        final ParseUser parseUser = new ParseUser();
        parseUser.setUsername(edtUserNameSignUp.getText().toString());
        parseUser.setPassword(edtPasswordSignUp.getText().toString());
        parseUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    FancyToast.makeText(SignUpLoginActivity.this, parseUser.get("username") + " signed up successfully",
                            FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();

                    Intent intent = new Intent(SignUpLoginActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                } else {
                    FancyToast.makeText(SignUpLoginActivity.this, e.getMessage(),
                            FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                }
            }
        });
    }

    @OnClick(R.id.btnLogin)
    public void login(View v) {
        ParseUser.logInInBackground(edtUserNameLogin.getText().toString(), edtPasswordLogin.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null && e == null) {
                    FancyToast.makeText(SignUpLoginActivity.this, user.get("username") + " logged in successfully",
                            FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();

                    Intent intent = new Intent(SignUpLoginActivity.this, WelcomeActivity.class);
                    startActivity(intent);

                } else {
                    FancyToast.makeText(SignUpLoginActivity.this, e.getMessage(),
                            FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                }
            }
        });
    }
}
