/*
 * Made by Bartosz Lewandowski
 * Copyright (c) Lodz, Poland 2019.
 */

package com.bartoszlewandowski.instaclone.social.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bartoszlewandowski.instaclone.R;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileTab extends Fragment {


    @BindView(R.id.edtProfileName)
    EditText edtProfileName;
    @BindView(R.id.edtProfileBio)
    EditText edtProfileBio;
    @BindView(R.id.edtProfileProfession)
    EditText edtProfileProfession;
    @BindView(R.id.edtProfileHobbies)
    EditText edtProfileHobbies;
    @BindView(R.id.edtProfileFavSport)
    EditText edtProfileFavSport;
    @BindView(R.id.btnUpdateInfo)
    Button btnUpdateInfo;


    public ProfileTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_tab, container, false);
        ButterKnife.bind(this, view);
        checkCurrentUserData();
        return view;
    }

    private void checkCurrentUserData(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        modifyEdtIfParseUserKeyIsNull(currentUser, "profileName", edtProfileName);
        modifyEdtIfParseUserKeyIsNull(currentUser, "profileBio", edtProfileBio);
        modifyEdtIfParseUserKeyIsNull(currentUser, "profileProfession", edtProfileProfession);
        modifyEdtIfParseUserKeyIsNull(currentUser, "profileHobbies", edtProfileHobbies);
        modifyEdtIfParseUserKeyIsNull(currentUser, "profileFavSport", edtProfileFavSport);
    }

    private void modifyEdtIfParseUserKeyIsNull(ParseUser parseUser, String key, EditText edt) {
        if (parseUser.get(key) == null) {
            edt.setText("");
        } else {
            edt.setText(Objects.requireNonNull(parseUser.get(key)).toString());
        }
    }

    @OnClick(R.id.btnUpdateInfo)
    public void onBtnUpdateInfoClick(View v) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        prepareUserDataToSave(currentUser);
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Updating info...");
        progressDialog.show();
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    FancyToast.makeText(getContext(), "Info updated", Toast.LENGTH_SHORT,
                            FancyToast.INFO, false).show();
                } else {
                    FancyToast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT,
                            FancyToast.ERROR, false).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    private void prepareUserDataToSave(ParseUser user){
        user.put("profileName", edtProfileName.getText().toString());
        user.put("profileBio", edtProfileBio.getText().toString());
        user.put("profileProfession", edtProfileProfession.getText().toString());
        user.put("profileHobbies", edtProfileHobbies.getText().toString());
        user.put("profileFavSport", edtProfileFavSport.getText().toString());
    }

}
