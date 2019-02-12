package com.bartoszlewandowski.instaclone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUp extends AppCompatActivity {

    //butterknife implementation
    @BindView(R.id.edtSignUpEmail)
    EditText edtEmail;
    @BindView(R.id.edtSignUpUsername)
    EditText edtUsername;
    @BindView(R.id.edtSignUpPassword)
    EditText edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ButterKnife.bind(this);
    }


    @OnClick(R.id.btnSignUp)
    public void btnSignUpClick(View v) {
        final ParseUser parseUser = new ParseUser();
        parseUser.setEmail(edtEmail.getText().toString());
        parseUser.setUsername(edtUsername.getText().toString());
        parseUser.setPassword(edtPassword.getText().toString());
        parseUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    FancyToast.makeText(SignUp.this, parseUser.getUsername() + " is signed up",
                            Toast.LENGTH_LONG, FancyToast.SUCCESS, false);
                }else{
                    FancyToast.makeText(SignUp.this, e.getMessage(),
                            Toast.LENGTH_LONG, FancyToast.ERROR, false);
                }
            }
        });
    }

    @OnClick(R.id.btnGoToLogIn)
    public void startLoginActivity(View v) {
        Intent intent = new Intent(SignUp.this, LoginActivity.class);
        startActivity(intent);
    }
}
