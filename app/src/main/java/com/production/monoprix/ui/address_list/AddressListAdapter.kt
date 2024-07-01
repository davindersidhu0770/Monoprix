package com.production.monoprix.ui.address_list

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import com.production.monoprix.ui.address_list.ItemClickListener
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.production.monoprix.R
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.production.monoprix.model.AddressLists
import com.production.monoprix.ui.add_new_address.AddNewAddressActivity
import java.util.ArrayList
import kotlin.jvm.JvmStatic

class AddressListAdapter     // Create constructor 
    (// Initialize variable 
    private var dataList: List<AddressLists>, private val context: Context,
    private val diff: String?, var addressID: String,
    var itemClickListener: ItemClickListener
) : RecyclerView.Adapter<AddressListAdapter.ViewHolder>() {
    var selectedPosition = -1
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        // Initialize view 
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.layout_address_list_items, parent,
                false
            )
        // Pass holder view 
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        // Set text on radio button 

        // Checked selected radio button 
        holder.radioButton.isChecked = (position == selectedPosition)
        if (dataList.get(position).is_defalut_shipping_address)
            holder.radioButton.isChecked = true
        // set listener on radio button 
        holder.radioButton.setOnCheckedChangeListener { compoundButton, b ->
            // check condition 
            if (b) {
                // When checked 
                // update selected position 
                selectedPosition = holder.adapterPosition
                // Call listener 
                itemClickListener.onClick(
                    holder.radioButton.text
                        .toString()
                )
            }
        }

        if (dataList.get(position).address_type == "1") {
            holder.txt_name_address.text = context.getString(R.string.label_home)
        } else if (dataList.get(position).address_type == "2") {
            holder.txt_name_address.text = context.getString(R.string.label_office)
        } else if (dataList.get(position).address_type == "3") {
            holder.txt_name_address.text = context.getString(R.string.label_other)
        } else {
            holder.txt_name_address.text = dataList.get(position).address_type
        }
        val address = dataList.get(position).addressDesc ?: ""
        val housenumber = dataList.get(position).houseNo
        val buildingNumber = dataList.get(position).building
        val street = dataList.get(position).street
        val landmark = dataList.get(position).landMark
        var completeAddress = ""
        if (housenumber != null)
            completeAddress =
                context.getString(R.string.house_no_txt) + " - " + dataList.get(position).houseNo
        if (buildingNumber != null)
            completeAddress =
                completeAddress + " " + context.getString(R.string.buildingnumber) + " " + dataList.get(
                    position
                ).building + "\n"

        if (street != null)
            completeAddress =
                completeAddress + context.getString(R.string.street_txt) + " " + dataList.get(
                    position
                ).street + " "

        if (landmark != null)
            completeAddress =
                completeAddress + " " + context.getString(R.string.landmark) + " - " + dataList.get(
                    position
                ).landMark

