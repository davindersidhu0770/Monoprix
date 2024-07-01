package com.production.monoprix.ui.navigation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.production.monoprix.R
import com.production.monoprix.model.DrawerModel
import com.production.monoprix.util.SessionManager
import kotlinx.android.synthetic.main.item_drawer.view.*

class DrawerAdapter(private var dataList: ArrayList<DrawerModel>, private val context: Context) :
    RecyclerView.Adapter<DrawerAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[holder.adapterPosition], context)
    }

    override fun getItemCount(): Int = dataList.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_drawer, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var sessionManager: SessionManager
        fun bind(mList: DrawerModel, context: Context) {
            sessionManager = SessionManager(context)
//          if (/*mList.name != context.getString(R.string.myshopping) &&*/ mList.name != context.getString(R.string.mypro) && mList.name != context.getString(R.string.oursh)) {

            if (mList.name == context.getString(R.string.mydeli) ||
                mList.name == context.getString(R.string.shoppinghistory) ||
                mList.name == context.getString(R.string.myaccount) ||
                mList.name == context.getString(R.string.mloyaltyprogram) ||
                mList.name == context.getString(R.string.myrewards)
            ) {

                if (sessionManager.userId.toString().isEmpty()) {
                    itemView.txt_title.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorgray
                        )
                    )
                } else {
                    itemView.txt_title.setTextColor(ContextCompat.getColor(context, R.color.black))
                }
            } else {
                itemView.txt_title.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
            itemView.txt_title.text = mList.getNames()
            itemView.img_navigation.setImageResource(mList.getImages())

        }

    }
}