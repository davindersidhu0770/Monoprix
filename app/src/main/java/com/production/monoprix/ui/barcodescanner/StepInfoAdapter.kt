package com.production.monoprix.ui.barcodescanner

import android.content.Context
import android.content.DialogInterface
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.production.monoprix.R
import com.production.monoprix.room.AppDatabase
import com.production.monoprix.model.ProductByCodeModel
import com.production.monoprix.model.StepInfoModel
import com.production.monoprix.util.SessionManager
import com.production.monoprix.util.Utils
import kotlinx.android.synthetic.main.item_product_list.view.*
import kotlinx.android.synthetic.main.item_step_info.view.*
import kotlinx.android.synthetic.main.layout_address_list_items.view.*
import java.text.DecimalFormat



class StepInfoAdapter (private val context: Context, private var data: ArrayList<StepInfoModel>) :
    RecyclerView.Adapter<StepInfoAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[holder.adapterPosition])
    }

    override fun getItemCount(): Int = data.count()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_step_info, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(stepInfoModel: StepInfoModel) {
            itemView.tvSrNo.visibility=View.GONE
            itemView.txt_descp.text=stepInfoModel.stepInfo
        }

    }


}