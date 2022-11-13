package com.example.capstone

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class SelectTableAdapter(
    private val tableList: ArrayList<DataTable>
) :
    RecyclerView.Adapter<SelectTableAdapter.TableViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.table_items, parent, false)
        return TableViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        val currentItem = tableList[position]
        holder.tableType.text = (currentItem.Category.toString())
        holder.tableNum.text = (currentItem.id?.toString())
        if (holder.tableType.text.equals("Door")) {
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        }
        if (holder.tableType.text.equals("Table")) {
            holder.iconType.setImageResource(R.drawable.icon_table)
        }
        if (holder.tableType.text.equals("Hut")) {
            holder.iconType.setImageResource(R.drawable.icon_hut)
        }
        if (!currentItem.Status!!) {
            holder.itemView.setBackgroundResource(R.drawable.table_occupied_bg)
            holder.tableType.setTextColor(Color.WHITE)
            holder.tableNum.setTextColor(Color.WHITE)
            holder.poundSign.setTextColor(Color.WHITE)
            holder.btnTakeOrder.isEnabled = false
            holder.btnViewOrders.isEnabled =true
            holder.btnTransferTable.isEnabled =true
        } else {
            holder.itemView.setBackgroundResource(R.drawable.table_items_bg)
            holder.tableType.setTextColor(Color.BLACK)
            holder.tableNum.setTextColor(Color.BLACK)
            holder.poundSign.setTextColor(Color.BLACK)
            holder.btnTakeOrder.isEnabled = true
            holder.btnViewOrders.isEnabled =false
            holder.btnTransferTable.isEnabled =false
        }
        var isVisible: Boolean = currentItem.visibility
        holder.expandOption.visibility = if (isVisible) View.VISIBLE else View.GONE
        holder.linear.setOnClickListener {
            currentItem.visibility = !currentItem.visibility
            notifyItemChanged(position)
        }
        holder.btnTakeOrder.setOnClickListener {
            val activity = it.context as AppCompatActivity
            var arrayList = arrayListOf<DataCartList>()
            activity.supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    TakeOrderFragment(holder.tableNum, holder.tableType, arrayList, SelectTableFragment())
                ).commitNow()
        }
        holder.btnViewOrders.setOnClickListener{
            val activity = it.context as AppCompatActivity
            activity.supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    OrderListFragment(holder.tableNum, holder.tableType)
                ).commitNow()
        }
        holder.btnTransferTable.setOnClickListener{
            val activity = it.context as AppCompatActivity
            activity.supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    TransferTableFragment(holder.tableNum, holder.tableType)
                ).commitNow()
        }
    }

    override fun getItemCount(): Int {
        return tableList.size
    }

    inner class TableViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)/*,View.OnClickListener*/ {
        val tableNum: TextView = itemView.findViewById(R.id.tvTableNum)
        val tableType: TextView = itemView.findViewById(R.id.tableType)
        val iconType: ImageView = itemView.findViewById(R.id.iconType)
        val expandOption: ConstraintLayout = itemView.findViewById(R.id.expandOption)
        val linear: LinearLayout = itemView.findViewById(R.id.linear)
        val btnTakeOrder: Button = itemView.findViewById(R.id.btnTakeOrder)
        val poundSign: TextView = itemView.findViewById(R.id.poundSign)
        val btnViewOrders: Button = itemView.findViewById(R.id.btnViewOrders)
        val btnTransferTable: Button = itemView.findViewById(R.id.btnTransferTable)
    }
}