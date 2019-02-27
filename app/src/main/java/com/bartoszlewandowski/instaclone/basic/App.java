/*
 * Made by Bartosz Lewandowski
 * Copyright (c) Lodz, Poland 2019.
 */

package com.bartoszlewandowski.instaclone.basic;

import android.app.Application;

import com.parse.Parse;

public class App extends Application {


    @Override
    public void onCreate(){
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("lGYfTvOctp9A8hn2aTXW5m4xGjJDNBF309gCgkzC")
                .clientKey("HWzGmmGmPomfPcvaCutNZSInNzwRGHsRdj5AHVRI")
                .server("https://parseapi.back4app.com")
                .build()
        );

    }
}
