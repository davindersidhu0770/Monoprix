package com.production.monoprix.ui.barcodescanner

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.production.monoprix.R
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.room.AppDatabase
import com.production.monoprix.model.ProductByCodeModel
import com.production.monoprix.model.StatusModel
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import kotlinx.android.synthetic.main.item_product_list.view.*
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*


class ScanProductDetailsAdapter(
    private val context: Context,
    private var data: List<ProductByCodeModel>,
    val cart_top_total: TextView,
    val txt_cart_count: TextView,
    val recycler_scan_cart_lits: RecyclerView,
    val rele_scan_pay: RelativeLayout,
    val title: String
) :
    RecyclerView.Adapter<ScanProductDetailsAdapter.ViewHolder>() {

    lateinit var session: SessionManager
    var quantity: Int = 1
    var in_qty: Int = 0
    private var cartTotal: Int = 0
    private var productTotal: Double = 0.0
    lateinit var mHolder: ViewHolder
    lateinit var database: AppDatabase

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        session = SessionManager(context)
        database = AppDatabase(context)
        holder.bind(
            data[holder.adapterPosition],
            context,
            data,
            session,
            txt_cart_count,
            cart_top_total
        )
        var manualPrice: Double = data.get(position).manual_price.toDouble()
        var salesPrice: Double = data.get(position).salesPrice.toDouble()
        val precision = DecimalFormat("#.##", DecimalFormatSymbols.getInstance(
                Locale.ENGLISH))
        Log.e("price_check", "manualPrice= " + manualPrice + " salesPrice= " + salesPrice)
        holder.itemView.txt_discount_price.setTextColor(
            context.getResources().getColor(R.color.black)
        );
        if (data.get(position).pluBarcode) {
            var weigth: Double = manualPrice / salesPrice
            holder.itemView.img_cart_plus.isEnabled = false
            holder.itemView.img_cart_minus.isEnabled = false
            holder.itemView.img_cart_plus.isClickable =false
            holder.itemView.img_cart_minus.isClickable =false
            holder.itemView.txt_discount_price.text =
                context.resources.getString(R.string.Qatarcurrency) + " " + precision.format(manualPrice)
                    .toString() + " " + context.resources.getString(R.string.weight) + " " + precision.format(
                    weigth).toString()
        } else {
            holder.itemView.img_cart_plus.isEnabled = true
            holder.itemView.img_cart_minus.isEnabled = true
            holder.itemView.img_cart_plus.isClickable =true
            holder.itemView.img_cart_minus.isClickable =true
            holder.itemView.txt_discount_price.text =
                context.resources.getString(R.string.Qatarcurrency) + " " + precision.format(
                    salesPrice
                ).toString()
        }

        holder.itemView.img_cart_plus.setOnClickListener(View.OnClickListener {
            AddUpdateShowDialog(2, holder, position)
            quantity = data[holder.adapterPosition].quantity
            /* if (quantity < Integer.parseInt(data[holder.adapterPosition].totalQuantity)) {*/
            quantity += 1
            database.todoDao().update(quantity, data[holder.adapterPosition].pluCode)
            holder.itemView.txt_cart_quant.text = quantity.toString()
            data[holder.adapterPosition].quantity = quantity

            data[holder.adapterPosition].productTotal =
                (data[holder.adapterPosition].salesPrice).toDouble() * data[holder.adapterPosition].quantity.toDouble()

            mHolder = holder
            getAmountCalculated(data[holder.adapterPosition].quantity)

            /*  } else {
                  Toast.makeText(context, "Quantity limit reached.", Toast.LENGTH_SHORT).show()
              }*/
        })


        holder.itemView.img_cart_minus.setOnClickListener(View.OnClickListener {
            if (data[holder.adapterPosition].quantity > 1) {
                AddUpdateShowDialog(1, holder, position)
            }
            quantity = data[holder.adapterPosition].quantity
            if (quantity > 1) {
                in_qty = quantity - 1
                quantity = in_qty
                holder.itemView.txt_cart_quant.text = quantity.toString()
                database.todoDao().update(quantity, data[holder.adapterPosition].pluCode)
                data[holder.adapterPosition].quantity = in_qty
                data[holder.adapterPosition].productTotal =
                    (data[holder.adapterPosition].salesPrice).toDouble() * data.get(holder.adapterPosition).quantity.toDouble()
                mHolder = holder
                getDecreaseAmount(data[holder.adapterPosition].quantity)

            } else {
                holder.itemView.txt_cart_quant.text = (data[position].quantity).toString()
            }
        })

        holder.itemView.rel_cart_delete.setOnClickListener {

            val dialogBuilder = android.app.AlertDialog.Builder(context)
//            dialogBuilder.setTitle(title)
            dialogBuilder.setMessage(context.getString(R.string.del_msg))
                .setCancelable(false)
                .setPositiveButton(
                    context.getString(R.string.ok),
                    DialogInterface.OnClickListener { dialog, id ->
                        cartTotal = data.size
                        productTotal = 0.0
                        for (i in data.indices) {
                            productTotal += data.get(i).productTotal
                        }
                        cart_top_total.text =
                            context.resources.getString(R.string.Qatarcurrency) + " " + productTotal.toString()
                        var cartItemId = data.get(holder.adapterPosition).cartItemId
                        if (cartItemId == 0) {
                            database.todoDao().deleteProduct(data[holder.adapterPosition].pluCode)
                            if (data.isEmpty()) {
                                txt_cart_count.text = data.size.toString()
                                session.totalCart = data.size.toString()
                                session.subTotal = ""
                                cart_top_total.text =
                                    context.resources.getString(R.string.model_qr_price)
                                recycler_scan_cart_lits.visibility = View.GONE
                                rele_scan_pay.visibility = View.GONE
                            }
                            (context as BarcodeScannerActivity).fetchWeatherDataFromDb()
                        }else{
                            mHolder = holder
                            Utils.showProgressDialog(context, true)
                            cartDelete(""+cartItemId)
                        }

                    })
                .setNegativeButton(
                    context.getString(R.string.cancel),
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                    })
            val alert = dialogBuilder.create()
            alert.show()


        }
    }

    fun AddUpdateShowDialog(i: Int, holder: ViewHolder, position: Int) {
        val dialog = Dialog(context, R.style.MyDialogTheme1)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dailog_quntity_update)
        val dialogtext: TextView = dialog.findViewById(R.id.tv_dsc) as TextView
        val dialogButton: RelativeLayout = dialog.findViewById(R.id.rl_mainview) as RelativeLayout
        dialogtext.setText(context.getString(R.string.dailog_update_qnty))

        Handler().postDelayed({
            dialog.dismiss()
        }, 1000)
        dialogButton.setOnClickListener(View.OnClickListener {
//            when(i) {
//
//            1 -> {//minus
//                quantity = data[holder.adapterPosition].quantity
//                if (quantity > 1) {
//                    in_qty = quantity - 1
//                    quantity = in_qty
//                    holder.itemView.txt_cart_quant.text = quantity.toString()
//                    database.todoDao().update(quantity, data[holder.adapterPosition].productId)
//                    data[holder.adapterPosition].quantity = in_qty
//                    data[holder.adapterPosition].productTotal =
//                        (data[holder.adapterPosition].salesPrice).toDouble() * data.get(holder.adapterPosition).quantity.toDouble()
//                    mHolder = holder
//                    getDecreaseAmount(data[holder.adapterPosition].quantity)
//
//                } else {
//                    holder.itemView.txt_cart_quant.text = (data[position].quantity).toString()
//                }
//            }
//
//            2 -> {//plus
//                quantity = data[holder.adapterPosition].quantity
//                /* if (quantity < Integer.parseInt(data[holder.adapterPosition].totalQuantity)) {*/
//                quantity += 1
//                database.todoDao().update(quantity, data[holder.adapterPosition].productId)
//                holder.itemView.txt_cart_quant.text = quantity.toString()
//                data[holder.adapterPosition].quantity = quantity
//
//                data[holder.adapterPosition].productTotal =
//                    (data[holder.adapterPosition].salesPrice).toDouble() * data[holder.adapterPosition].quantity.toDouble()
//
//                mHolder = holder
//                getAmountCalculated(data[holder.adapterPosition].quantity)
//            }
//        }
            dialog.dismiss()
        }
        )
        dialog.show()
    }

    override fun getItemCount(): Int = data.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_product_list, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var actualPrice: Double = 0.0
        private var offerPrice: Double = 0.0
        private var cartTotal: Double = 0.0
        fun bind(
            mList: ProductByCodeModel,
            context: Context,
            data: List<ProductByCodeModel>,
            sessionManager: SessionManager,
            txt_cart_count: TextView,
            cart_top_total: TextView
        ) {

            if (mList.quantity == 0) {
                mList.quantity = 1
            }
            itemView.txt_product_title.text = mList.r_name
//            itemView.txt_discount_price.text = context.resources.getString(R.string.Qatarcurrency) + " " + mList.manual_price
           /* itemView.txt_original_price.text =
                context.resources.getString(R.string.Qatarcurrency) + " " + mList.salesPrice
            itemView.txt_original_price.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG*/
            if (mList.salesPrice == mList.manual_price || mList.manual_price.isEmpty() || mList.manual_price.contains(
                    "0.00",
                    true
                )
            ) {
                itemView.txt_you_save.visibility = View.INVISIBLE
                itemView.txt_original_price.visibility = View.INVISIBLE
//                itemView.txt_discount_price.text = context.resources.getString(R.string.Qatarcurrency) + " " + mList.salesPrice
            } else {
              //  itemView.txt_you_save.visibility = View.VISIBLE
                //itemView.txt_original_price.visibility = View.VISIBLE
            }
            itemView.txt_cart_quant.text = mList.quantity.toString()
            sessionManager.totalCart = data.size.toString()
            actualPrice = mList.salesPrice.toDouble()
            offerPrice = mList.manual_price.toDouble()

            val sub = actualPrice - offerPrice
            val res = (sub * 100) / actualPrice
           /* itemView.txt_you_save.text =
                context.resources.getString(R.string.txt_you_save_qr) + " " + DecimalFormat("##.####").format(
                    sub
                ) + " " + "(" + DecimalFormat("##.####").format(res) + "%" + ")"*/
            cartTotal = 0.00
            for (i in data.indices) {
                cartTotal += if (data[i].salesPrice == data[i].manual_price || data[i].manual_price.isEmpty() || data[i].manual_price.contains(
                        "0.00",
                        true
                    )
                ) {
                    (data[i].salesPrice).toDouble() * data[i].quantity.toDouble()
                } else {
                    (data[i].manual_price).toDouble() * data[i].quantity.toDouble()
                }
            }
            val precision = DecimalFormat("#.##", DecimalFormatSymbols.getInstance(
                Locale.ENGLISH))
            cart_top_total.text =
                context.resources.getString(R.string.Qatarcurrency) + " " + precision.format(
                    cartTotal
                ).toString()
            txt_cart_count.text = data.size.toString()
            sessionManager.subTotal = cartTotal.toString()
            if (mList.offer.isNotEmpty() || mList.offer.isNotBlank()) {
                //itemView.txt_offer.text = mList.offer
               // itemView.txt_offer.visibility = View.VISIBLE
            } else {
               /// itemView.txt_offer.visibility = View.GONE
            }


        }
    }

    public fun getDecreaseAmount(quantity: Int?) {
        val precision = DecimalFormat("#.##", DecimalFormatSymbols.getInstance(
                Locale.ENGLISH))
        val subTotal =
            (session.subTotal.toString()).toDouble() - (data[mHolder.adapterPosition].salesPrice).toDouble()
        cart_top_total.text =
            context.resources.getString(R.string.Qatarcurrency) + " " + precision.format(subTotal)
                .toString()
        session.subTotal = subTotal.toString()
    }


    private fun getAmountCalculated(quantity: Int?) {
        val precision = DecimalFormat("#.##", DecimalFormatSymbols.getInstance(
                Locale.ENGLISH))
        val subTotal =
            (session.subTotal.toString()).toDouble() + (data[mHolder.adapterPosition].salesPrice).toDouble()
        cart_top_total.text =
            context.getString(R.string.Qatarcurrency) + " " + precision.format(subTotal).toString()
        session.subTotal = subTotal.toString()
    }

    fun notifyDatachanged(data: List<ProductByCodeModel>) {
        this.data = data
        notifyDataSetChanged()
    }

    private fun cartDelete(cart_item_id: String) {
        ApiManager.doDeleteCart(cart_item_id)
            .subscribe(
                this::handleDeleteResponse, this::handleError
            )
    }

    private fun handleDeleteResponse(rsponse: StatusModel) {
        Utils.pd.dismiss()
        if (rsponse.status == 200) {
            database.todoDao().deleteProduct(data[mHolder.adapterPosition].pluCode)
            if (data.isEmpty()) {
                txt_cart_count.text = data.size.toString()
                session.totalCart = data.size.toString()
                session.subTotal = ""
                cart_top_total.text =
                    context.resources.getString(R.string.model_qr_price)
                recycler_scan_cart_lits.visibility = View.GONE
                rele_scan_pay.visibility = View.GONE
            }
            (context as BarcodeScannerActivity).fetchWeatherDataFromDb()
        } else {
            var msg = "";
            if(session.languageselect == "en") {
                msg = rsponse.statusMessage
            }else{
                msg = rsponse.statusMsgArabic
            }
            Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show()
        }

    }

    private fun handleError(error: Throwable) {
        Utils.pd.dismiss()
        Toast.makeText(context, "Error ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
    }



}