package com.production.monoprix.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.production.monoprix.R
import com.production.monoprix.model.HomeModel
import kotlinx.android.synthetic.main.item_homepage.view.*


class HomeAdapter(private var dataList: List<HomeModel>, private val context: Context) :
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[holder.adapterPosition], context)
    }

    override fun getItemCount(): Int = dataList.count()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_homepage, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(mList: HomeModel, context: Context) {
            itemView.txt_heading.text = mList.name
            itemView.ivone.setImageDrawable(mList.icon);
//            itemView.c1.setBackgroundColor(mList.color)
            if (mList.isLogin) {
                itemView.img_lock.visibility = View.VISIBLE
            } else {
                itemView.img_lock.visibility = View.GONE
            }
        }


    }
}