package com.production.monoprix.ui.introscreen

import android.content.Context
import android.content.Intent
import androidx.viewpager.widget.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.production.monoprix.R
import androidx.viewpager.widget.ViewPager
import com.production.monoprix.ui.home.HomeActivity
import kotlinx.android.synthetic.main.activity_intro.*
import java.util.ArrayList

class ViewAdapter     //	private Integer[] images={R.drawable.one,R.drawable.two,R.drawable.three,R.drawable.four,R.drawable.five};
    (
    private val context: Context,
    private var arraylist: ArrayList<String>,
    var itemClickListener: LastPosClickListener
) : PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null

    override fun getCount(): Int {
        return arraylist.count()
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(
            Context.LAYOUT_INFLATER_SERVICE
        ) as LayoutInflater
        val view = layoutInflater!!.inflate(R.layout.item, null)
        val imageView = view.findViewById<ImageView>(R.id.image_view)
        val tvtitle = view.findViewById<TextView>(R.id.tvtitle)
        tvtitle.setText(arraylist.get(position))
        val viewPager = container as ViewPager
        viewPager.addView(view, 0)

//        itemClickListener.onLastItemClick(position)

//      Toast.makeText(context,"position :"+position,Toast.LENGTH_SHORT).show()

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val viewPager = container as ViewPager
        val view = `object` as View
        viewPager.removeView(view)
    }
}