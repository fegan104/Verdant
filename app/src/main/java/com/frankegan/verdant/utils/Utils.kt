package com.frankegan.verdant.utils

import android.os.Build

/**
 * Created by frankegan on 3/4/18.
 */
fun lollipop (block: () -> Unit)  {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
        run(block)
    }
}
fun prelollipop (block: () -> Unit)  {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
        run(block)
    }
}