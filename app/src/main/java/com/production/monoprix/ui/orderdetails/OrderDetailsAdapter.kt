package com.production.monoprix.ui.orderdetails

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.production.monoprix.R
import com.production.monoprix.model.Product
import com.production.monoprix.util.Utils
import kotlinx.android.synthetic.main.item_order_detail.view.*
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.collections.ArrayList

class OrderDetailsAdapter(private var dataList: ArrayList<Product>, private val context: Context) :
    RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[holder.adapterPosition], context)
    }

    override fun getItemCount(): Int = dataList.count()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_order_detail, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var actualPrice: Double = 0.0
        private var offerPrice: Double = 0.0
        fun bind(mList: Product, context: Context) {
            itemView.txt_product_name.text = mList.productName
            /*itemView.txt_product_original_price.text =
                context.resources.getString(R.string.Qatarcurrency) + " " + Utils.numbersWithoutLocalization(
                    mList.salesPrice
                )
            itemView.txt_product_sell_price.text =
                context.resources.getString(R.string.Qatarcurrency) + " " + Utils.numbersWithoutLocalization(
                    mList.price
                )*/
            val precision = DecimalFormat("#.##", DecimalFormatSymbols.getInstance(
                Locale.ENGLISH))
            if (mList.pluBarcode) {
                var weigth: Double = mList.price / mList.salesPrice
                itemView.txt_product_sell_price.text =
                    context.resources.getString(R.string.Qatarcurrency) + " " + Utils.numbersWithoutLocalization(
                        mList.price
                    ) + " " + context.resources.getString(R.string.weight) + " " + precision.format(
                        weigth).toString()
            } else {
                itemView.txt_product_sell_price.text =
                    context.resources.getString(R.string.Qatarcurrency) + " " + Utils.numbersWithoutLocalization(
                        mList.price
                    )
            }

            itemView.tvQuantity.text =mList.quantity.toString()
            itemView.txt_product_original_price.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            actualPrice = mList.salesPrice
            offerPrice = mList.price
            val sub = actualPrice - offerPrice
            val res = (sub * 100) / actualPrice
            itemView.txt_you_save.text =
                context.resources.getString(R.string.txt_you_save_qr) + " " + DecimalFormat("##.####").format(
                    sub
                ) + " " + "(" + DecimalFormat("##.####").format(res) + "%" + ")"
/*
            if (mList.salesPrice == mList.price || mList.price.toString()
                    .isEmpty() || mList.price == 0.0
            ) {
                itemView.txt_you_save.visibility = View.INVISIBLE
                itemView.txt_product_original_price.visibility = View.INVISIBLE
            } else {
                itemView.txt_you_save.visibility = View.VISIBLE
                itemView.txt_product_original_price.visibility = View.VISIBLE
            }
*/
            if (mList.promotionFlag && mList.price == 0.0) {
                itemView.txt_free.visibility = View.VISIBLE
                itemView.txt_product_sell_price.visibility = View.GONE
            } else {
                itemView.txt_free.visibility = View.GONE
                itemView.txt_product_sell_price.visibility = View.VISIBLE
            }
        }


    }


}