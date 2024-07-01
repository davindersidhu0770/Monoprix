package com.production.monoprix.ui.checkout

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Paint
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.production.monoprix.R
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.model.CartList
import com.production.monoprix.model.StatusModel
import com.production.monoprix.room.AppDatabase
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import kotlinx.android.synthetic.main.item_product_list.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.collections.ArrayList


class CheckoutAdapter(
    private var dataList: ArrayList<CartList>,
    private val context: Context,
    val txt_sub_total: TextView,
    val txt_payableamount: TextView,
    val lable_home_delivery_err_msg: TextView,
    val c2: ConstraintLayout,
    var subamt: String

//    var addUpdateDlg : Dialog? = null
) :
    RecyclerView.Adapter<CheckoutAdapter.ViewHolder>() {
    lateinit var sessionManager: SessionManager
    var quantity: Int = 0
    lateinit var jsonArray: JSONArray
    lateinit var jsonObject: JSONObject
    lateinit var mHolder: ViewHolder
    lateinit var db: AppDatabase
//    var myCountDownTimer: MyCountDownTimer? = null

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        db = AppDatabase(context)
        sessionManager = SessionManager(context)
        holder.bind(dataList[holder.adapterPosition], context)

        var manualPrice: Double = dataList.get(position).itemSellingPrice.toDouble()//manual price
        var salesPrice: Double = dataList.get(position).itemOriginalPrice.toDouble()//salesPrice
        val precision = DecimalFormat(
            "#.##", DecimalFormatSymbols.getInstance(
                Locale.ENGLISH
            )
        )
        Log.e("price_check", "manualPrice= " + manualPrice + " salesPrice= " + salesPrice)
        holder.itemView.txt_discount_price.setTextColor(
            context.getResources().getColor(R.color.black)
        );


        Log.e("vihuu==", "= " + dataList.get(position).pluBarcode)
        if (dataList.get(position).pluBarcode) {
            var weigth: Double = manualPrice / salesPrice
            holder.itemView.img_cart_plus.isEnabled = false
            holder.itemView.img_cart_minus.isEnabled = false
            holder.itemView.img_cart_plus.isClickable = false
            holder.itemView.img_cart_minus.isClickable = false
            holder.itemView.txt_discount_price.text =
                context.resources.getString(R.string.Qatarcurrency) + " " + precision
                    .format(manualPrice)
                    .toString() + " " + context.resources.getString(R.string.weight) + " " + precision.format(
                    weigth
                ).toString()
        } else {
            holder.itemView.img_cart_plus.setTextColor(
                context.getResources().getColor(R.color.black)
            );
            holder.itemView.img_cart_minus.setTextColor(
                context.getResources().getColor(R.color.black)
            );
            holder.itemView.img_cart_plus.isEnabled = true
            holder.itemView.img_cart_minus.isEnabled = true
            holder.itemView.img_cart_plus.isClickable = true
            holder.itemView.img_cart_minus.isClickable = true
            holder.itemView.txt_discount_price.text =
                context.resources.getString(R.string.Qatarcurrency) + " " + precision.format(
                    salesPrice
                ).toString()
        }

//        if(dataList.get(position).pluBarcode)
//        {
//            var weigth: Double =manualPrice/salesPrice
//            holder.itemView.img_cart_plus.isEnabled = false
//            holder.itemView.img_cart_minus.isEnabled = false
//            holder.itemView.txt_discount_price.text = context.resources.getString(R.string.Qatarcurrency) + " " +precision.format(manualPrice).toString() + " "+context.resources.getString(R.string.weight)+" " + precision.format(weigth).toString()
//        }
//        else
//        {
//            holder.itemView.txt_discount_price.text = context.resources.getString(R.string.Qatarcurrency) + " " + precision.format(salesPrice).toString()
//        }


        holder.itemView.img_cart_plus.setOnClickListener {
            /*update cart quantity increase*/
            AddUpdateShowDialog(2, holder, position)
            mHolder = holder
            quantity = dataList[holder.adapterPosition].cartQuantity.toInt()
            quantity += 1
            updateCartJsonFormat(dataList[holder.adapterPosition].cartItemId)
            holder.itemView.txt_cart_quant.text = quantity.toString()
        }
        holder.itemView.img_cart_minus.setOnClickListener {
            /*update cart quantity decrease*/
            if (dataList[holder.adapterPosition].cartQuantity.toInt() > 1) {
                AddUpdateShowDialog(1, holder, position)
            }
            mHolder = holder
            quantity = dataList[holder.adapterPosition].cartQuantity.toInt()
            quantity -= 1
            if (quantity >= 1) {
                updateCartJsonFormat(dataList[holder.adapterPosition].cartItemId)
                holder.itemView.txt_cart_quant.text = quantity.toString()
            }
        }

        holder.itemView.rel_cart_delete.setOnClickListener {
            mHolder = holder
            val dialogBuilder = android.app.AlertDialog.Builder(context)
            dialogBuilder.setTitle(context.getString(R.string.checkout_ti))
            dialogBuilder.setMessage(context.getString(R.string.del_msg))
                .setCancelable(false)
                .setPositiveButton(
                    context.getString(R.string.ok),
                    DialogInterface.OnClickListener { dialog, id ->
                        Utils.showProgressDialog(context, true)
                        cartDelete(dataList[holder.adapterPosition].cartItemId)

                    })
                .setNegativeButton(
                    context.getString(R.string.cancel),
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                    })
            val alert = dialogBuilder.create()
            alert.show()


        }

        /* if(subamt.toDouble() < 99.00) {
             c2.isEnabled = false
             c2.alpha = 0.5f
             lable_home_delivery_err_msg.visibility = View.VISIBLE
             lable_home_delivery_err_msg.text = context.getString(R.string.home_delivery_er)
             if (context is CheckoutActivity) {
                 context.ChangeTakeAway()
             }
         }*/

    }


    fun AddUpdateShowDialog(i: Int, holder: CheckoutAdapter.ViewHolder, position: Int) {
        val addUpdateDlg = Dialog(context, R.style.MyDialogTheme1)
        addUpdateDlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        addUpdateDlg.setCancelable(false)
        addUpdateDlg.setContentView(R.layout.dailog_quntity_update)
        val dialogtext: TextView = addUpdateDlg.findViewById(R.id.tv_dsc) as TextView
        val dialogButton: RelativeLayout =
            addUpdateDlg.findViewById(R.id.rl_mainview) as RelativeLayout
        dialogtext.setText(context.getString(R.string.dailog_update_qnty))

        Handler().postDelayed({
            addUpdateDlg.dismiss()
        }, 1000)
        dataList[holder.adapterPosition].timerclose = true;
        dialogButton.setOnClickListener(View.OnClickListener {
            /* when(i) {

                 1 -> {//minus
                     mHolder = holder
                     quantity = dataList[holder.adapterPosition].cartQuantity.toInt()
                     quantity -= 1
                     if (quantity >= 1) {
                         updateCartJsonFormat(dataList[holder.adapterPosition].cartItemId)
                         holder.itemView.txt_cart_quant.text = quantity.toString()
                     }
                 }

                 2 -> {//plus
                     mHolder = holder
                     quantity = dataList[holder.adapterPosition].cartQuantity.toInt()
                     quantity += 1
                     updateCartJsonFormat(dataList[holder.adapterPosition].cartItemId)
                     holder.itemView.txt_cart_quant.text = quantity.toString()
                 }
             }*/

            addUpdateDlg.dismiss()
        }
        )
        addUpdateDlg.show()

    }

    override fun getItemCount(): Int = dataList.count()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_product_list, parent, false)
        return ViewHolder(view)
    }

