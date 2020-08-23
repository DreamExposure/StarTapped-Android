package org.dreamexposure.startapped.utils;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import org.dreamexposure.startapped.R;

public class ViewUtils {

    @SuppressLint("InflateParams")
    public static View smallSpace(FragmentActivity fragment) {
        return LayoutInflater.from(fragment).inflate(R.layout.small_space, null);
    }

    @SuppressLint("InflateParams")
    public static View smallSpace(AppCompatActivity activity) {
        return LayoutInflater.from(activity).inflate(R.layout.small_space, null);
    }
}
