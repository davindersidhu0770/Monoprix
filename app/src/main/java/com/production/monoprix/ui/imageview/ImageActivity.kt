package com.production.monoprix.ui.imageview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.daimajia.slider.library.Animations.DescriptionAnimation
import com.daimajia.slider.library.SliderLayout
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.DefaultSliderView
import com.production.monoprix.R
import com.production.monoprix.model.BannerModel
import kotlinx.android.synthetic.main.activity_image.*

class ImageActivity : AppCompatActivity() {

    lateinit var textSliderView: DefaultSliderView
    var POSITION : Int = 0
    var mBannerList: MutableList<BannerModel> = java.util.ArrayList<BannerModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        mBannerList = intent.getParcelableArrayListExtra<BannerModel>("LIST") as ArrayList<BannerModel>
        POSITION = intent.getIntExtra("POSITION",0)
        getSliderBanners()
        btn_close.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                finish()
            }

        })
    }


    private fun getSliderBanners() {
        if (banner != null) {
            banner.removeAllSliders()
        }
        for (i in mBannerList.indices) {
            textSliderView = DefaultSliderView(applicationContext)
            textSliderView
                .image(mBannerList[i].image)
                .setScaleType(BaseSliderView.ScaleType.CenterInside)
                .setOnSliderClickListener {
                }
            banner.addSlider(textSliderView)

        }
        // banner.moveNextPosition(true)
        banner.movePrevPosition(false)
        banner.setPresetTransformer(SliderLayout.Transformer.Accordion)
        banner.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
        banner.setCustomAnimation(DescriptionAnimation())
        banner.pagerIndicator.setVisibility(View.VISIBLE)
        banner.pagerIndicator
            .setDefaultIndicatorColor(
                ContextCompat.getColor(applicationContext, R.color.color2),
                ContextCompat.getColor(applicationContext, R.color.gray)
            )
        banner.setDuration(3000)
//        banner.addOnPageChangeListener(this)

//        if (mBannerList.size == 1) {
            banner.stopAutoCycle()
//        } else {
//            banner.startAutoCycle()
//        }

        banner.setCurrentPosition(POSITION)

    }

}