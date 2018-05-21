package com.frankegan.verdant.utils

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity

fun <T : ViewModel> AppCompatActivity.obtainViewModel(viewModelClass: Class<T>) =
        ViewModelProviders.of(this).get(viewModelClass)

fun AppCompatActivity.hasPermission(permission: String) =
        PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, permission)