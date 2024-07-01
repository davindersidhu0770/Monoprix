package com.production.monoprix.ui.orderhistory

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.production.monoprix.R
import com.production.monoprix.model.OrderHistoryList
import com.production.monoprix.util.SessionManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_orderhistory_list.view.*
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class OrderHistoryAdapter(
    private var dataList: ArrayList<OrderHistoryList>,
    private val context: Context,
    private val sessionManager: SessionManager
) : RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[holder.adapterPosition], context, sessionManager)
    }

    override fun getItemCount(): Int = dataList.count()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_orderhistory_list, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(mList: OrderHistoryList, context: Context, sessionManager: SessionManager) {
            itemView.txt_product_name.text = mList.productName
            itemView.txt_order_id.text = context.getString(R.string.orderid) + " #" + mList.orderId
            Picasso.with(context).load(mList.qrCodeImageUrl).into(itemView.ivQrCode)
            val precision = DecimalFormat(
                "#.##", DecimalFormatSymbols.getInstance(
                    Locale.ENGLISH
                )
            )
            if (mList.noofitems == "1") {
                itemView.txt_order_items.text =
                    mList.noofitems + " " + context.getString(R.string.item) + " " + context.resources.getString(
                        R.string.Qatarcurrency
                    ) + " " + String.format(Locale.ENGLISH, "%.2f", mList.invoiceAmount.toFloat())
            } else {
                itemView.txt_order_items.text =
                    mList.noofitems + " " + context.getString(R.string.iems) + " " + context.resources.getString(
                        R.string.Qatarcurrency
                    ) + " " + String.format(Locale.ENGLISH, "%.2f", mList.invoiceAmount.toFloat())
            }
            dateFormat(mList.creationDatetime)
            itemView.txt_order_status.text =
                if (sessionManager.languageselect == "en") mList.deliveryStatus else mList.deliveryStatusArabic

            if (mList.deliveryStatus == "Order Placed") {
//                calculateDiffTime()
                if (!TextUtils.isEmpty(mList.creationDatetime)) {
                    val toyBornTime =
                        formateDate(mList.creationDatetime/*"2021-05-24T02:59:01.533"*/);
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

                    try {
                        val oldDate = dateFormat.parse(toyBornTime)
                        System.out.println(oldDate)
                        val currentDate = Date()
                        val diff = currentDate.time - oldDate.time
                        val seconds = diff / 1000
                        val minutes = seconds / 60
                        val hours = minutes / 60
                        val days = hours / 24
                        if (oldDate.before(currentDate)) {

                            Log.e("oldDate", "is previous date")
                            Log.e(
                                "Difference: ", " seconds: " + seconds + " minutes: " + minutes
                                        + " hours: " + hours + " days: " + days
                            )
                            if (minutes < 20 && minutes >= 0) {
                                itemView.rlmain.setBackgroundDrawable(
                                    context.resources.getDrawable(
                                        R.drawable.dotted_border_green
                                    )
                                )
                                itemView.txt_product_name.setTextColor(context.resources.getColor(R.color.green))
                                itemView.txt_order_status.setBackgroundDrawable(
                                    context.resources.getDrawable(
                                        R.drawable.buttonbggreen
                                    )
                                )

//                            itemView.card_view.setCardBackgroundColor(context.resources.getColor(R.color.green))
                            } else {
                                itemView.rlmain.setBackgroundDrawable(
                                    context.resources.getDrawable(
                                        R.drawable.dotted_border_theme
                                    )
                                )
                                itemView.txt_order_status.setBackgroundDrawable(
                                    context.resources.getDrawable(
                                        R.drawable.buttonbgtheme
                                    )
                                )
                                itemView.txt_product_name.setTextColor(context.resources.getColor(R.color.theme))

//                            itemView.card_view.setCardBackgroundColor(context.resources.getColor(R.color.red))
                            }
                        } else {
                            itemView.rlmain.setBackgroundDrawable(context.resources.getDrawable(R.drawable.dotted_border_theme))
                            itemView.txt_order_status.setBackgroundDrawable(
                                context.resources.getDrawable(
                                    R.drawable.buttonbgtheme
                                )
                            )
                            itemView.txt_product_name.setTextColor(context.resources.getColor(R.color.theme))

//                        itemView.card_view.setCardBackgroundColor(context.resources.getColor(R.color.red))
                        }

                        // Log.e("toyBornTime", "" + toyBornTime);
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                }
//                itemView.card_view.setCardBackgroundColor(context.resources.getColor(R.color.green))
            } else if (mList.deliveryStatus == "In Process") {
                itemView.rlmain.setBackgroundDrawable(context.resources.getDrawable(R.drawable.dotted_border_yellow))
                itemView.txt_order_status.setBackgroundDrawable(context.resources.getDrawable(R.drawable.buttonbgyellow))
                itemView.txt_product_name.setTextColor(context.resources.getColor(R.color.yellow))
            } else {
                itemView.rlmain.setBackgroundDrawable(context.resources.getDrawable(R.drawable.dotted_border_theme))
                itemView.txt_order_status.setBackgroundDrawable(context.resources.getDrawable(R.drawable.buttonbgtheme))
                itemView.txt_product_name.setTextColor(context.resources.getColor(R.color.theme))


            }
        }

        //    formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        fun dateFormat(sdate: String) {
            var date = sdate
            var spf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            val newDate = spf.parse(date)
            spf = SimpleDateFormat("dd MMM yyyy hh:mm aa", Locale.US)
            date = spf.format(newDate)
            itemView.txt_order_date.text = date
            println(date)
        }

//        fun  calculateDiffTime(dateString: String?): ?
//        {
////        val toyBornTime = "2021-05-25 12:56:50"
//
//            val toyBornTime = formateDate("2021-05-23T04:51:01.533");
//            val dateFormat = SimpleDateFormat(
//                "yyyy-MM-dd HH:mm:ss"
//            )
//
//            try {
//                val oldDate = dateFormat.parse(toyBornTime)
//                System.out.println(oldDate)
//                val currentDate = Date()
//                val diff = currentDate.time - oldDate.time
//                val seconds = diff / 1000
//                val minutes = seconds / 60
//                val hours = minutes / 60
//                val days = hours / 24
//                if (oldDate.before(currentDate)) {
//
//                    Log.e("oldDate", "is previous date")
//                    Log.e(
//                        "Difference: ", " seconds: " + seconds + " minutes: " + minutes
//                                + " hours: " + hours + " days: " + days
//                    )
//                }
//
//                // Log.e("toyBornTime", "" + toyBornTime);
//            } catch (e: ParseException) {
//                e.printStackTrace()
//            }
//            return minutes;
//        }


        //    2021-05-23T04:51:01.533 to 2020-08-23 10:30:00
        fun formateDate(dateString: String?): String? {
            val date: Date
            var formattedDate: String? = ""
            try {
                date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    .parse(dateString)
                formattedDate =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(date)
            } catch (e: Exception) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            return formattedDate
        }


    }


}