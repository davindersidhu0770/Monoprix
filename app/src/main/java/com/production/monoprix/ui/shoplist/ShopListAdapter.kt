package com.production.monoprix.ui.shoplist


import android.content.Context
import android.content.DialogInterface
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.production.monoprix.R
import com.production.monoprix.manager.ApiManager
import com.production.monoprix.model.CartList
import com.production.monoprix.model.StatusModel
import com.production.monoprix.room.AppDatabase
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import kotlinx.android.synthetic.main.activity_myaccount.*
import kotlinx.android.synthetic.main.item_product_list.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat

class ShopListAdapter(private var dataList: ArrayList<CartList>, private val context: Context,var fragment: ShopListFragment) :
    RecyclerView.Adapter<ShopListAdapter.ViewHolder>() {
    lateinit var sessionManager: SessionManager
    var quantity: Int = 0
    lateinit var jsonArray: JSONArray
    lateinit var jsonObject: JSONObject
    lateinit var mHolder: ViewHolder
    lateinit var db: AppDatabase

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        db = AppDatabase(context)
        sessionManager = SessionManager(context)
        holder.bind(dataList[holder.adapterPosition], context)
        if(holder.adapterPosition == dataList.size){
           holder.itemView.viewborder.visibility = View.GONE
        } else{
            holder.itemView.viewborder.visibility = View.VISIBLE
        }
        holder.itemView.img_cart_plus.setOnClickListener {
            /*update cart quantity increase*/
            mHolder = holder
            quantity = dataList[holder.adapterPosition].cartQuantity.toInt()
            quantity += 1
            updateCartJsonFormat(dataList[holder.adapterPosition].cartItemId)
        }
        holder.itemView.img_cart_minus.setOnClickListener {
            /*update cart quantity decrease*/
            mHolder = holder
            quantity = dataList[holder.adapterPosition].cartQuantity.toInt()
            quantity -= 1
            if (quantity >= 1) {
                updateCartJsonFormat(dataList[holder.adapterPosition].cartItemId)

            }
        }

        holder.itemView.rel_cart_delete.setOnClickListener {
            mHolder = holder
            val dialogBuilder = android.app.AlertDialog.Builder(context)
                dialogBuilder.setTitle(context.getString(R.string.myshopping))
            dialogBuilder.setMessage(context.getString(R.string.del_msg))
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.ok), DialogInterface.OnClickListener {
                        dialog, id ->
                    Utils.showProgressDialog(context,true)
                    cartDelete(dataList[holder.adapterPosition].cartItemId)

                })
                .setNegativeButton(context.getString(R.string.cancel), DialogInterface.OnClickListener {
                        dialog, id -> dialog.cancel()
                })
            val alert = dialogBuilder.create()
            alert.show()

        }

    }

    override fun getItemCount(): Int = dataList.count()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product_list, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
          lateinit var sessionManager: SessionManager
        fun bind(mList: CartList, context: Context) {
            sessionManager = SessionManager(context)
            sessionManager.cartId = mList.cartId
            itemView.txt_product_title.text = mList.itemName
            itemView.txt_discount_price.text = context.resources.getString(R.string.Qatarcurrency)+ " " + mList.itemSellingPrice
            itemView.txt_original_price.text = context.resources.getString(R.string.Qatarcurrency)+ " " +mList.itemOriginalPrice
            itemView.txt_original_price.paintFlags = itemView.txt_original_price.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            itemView.txt_cart_quant.text = mList.cartQuantity
            val amount: Double =  mList.itemOriginalPrice.toDouble() - mList.itemSellingPrice.toDouble()
            itemView.txt_you_save.text = context.resources.getString(R.string.yousave) + " " + context.resources.getString(R.string.Qatarcurrency) + " " +  DecimalFormat("##.####").format(amount)
            if(mList.itemSellingPrice == mList.itemOriginalPrice || mList.itemOriginalPrice.isEmpty() || mList.itemOriginalPrice == "0.00"){
                itemView.txt_you_save.visibility = View.INVISIBLE
                itemView.txt_original_price.visibility = View.INVISIBLE
            }else{
                itemView.txt_you_save.visibility = View.VISIBLE
                itemView.txt_original_price.visibility = View.VISIBLE
            }
            if (mList.itemSellingPrice == "0.00" && mList.promotionFlag){
                itemView.txt_free.visibility = View.VISIBLE
                itemView.txt_cart_quant.text = context.getString(R.string.qantity)+mList.cartQuantity
                itemView.img_cart_plus.visibility = View.GONE
                itemView.img_cart_minus.visibility = View.GONE
                itemView.txt_discount_price.visibility = View.GONE
                itemView.txt_original_price.visibility = View.GONE
                itemView.txt_you_save.visibility = View.GONE
            }else{
                itemView.txt_free.visibility = View.GONE
                itemView.img_cart_plus.visibility = View.VISIBLE
                itemView.img_cart_minus.visibility = View.VISIBLE
                itemView.txt_discount_price.visibility = View.VISIBLE
                if(mList.itemSellingPrice == mList.itemOriginalPrice || mList.itemOriginalPrice.isEmpty() || mList.itemOriginalPrice == "0.00"){
                    itemView.txt_you_save.visibility = View.INVISIBLE
                    itemView.txt_original_price.visibility = View.INVISIBLE
                }else{
                    itemView.txt_you_save.visibility = View.VISIBLE
                    itemView.txt_original_price.visibility = View.VISIBLE
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
        cartUpdate(cart_item_id,quantity.toString())
    }

    private fun cartUpdate(cart_item_id: String,quantity: String) {
        ApiManager.doUpdateCart(
            cart_item_id,quantity
        )
            .subscribe(
                this::handleResponse, this::handleError
            )
    }
    private fun handleResponse(rsponse: StatusModel) {
        Utils.pd.dismiss()
        if (rsponse.status == 200) {
            var msg = "";
            if(sessionManager.languageselect == "en") {
                msg = rsponse.statusMessage
            }else{
                msg = rsponse.statusMsgArabic
            }
            Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show()
            dataList[mHolder.adapterPosition].cartQuantity = quantity.toString()
            mHolder.itemView.txt_cart_quant.text = quantity.toString()
            db.todoDao().update(quantity, dataList[mHolder.adapterPosition].barcode)
        } else {
            var msg = "";
            if(sessionManager.languageselect == "en") {
                msg = rsponse.statusMessage
            }else{
                msg = rsponse.statusMsgArabic
            }
            Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show()
        }

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
            var msg = "";
            if(sessionManager.languageselect == "en") {
                msg = rsponse.statusMessage
            }else{
                msg = rsponse.statusMsgArabic
            }
            Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show()
            db.todoDao().deleteProductByCartItemId(dataList[mHolder.adapterPosition].cartItemId)
            fragment.apiCall()
        } else {
            var msg = "";
            if(sessionManager.languageselect == "en") {
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