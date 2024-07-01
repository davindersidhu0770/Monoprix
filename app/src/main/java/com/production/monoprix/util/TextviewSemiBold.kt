package com.production.monoprix.util


import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.production.monoprix.R

/*
 * Created by Harris on 12/2/2019.
 */

class TextviewSemiBold(context: Context, attrs: AttributeSet?) : androidx.appcompat.widget.AppCompatTextView(context, attrs) {

    init {
        //this.typeface = Typeface.createFromAsset(context.assets, "font/Roboto-Bold.ttf")
        this.typeface =(ResourcesCompat.getFont(context, R.font.poppinssemibold))
//        this.typeface = Typeface.createFromAsset(context.assets, "SF-Pro-Display-Medium.otf")

    }
}

