package com.production.monoprix.ui.introscreen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.production.monoprix.R
import com.production.monoprix.ui.home.HomeActivity
import com.production.monoprix.util.SessionManager
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator
import kotlinx.android.synthetic.main.activity_address_list.*
import kotlinx.android.synthetic.main.activity_intro.*
import java.util.*

class IntroScreen : AppCompatActivity(), LastPosClickListener {

    private lateinit var arraylist: ArrayList<String>
    lateinit var session: SessionManager
    var viewPager: ViewPager? = null
    var dot2: SpringDotsIndicator? = null
    var viewAdapter: ViewAdapter? = null
    lateinit var itemClickListener: LastPosClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        session = SessionManager(this)
        viewPager = findViewById(R.id.view_pager);
        dot2 = findViewById(R.id.dot2);
        itemClickListener = this

        //add data to arraylist...
        addDataToArrayList();
        viewAdapter = ViewAdapter(this, arraylist, itemClickListener)
        viewPager?.setAdapter(viewAdapter);
//      viewPager?.setOffscreenPageLimit(1)
        dot2?.setViewPager(viewPager)
        // Assuming you're using the first page initially
        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // Not needed for this implementation
            }

            override fun onPageSelected(position: Int) {
                updateButtonVisibility(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                // Not needed for this implementation
            }
        })
        tvskip.setOnClickListener {
            val i = Intent(this, HomeActivity::class.java)
            i.putExtra("from_splash", true)
            session.freshInstall = "no"
            startActivity(i)
            finish()
        }

        tvstart.setOnClickListener {
            val i = Intent(this, HomeActivity::class.java)
            i.putExtra("from_splash", true)
            session.freshInstall = "no"
            startActivity(i)
            finish()
        }


    }

    private fun updateButtonVisibility(position: Int) {
        if (position == 2) {
            tvskip.visibility = View.GONE
            tvstart.visibility = View.VISIBLE
        } else {
            tvskip.visibility = View.VISIBLE
            tvstart.visibility = View.GONE
        }
    }

    private fun addDataToArrayList() {

        arraylist = ArrayList<String>()
        arraylist.add("Welcome " + getEmoji(0x1F60A))
        arraylist.add("Loyalty App\n\nDiscover a new journey of saving with Monoprix loyalty program.")
        arraylist.add("Scan & Pay\n\nShopping without Queuing, It’s Easy!")
    }

    fun getEmoji(unicode: Int): String {
        return String(Character.toChars(unicode))
    }

    override fun onLastItemClick(pos: Int) {

        Log.d("Position>>>", pos.toString())
        if (pos == 2) {
            tvskip.visibility = View.GONE
            tvstart.visibility = View.VISIBLE

        } else {

            tvskip.visibility = View.VISIBLE
            tvstart.visibility = View.GONE

        }
    }
}