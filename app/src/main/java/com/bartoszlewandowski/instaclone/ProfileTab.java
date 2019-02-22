/*
 * Made by Bartosz Lewandowski
 * Copyright (c) Lodz, Poland 2019.
 */

package com.bartoszlewandowski.instaclone;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import butterknife.BindView;
import butterknife.ButterKnife;


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_tab, container, false);
        ButterKnife.bind(this, view);

        final ParseUser parseUser = ParseUser.getCurrentUser();
        modifyEdtIfNull(parseUser, "profileName", edtProfileName);
        modifyEdtIfNull(parseUser, "profileBio", edtProfileBio);
        modifyEdtIfNull(parseUser, "profileProfession", edtProfileProfession);
        modifyEdtIfNull(parseUser, "profileHobbies", edtProfileHobbies);
        modifyEdtIfNull(parseUser, "profileFavSport", edtProfileFavSport);

        btnUpdateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseUser.put("profileName", edtProfileName.getText().toString());
                parseUser.put("profileBio", edtProfileBio.getText().toString());
                parseUser.put("profileProfession", edtProfileProfession.getText().toString());
                parseUser.put("profileHobbies", edtProfileHobbies.getText().toString());
                parseUser.put("profileFavSport", edtProfileFavSport.getText().toString());
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Updating info...");
                progressDialog.show();
                parseUser.saveInBackground(new SaveCallback() {
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
        });
        return view;
    }

    private void modifyEdtIfNull(ParseUser parseUser, String key, EditText edt) {
        if (parseUser.get(key) == null) {
            edt.setText("");
        } else {
            edt.setText(parseUser.get(key).toString());
        }
    }

}