/*
        holder.txt_address.text = if(address.isEmpty()) address + "" + dataList.get(position).area_name +
                dataList.get(position).city_name else address + " " + dataList.get(position).area_name + dataList.get(position).city_name
*/

        holder.txt_address.text = completeAddress

        holder.txt_mobileno_address.text =
            context.getString(R.string.mobile) + "${dataList.get(position).mobile_number}"

        holder.button_edit_address_page.setOnClickListener {
            val i = Intent(context, AddNewAddressActivity::class.java)
            i.putExtra("addressID", dataList.get(position).shipping_address_id)
            i.putExtra("phone", dataList.get(position).mobile_number)
            i.putExtra("address", dataList.get(position).addressDesc)
            i.putExtra("area", dataList.get(position).area_name)
            i.putExtra("city", dataList.get(position).city_name)
            i.putExtra("cityId", dataList.get(position).city_id)

            i.putExtra("address_type", dataList.get(position).address_type)
            i.putExtra(
                "isDefaultAddress",
                dataList.get(position).is_defalut_shipping_address.toString()
            )
            i.putExtra("postcode", dataList.get(position).postcode)
            i.putExtra("country", dataList.get(position).countryname)

            i.putExtra("street", dataList.get(position).street)
            i.putExtra("building", dataList.get(position).building)
            i.putExtra("zone_no", dataList.get(position).zoneId.toString())
            i.putExtra("municipality", dataList.get(position).muncipality.toString())
            i.putExtra("place", dataList.get(position).place.toString())
            i.putExtra("house_no", dataList.get(position).houseNo)
            i.putExtra("pageName", "1")
            i.putExtra("landMark", dataList.get(position).landMark)
            (context as Activity).startActivityForResult(i, 200)
        }

        holder.tvdeladdress.setOnClickListener {
            //show confirm dialog first...

            val builder = AlertDialog.Builder(context)
            builder.setTitle(context.resources.getString(R.string.app_name))
            builder.setMessage(context.resources.getString(R.string.doyou))
                .setCancelable(false)
                .setPositiveButton(context.resources.getString(R.string.txt_yes)) { dialog, id ->
                    // Delete selected note from database
                    itemClickListener.passAddressId(dataList.get(position).shipping_address_id)

                }
                .setNegativeButton(context.resources.getString(R.string.txt_no)) { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()

        }

        holder.layout_main_list_address.setOnClickListener {
            diff?.let {
                if (it.isNotEmpty()) {

                    val intent = Intent()
                    intent.putExtra("phone", dataList.get(position).mobile_number)
                    intent.putExtra("address", dataList.get(position).addressDesc)
                    intent.putExtra("area", dataList.get(position).area_name)
                    intent.putExtra("city", dataList.get(position).city_name)
                    intent.putExtra("address_type", dataList.get(position).address_type)
                    intent.putExtra("addressID", dataList.get(position).shipping_address_id)
                    intent.putExtra("postcode", dataList.get(position).postcode)
                    intent.putExtra("isDefaultAddress", dataList.get(position).is_defalut_shipping_address.toString())
                    intent.putExtra("country", dataList.get(position).countryname)
                    intent.putExtra("building", dataList.get(position).building)
                    intent.putExtra("house_no", dataList.get(position).houseNo)
                    intent.putExtra("muncipality", dataList.get(position).muncipality)
                    intent.putExtra("place", dataList.get(position).place)
                    intent.putExtra("zone_no", dataList.get(position).zoneId).toString()
                    intent.putExtra("zone", dataList.get(position).zoneId)
                    intent.putExtra("landMark", dataList.get(position).landMark)

                    Log.d("27Nov:"," zone_no iii "+dataList.get(position).zoneId.toString())
//                    Log.d("27Nov:"," place iii "+dataList.get(position).place.toString())
//                    Log.d("22Nov:"," houseNo iii"+dataList.get(position).houseNo)
//                    Log.d("22Nov:"," muncipality iii"+dataList.get(position).muncipality+
//                           "place "+ dataList.get(position).place+"landMark "+dataList.get(position).landMark)

                    (context as Activity).setResult(200, intent)
                    context.finish()

//                  holder.radioButton.isChecked= true
                    holder.radioButton.performClick()

                }
            }


        }
    }

    override fun getItemId(position: Int): Long {
        // pass position 
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        // pass position 
        return position
    }

    override fun getItemCount(): Int {
        // pass total list size 
        return dataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Initialize variable 
        var radioButton: RadioButton
        var txt_name_address: TextView
        var button_edit_address_page: TextView
        var tvdeladdress: TextView
        var txt_address: TextView
        var txt_mobileno_address: TextView
        var layout_main_list_address: RelativeLayout

        init {

            // Assign variable 
            radioButton = itemView.findViewById(R.id.radioButton)
            tvdeladdress = itemView.findViewById(R.id.tvdeladdress)
            txt_name_address = itemView.findViewById(R.id.txt_name_address)
            button_edit_address_page = itemView.findViewById(R.id.button_edit_address_page)
            txt_address = itemView.findViewById(R.id.txt_address)
            txt_mobileno_address = itemView.findViewById(R.id.txt_mobileno_address)
            layout_main_list_address = itemView.findViewById(R.id.layout_main_list_address)
        }
    }
}

internal object GFG {
    @JvmStatic
    fun main(args: Array<String>) {
        println("GFG!")
    }
}