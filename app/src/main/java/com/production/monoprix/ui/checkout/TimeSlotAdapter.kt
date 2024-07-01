package com.production.monoprix.ui.checkout

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.production.monoprix.R
import com.production.monoprix.model.TimeSlot
import kotlinx.android.synthetic.main.item_time_slot.view.*

class TimeSlotAdapter(private var dataList: List<TimeSlot>, private val context: Context) :
    RecyclerView.Adapter<TimeSlotAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[holder.adapterPosition], context)
    }

    override fun getItemCount(): Int = dataList.count()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_time_slot, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(mList: TimeSlot, context: Context) {
            itemView.txt_time_slot.text = mList.slotName
            itemView.readio_button.isChecked = mList.isClicked
        }


    }
}