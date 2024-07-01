package com.production.monoprix.ui.promotions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.production.monoprix.R
import com.production.monoprix.model.Data
import com.production.monoprix.util.Utils
import kotlinx.android.synthetic.main.item_promotion.view.*

class PromotionaAdapter(private var dataList: List<Data>, private val context: Context) :
    RecyclerView.Adapter<PromotionaAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[holder.adapterPosition], context)
    }

    override fun getItemCount(): Int = dataList.count()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_promotion, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(mList: Data, context: Context) {
            itemView.txt_promo_name.text = mList.productName
            itemView.txt_promo_desc.text = mList.productDesc
                itemView.txt_price.text = context.resources.getString(R.string.Qatarcurrency) + " " + mList.price

        }


    }
}