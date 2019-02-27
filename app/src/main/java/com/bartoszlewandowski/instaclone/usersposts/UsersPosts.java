/*
 * Made by Bartosz Lewandowski
 * Copyright (c) Lodz, Poland 2019.
 */

package com.bartoszlewandowski.instaclone.usersposts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bartoszlewandowski.instaclone.R;
import com.bartoszlewandowski.instaclone.consts.DatabaseConsts;
import com.bartoszlewandowski.instaclone.social.fragments.UsersTab;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UsersPosts extends AppCompatActivity {

    @BindView(R.id.postsLinearLayout)
    LinearLayout postsLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_posts);
        ButterKnife.bind(this);
        final String receivedUserName = getUserNameFromIntent();
        setTitle(receivedUserName + "'s posts");

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        createParseQueryToFindByUsername(receivedUserName).findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> posts, ParseException e) {
                if (posts.size() > 0 && e == null) {
                    createPosts(posts);
                } else {
                    FancyToast.makeText(UsersPosts.this, receivedUserName + " doesn't have any posts",
                            Toast.LENGTH_LONG, FancyToast.INFO, false).show();
                    finish();
                }
                progressDialog.dismiss();
            }
        });
    }

    private String getUserNameFromIntent() {
        Intent receivedIntentObject = getIntent();
        return receivedIntentObject.getStringExtra(UsersTab.CHOSEN_USERNAME);
    }

    private ParseQuery<ParseObject> createParseQueryToFindByUsername(String receivedUserName) {
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>(DatabaseConsts.CLASS_PHOTO);
        parseQuery.whereEqualTo(DatabaseConsts.USERNAME, receivedUserName);
        parseQuery.orderByDescending(DatabaseConsts.CREATED_AT);
        return parseQuery;
    }

    private void createPosts(List<ParseObject> posts) {
        for (final ParseObject post : posts) {
            ParseFile postPicture = (ParseFile) post.get(DatabaseConsts.PICTURE_FILE);
            Objects.requireNonNull(postPicture).getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (data != null && e == null) {
                        createOnePost(post, data);
                    }
                }
            });
        }
    }

    private void createOnePost(ParseObject post, byte[] data) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        String description = post.get(DatabaseConsts.PICTURE_DESCRIPTION) + "";
        postsLinearLayout.addView(createPostImageViewFromBitmap(bitmap));
        postsLinearLayout.addView(createPostDescriptionTextView(validateDescription(description)));
    }

    private ImageView createPostImageViewFromBitmap(Bitmap bitmap) {
        ImageView postImageView = new ImageView(UsersPosts.this);
        LinearLayout.LayoutParams imageView_params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView_params.setMargins(5, 5, 5, 5);
        postImageView.setLayoutParams(imageView_params);
        postImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        postImageView.setImageBitmap(bitmap);
        return postImageView;
    }

    private TextView createPostDescriptionTextView(String description) {
        TextView postDescription = new TextView(UsersPosts.this);
        LinearLayout.LayoutParams des_params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        des_params.setMargins(5, 5, 5, 15);
        postDescription.setText(description);
        postDescription.setLayoutParams(des_params);
        postDescription.setGravity(Gravity.CENTER);
        postDescription.setBackgroundColor(Color.BLUE);
        postDescription.setTextColor(Color.WHITE);
        postDescription.setTextSize(30f);
        return postDescription;
    }

    private String validateDescription(String description) {
        if (description == null) {
            return "No description";
        } else {
            return description;
        }
    }
}
