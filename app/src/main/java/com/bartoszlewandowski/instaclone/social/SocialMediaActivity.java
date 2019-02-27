/*
 * Made by Bartosz Lewandowski
 * Copyright (c) Lodz, Poland 2019.
 */

package com.bartoszlewandowski.instaclone.social;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bartoszlewandowski.instaclone.R;
import com.bartoszlewandowski.instaclone.consts.Codes;
import com.bartoszlewandowski.instaclone.consts.DatabaseConsts;
import com.bartoszlewandowski.instaclone.social.adapters.TabAdapter;
import com.bartoszlewandowski.instaclone.registration.SignUpActivity;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SocialMediaActivity extends AppCompatActivity {

    @BindView(R.id.my_toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    Bitmap chosenImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.postImageItem) {
            if (Build.VERSION.SDK_INT >= 23 &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Codes.REQUEST_CODE_PERMISSION_TO_EXTERNAL_STORAGE_DIRECT_LOAD);
            } else {
                captureImage();
            }
        } else if (item.getItemId() == R.id.logoutUserItem) {
            ParseUser.getCurrentUser().logOut();
            startSocialActivity();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void startSocialActivity() {
        Intent intent = new Intent(SocialMediaActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Codes.REQUEST_CODE_PERMISSION_TO_EXTERNAL_STORAGE_DIRECT_LOAD) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            }
        }
    }

    private void captureImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Codes.REQUEST_CODE_CHOOSE_IMAGE_DIRECT_UPLOAD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent chosenImageIntent) {
        super.onActivityResult(requestCode, resultCode, chosenImageIntent);
        if (requestCode == Codes.REQUEST_CODE_CHOOSE_IMAGE_DIRECT_UPLOAD &&
                resultCode == RESULT_OK && chosenImageIntent != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading");
            progressDialog.show();
            makeParseObjectToShare(chosenImageIntent).saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        FancyToast.makeText(SocialMediaActivity.this, "Picture uploaded",
                                Toast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                    } else {
                        FancyToast.makeText(SocialMediaActivity.this, "Error: " + e.getMessage(),
                                Toast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                    }
                    progressDialog.dismiss();
                }
            });

        }
    }

    private ParseObject makeParseObjectToShare(Intent chosenImageIntent) {
        ParseObject parseObject = new ParseObject(DatabaseConsts.CLASS_PHOTO);
        parseObject.put("picture", Objects.requireNonNull(convertImageBitmapToParseFile(chosenImageIntent)));
        parseObject.put("username", ParseUser.getCurrentUser().getUsername());
        return parseObject;
    }

    private ParseFile convertImageBitmapToParseFile(Intent chosenImageIntent) {
        try {
            Uri capturedImage = chosenImageIntent.getData();
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), capturedImage);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            return new ParseFile("img.png", bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