//    class MyCountDownTimer(
//        millisInFuture: Long,
//        countDownInterval: Long
//    ) :
//        CountDownTimer(millisInFuture, countDownInterval) {
//        override fun onTick(millisUntilFinished: Long) {
//            Log.e("ontick","count="+(millisUntilFinished / 1000).toInt());
//
////            dialog!!.dismiss()
//        }
//
//        override fun onFinish() {
////            finish()
//
//        }
//    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var sessionManager: SessionManager
        fun bind(
            mList: CartList,
            context: Context
        ) {
            sessionManager = SessionManager(context)
            sessionManager.cartId = mList.cartId
            itemView.txt_product_title.text = mList.itemName
            itemView.txt_discount_price.text =
                context.resources.getString(R.string.Qatarcurrency) + " " + mList.itemSellingPrice
            /*itemView.txt_original_price.text =
                context.resources.getString(R.string.Qatarcurrency) + " " + mList.itemOriginalPrice*/
            /* itemView.txt_original_price.paintFlags =
                 itemView.txt_original_price.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG*/
            itemView.txt_cart_quant.text = mList.cartQuantity
            val amount: Double =
                mList.itemOriginalPrice.toDouble() - mList.itemSellingPrice.toDouble()
            itemView.txt_you_save.text =
                context.resources.getString(R.string.yousave) + " " + context.resources.getString(R.string.Qatarcurrency) + " " + DecimalFormat(
                    "##.####"
                ).format(amount)
            if (mList.itemSellingPrice == mList.itemOriginalPrice || mList.itemOriginalPrice.isEmpty() || mList.itemOriginalPrice == "0.00") {
                itemView.txt_you_save.visibility = View.INVISIBLE
                itemView.txt_original_price.visibility = View.INVISIBLE
            } else {
                // itemView.txt_you_save.visibility = View.VISIBLE
                itemView.txt_original_price.visibility = View.INVISIBLE
            }

            if (mList.itemSellingPrice == "0.00" && mList.promotionFlag) {
                itemView.txt_free.visibility = View.VISIBLE
                itemView.txt_cart_quant.text =
                    context.getString(R.string.qantity) + mList.cartQuantity
                itemView.img_cart_plus.visibility = View.GONE
                itemView.img_cart_minus.visibility = View.GONE
                itemView.txt_discount_price.visibility = View.GONE
                itemView.txt_original_price.visibility = View.GONE
                itemView.txt_you_save.visibility = View.GONE
            } else {
                itemView.txt_free.visibility = View.GONE
                itemView.img_cart_plus.visibility = View.VISIBLE
                itemView.img_cart_minus.visibility = View.VISIBLE
                itemView.txt_discount_price.visibility = View.VISIBLE
                if (mList.itemSellingPrice == mList.itemOriginalPrice || mList.itemOriginalPrice.isEmpty() || mList.itemOriginalPrice == "0.00") {
                    itemView.txt_you_save.visibility = View.INVISIBLE
                    itemView.txt_original_price.visibility = View.INVISIBLE
                } else {
                    //itemView.txt_you_save.visibility = View.VISIBLE
                    // itemView.txt_original_price.visibility = View.VISIBLE
                }
            }

        }

    }

    private fun updateCartJsonFormat(cart_item_id: String) {
        jsonArray = JSONArray()
        try {
            jsonObject = JSONObject()
            jsonObject.put("cart_item_id", cart_item_id)
            jsonObject.put("cart_quantity", quantity)
            jsonArray.put(jsonObject)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("jsonResponse", jsonArray.toString() + "")
        Utils.showProgressDialog(context, true)
        cartUpdate(cart_item_id, quantity.toString())
    }

    private fun cartUpdate(cart_item_id: String, quantity: String) {
        ApiManager.doUpdateCart(cart_item_id, quantity)
            .subscribe(
                this::handleResponse, this::handleError
            )
    }

    private fun handleResponse(rsponse: StatusModel) {
        Utils.pd.dismiss()
        if (rsponse.status == 200) {
            var msg = "";
            if (sessionManager.languageselect == "en") {
                msg = rsponse.statusMessage
            } else {
                msg = rsponse.statusMsgArabic
            }
            Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show()
            dataList[mHolder.adapterPosition].cartQuantity = quantity.toString()
            mHolder.itemView.txt_cart_quant.text = quantity.toString()
            cartProductList(dataList[mHolder.adapterPosition].cartId.toInt())
            db.todoDao().update(quantity, dataList[mHolder.adapterPosition].barcode)

            /*if(rsponse.data.is_homedelivery_available){
                c2.isEnabled = true
                c2.alpha = 1f
                lable_home_delivery_err_msg.visibility = View.GONE
            }else {
                c2.isEnabled = false
                c2.alpha = 0.5f
                lable_home_delivery_err_msg.visibility = View.VISIBLE
                lable_home_delivery_err_msg.text = rsponse.data.delivery_message
            }*/
        } else {
            var msg = "";
            if (sessionManager.languageselect == "en") {
                msg = rsponse.statusMessage
            } else {
                msg = rsponse.statusMsgArabic
            }
            Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show()
        }

    }


    private fun cartDelete(cart_item_id: String) {
        ApiManager.doDeleteCart(cart_item_id)
            .subscribe(
                this::handleDeleteResponse, this::handleError
            )
    }

    private fun cartProductList(cartId: Int) {
        ApiManager.getProductList(sessionManager.userId.toString(), cartId)
            .subscribe(
                this::handleProductResponse, this::handleError
            )
    }

    private fun handleProductResponse(res: StatusModel) {
        Utils.pd.dismiss()
        Log.e("handleProductResponse", "${res.data}")
        if (res.status == 200) {
            if (res.data.subTotalAmount.toDouble() < 99.00) {
                if (context is CheckoutActivity) {
//                    context.ChangeTakeAway()
//                    context.ChangeHomeDelivery()
                    context.ChangeHomeDeliveryNew();
                }

                c2.isEnabled = false
                c2.alpha = 0.5f
//                lable_home_delivery_err_msg.visibility = View.GONE

            } else {
                c2.isEnabled = true
                c2.alpha = 1f
//                lable_home_delivery_err_msg.visibility = View.VISIBLE
            }
            lable_home_delivery_err_msg.visibility = View.VISIBLE
            lable_home_delivery_err_msg.text =
                if (sessionManager.languageselect == "en") res.data.delivery_message else res.data.delivery_message_arabic

            txt_sub_total.text =
                context.resources.getString(R.string.Qatarcurrency) + " " + res.data.subTotalAmount
            txt_payableamount.text =
                context.resources.getString(R.string.Qatarcurrency) + " " + res.data.total
        }
    }

    private fun handleDeleteResponse(rsponse: StatusModel) {
        Utils.pd.dismiss()
        if (rsponse.status == 200) {
            var msg = "";
            if (sessionManager.languageselect == "en") {
                msg = rsponse.statusMessage
            } else {
                msg = rsponse.statusMsgArabic
            }
            Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show()
            txt_sub_total.text = rsponse.data.subTotalAmount
            txt_payableamount.text = rsponse.data.total
//            db.todoDao().update(quantity, dataList[mHolder.adapterPosition].barcode)
            (context as CheckoutActivity).apiCall(dataList[mHolder.adapterPosition].cartId.toInt())
            if (rsponse.data.subTotalAmount.toDouble() < 99.00) {
                context.ChangeHomeDeliveryNew();
                c2.isEnabled = false
                c2.alpha = 0.5f
//                lable_home_delivery_err_msg.visibility = View.GONE
//                lable_home_delivery_err_msg.text = context.getString(R.string.home_delivery_er)
            } else {
                c2.isEnabled = true
                c2.alpha = 1f
            }
            lable_home_delivery_err_msg.visibility = View.VISIBLE
            lable_home_delivery_err_msg.text =
                if (sessionManager.languageselect == "en") rsponse.data.delivery_message else rsponse.data.delivery_message_arabic

            /*if(rsponse.data.is_homedelivery_available){
                c2.isEnabled = true
                c2.alpha = 1f
                lable_home_delivery_err_msg.visibility = View.GONE
            }else {
                c2.isEnabled = false
                c2.alpha = 0.5f
                lable_home_delivery_err_msg.visibility = View.VISIBLE
                lable_home_delivery_err_msg.text = rsponse.data.delivery_message
            }*/

            db.todoDao().deleteProductByCartItemId(dataList[mHolder.adapterPosition].cartItemId)
            notifyDataSetChanged()
        } else {
            var msg = "";
            if (sessionManager.languageselect == "en") {
                msg = rsponse.statusMessage
            } else {
                msg = rsponse.statusMsgArabic
            }
            Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show()
        }

    }

    private fun handleError(error: Throwable) {
        Utils.pd.dismiss()
//        Toast.makeText(context, "Error ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
    }

    fun addData(listItems: List<CartList>) {
        //val size = this.dataList.size
        dataList.addAll(listItems)
        //  val sizeNew = this.dataList.size
        //  notifyItemRangeChanged(size, sizeNew)
        notifyItemChanged(dataList.size)


    }

    fun lessData() {
        dataList.subList(2, dataList.size).clear()
        var ss = dataList
        notifyDataSetChanged()
        /*notifyItemRangeRemoved(2,dataList.size)*/
        // notifyItemRemoved(1)

        //notifyItemRemoved(dataList.size)


    }


}