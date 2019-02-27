/*
 * Made by Bartosz Lewandowski
 * Copyright (c) Lodz, Poland 2019.
 */

package com.bartoszlewandowski.instaclone.social.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bartoszlewandowski.instaclone.R;
import com.bartoszlewandowski.instaclone.consts.Codes;
import com.bartoszlewandowski.instaclone.consts.DatabaseConsts;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class SharePictureTab extends Fragment {

    @BindView(R.id.imgShare)
    ImageView imgShare;
    @BindView(R.id.edtPictureDescription)
    EditText edtPictureDescription;

    Bitmap receivedImageBitmap;

    public SharePictureTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_share_picture_tab, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.imgShare)
    public void imgShareTapped(View v) {
        if (Build.VERSION.SDK_INT >= 23 &&
                ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    Codes.REQUEST_CODE_PERMISSION_TO_EXTERNAL_STORAGE);
        } else {
            getChosenImage();
        }
    }

    private void getChosenImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Codes.REQUEST_CODE_CHOOSE_IMAGE);
    }

    @OnClick(R.id.btnShareImage)
    public void btnShareImageTapped(View v) {
        if (receivedImageBitmap != null) {
            if (edtPictureDescription.getText().toString().equals("")) {
                FancyToast.makeText(getContext(), "Error: You must enter a description",
                        Toast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            } else {
                shareImage();
            }
        } else {
            FancyToast.makeText(getContext(), "Error: You must select an image",
                    Toast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }
    }

    private void shareImage() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading");
        progressDialog.show();
        makeParseObjectToShare().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    FancyToast.makeText(getContext(), "Done",
                            Toast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                } else {
                    FancyToast.makeText(getContext(), "Error:" + e.getMessage(),
                            Toast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    private ParseObject makeParseObjectToShare() {
        ParseObject parseObject = new ParseObject(DatabaseConsts.CLASS_PHOTO);
        parseObject.put(DatabaseConsts.PICTURE_FILE, convertImageBitmapToParseFile());
        parseObject.put(DatabaseConsts.PICTURE_DESCRIPTION, edtPictureDescription.getText().toString());
        parseObject.put(DatabaseConsts.USERNAME, ParseUser.getCurrentUser().getUsername());
        return parseObject;
    }

    private ParseFile convertImageBitmapToParseFile() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        receivedImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return new ParseFile("img.png", bytes);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Codes.REQUEST_CODE_PERMISSION_TO_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getChosenImage();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Codes.REQUEST_CODE_CHOOSE_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    setChosenImageInImageView(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setChosenImageInImageView(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = Objects.requireNonNull(getActivity())
                .getContentResolver()
                .query(Objects.requireNonNull(selectedImage),
                        filePathColumn, null, null, null);
        Objects.requireNonNull(cursor).moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        receivedImageBitmap = BitmapFactory.decodeFile(picturePath);
        imgShare.setImageBitmap(receivedImageBitmap);
    }
}
