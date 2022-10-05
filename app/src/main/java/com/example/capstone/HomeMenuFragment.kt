package com.example.capstone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeMenuFragment: Fragment(){
    private lateinit var dbref: DatabaseReference
    private lateinit var menuRecyclerView: RecyclerView
    private lateinit var menuArrayList: ArrayList<menu_meal>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home_menu,container,false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendar: Date = Calendar.getInstance().time

        val currentDate: String = DateFormat.getDateInstance(DateFormat.FULL).format(calendar)

        var txtDate: TextView = view.findViewById(R.id.txtDate)

        txtDate.text = currentDate
    }
}