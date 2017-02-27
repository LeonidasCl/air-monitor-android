package com.seu.seuairmonitor;

/**
 * Created by pc on 2016/2/6.
 */

import android.app.*;
import android.os.*;

import android.widget.*;
import android.content.Intent;
import android.widget.TextView;
import android.view.*;
import com.kot32.ksimpleframeworklibrary.R;
import com.kot32.ksimplelibrary.cache.ACache;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.net.URL;
import  java.net.*;
import java.io.*;


public class UserInfoActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_info);
        ////////
        ACache mCache = ACache.get(UserInfoActivity.this);
        String name = mCache.getAsString("username");
        String email=mCache.getAsString("email");
        String authority="基本权限";/////////////待修改
        String location="NanJing";
        TextView nameTxt = (TextView) findViewById(R.id.detail_account);
        TextView emailTxt = (TextView) findViewById(R.id.detail_email);
        TextView authorityTxt = (TextView) findViewById(R.id.detail_authority);
        TextView locationTxt = (TextView) findViewById(R.id.detail_location);
        nameTxt.setText(name);
        emailTxt.setText(email);
        authorityTxt.setText(authority);
        locationTxt.setText(location);

    }



}