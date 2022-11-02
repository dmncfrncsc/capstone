package com.example.capstone

open class DataTable (val id: Int? = null, val Category:String? = null, var Status: Boolean? = null, var visibility: Boolean = false, var x:Int? = null, var y:Int? = null, var Color:String? = null){

    fun editInfo(status: Boolean, color:String){
        this.Status = status
        this.Color = color
    }
}