package com.example.ldp.ireader.utils;

import android.widget.Toast;

import com.example.ldp.ireader.App;

/**
 * Created by ldp on 17-5-11.
 */

public class ToastUtils {

    public static void show(String msg){
        Toast.makeText(App.getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
