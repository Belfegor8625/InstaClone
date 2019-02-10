package com.bartoszlewandowski.instaclone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUp extends AppCompatActivity {

    //butterknife implementation
    @BindView(R.id.btnSave)
    Button btnSave;
    @BindView(R.id.edtName)
    EditText edtName;
    @BindView(R.id.edtPunchSpeed)
    EditText edtPunchSpeed;
    @BindView(R.id.edtPunchPower)
    EditText edtPunchPower;
    @BindView(R.id.edtKickSpeed)
    EditText edtKickSpeed;
    @BindView(R.id.edtKickPower)
    EditText edtKickPower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ButterKnife.bind(this);
    }


    @OnClick(R.id.btnSave)
    public void btnSaveClick(View v) {

        try {
            final ParseObject kickboxer = new ParseObject("kickboxer");
            kickboxer.put("name", edtName.getText().toString());
            kickboxer.put("punchSpeed", Integer.parseInt(edtPunchSpeed.getText().toString()));
            kickboxer.put("punchPower", Integer.parseInt(edtPunchPower.getText().toString()));
            kickboxer.put("kickSpeed", Integer.parseInt(edtKickSpeed.getText().toString()));
            kickboxer.put("kickPower", Integer.parseInt(edtKickPower.getText().toString()));
            kickboxer.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        FancyToast.makeText(SignUp.this, kickboxer.get("name")+ " is saved",
                                FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();
                    } else {
                        FancyToast.makeText(SignUp.this, e.getMessage(),
                                FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
                    }
                }
            });
        }catch (Exception e){
            FancyToast.makeText(SignUp.this, e.getMessage(),
                    FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
        }
    }
}