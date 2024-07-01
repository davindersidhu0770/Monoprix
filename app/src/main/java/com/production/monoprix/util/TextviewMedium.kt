package com.production.monoprix.util

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.production.monoprix.R


class TextviewMedium(context: Context, attrs: AttributeSet?) : androidx.appcompat.widget.AppCompatTextView(context, attrs) {

    init {
       // this.typeface = Typeface.createFromAsset(context.assets, "font/Roboto-Regular.ttf")
//      this.typeface = Typeface.createFromAsset(context.assets, "SFProDisplay-Regular.ttf")
        this.typeface =(ResourcesCompat.getFont(context, R.font.poppinsmedium))
    }


}