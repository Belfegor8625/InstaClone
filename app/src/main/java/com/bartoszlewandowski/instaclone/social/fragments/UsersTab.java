/*
 * Made by Bartosz Lewandowski
 * Copyright (c) Lodz, Poland 2019.
 */

package com.bartoszlewandowski.instaclone.social.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bartoszlewandowski.instaclone.R;
import com.bartoszlewandowski.instaclone.consts.DatabaseConsts;
import com.bartoszlewandowski.instaclone.usersposts.UsersPosts;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersTab extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.txtLoadingUsers)
    TextView txtLoadingUsers;

    private ArrayList<String> arrayList;
    private ArrayAdapter<String> arrayAdapter;

    public static final String CHOSEN_USERNAME = "username";
    public UsersTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users_tab, container, false);
        ButterKnife.bind(this, view);
        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                android.R.layout.simple_list_item_1, arrayList);
        listView.setOnItemClickListener(UsersTab.this);
        listView.setOnItemLongClickListener(UsersTab.this);
        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.whereNotEqualTo(DatabaseConsts.USERNAME, ParseUser.getCurrentUser().getUsername());
        parseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    if (users.size() > 0) {
                        for (ParseUser user : users) {
                            arrayList.add(user.getUsername());
                        }
                        listView.setAdapter(arrayAdapter);
                        hideTextViewAndShowListView();
                    }
                }
            }
        });
        return view;
    }

    private void hideTextViewAndShowListView() {
        txtLoadingUsers.animate().alpha(0).setDuration(2000);
        listView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(), UsersPosts.class);
        intent.putExtra(CHOSEN_USERNAME, arrayList.get(position));
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        showUserDetailsFromListViewInPosition(position);
        return true;
    }

    private void showUserDetailsFromListViewInPosition(int position) {
        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.whereEqualTo("username", arrayList.get(position));
        parseQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null && e == null) {
                    createPrettyDialogWithChosenUserDetails(user);
                }
            }
        });
    }

    private void createPrettyDialogWithChosenUserDetails(ParseUser user) {
        final PrettyDialog prettyDialog = new PrettyDialog(Objects.requireNonNull(getContext()));
        prettyDialog.setTitle(user.getUsername() + " 's Info")
                .setMessage(user.get("profileBio") + "\n"
                        + user.get("profileProfession") + "\n"
                        + user.get("profileHobbies") + "\n"
                        + user.get("profileFavSport"))
                .setIcon(R.drawable.person)
                .addButton("OK", R.color.pdlg_color_white, R.color.pdlg_color_green,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                prettyDialog.dismiss();
                            }
                        }).show();
    }
}
