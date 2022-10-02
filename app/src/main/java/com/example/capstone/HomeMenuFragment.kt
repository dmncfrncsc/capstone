package com.example.capstone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

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
        val layoutManager = LinearLayoutManager(context)
        menuRecyclerView = view.findViewById(R.id.menuList)
        menuRecyclerView.layoutManager = layoutManager
        menuRecyclerView.setHasFixedSize(true)
        menuArrayList = arrayListOf<menu_meal>()

        getMealData()


    }



    private fun getMealData() {
        dbref = FirebaseDatabase.getInstance().getReference("meals")

        dbref.addValueEventListener(object : ValueEventListener,
            MenuListAdapter.OnItemClickListner {
            override fun onDataChange(p0: DataSnapshot) {
                menuArrayList.clear();
                if(p0.exists()){
                    for(mealSnapshot in p0.children){
                        val meal = mealSnapshot.getValue(menu_meal::class.java)
                        menuArrayList.add(meal!!)

                    }
                    menuRecyclerView.adapter = MenuListAdapter(menuArrayList, this)

                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onItemClick(position: Int) {
                menuRecyclerView.isEnabled = true;
            }


        })
    }
}