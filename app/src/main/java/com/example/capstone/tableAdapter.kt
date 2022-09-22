package com.example.capstone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class tableAdapter(private val tableList: ArrayList<DataTable>,
                   private val listener: OnItemClickListener):
    RecyclerView.Adapter<tableAdapter.TableViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.table_items,parent,false)
        return TableViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        val currentItem = tableList[position]
        holder.tableNum.text = (currentItem.id?.plus(1)).toString()


    }

    override fun getItemCount(): Int {
        return tableList.size
    }

    inner class TableViewHolder(itemView: View): RecyclerView.ViewHolder(itemView),View.OnClickListener {
        val tableNum: TextView = itemView.findViewById(R.id.tvTableNum)
        init{
            itemView.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
          /* val position: Int = adapterPosition

            if(position!= RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }*/
            val currentItem = tableList[adapterPosition]
            if(!currentItem.id!!.equals(null)){
                listener.onItemClick(currentItem.id)
            }
        }

    }

    interface OnItemClickListener{
        fun onItemClick(tableId:Int)
    }
}