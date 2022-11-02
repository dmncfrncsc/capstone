package com.example.capstone

class DataQueue {
    var tableId: Int? = null
    var timeStamp: Any? = null
    val status: String? = null

   fun CreateTableId(tableId:Int){
       this.tableId = tableId
   }
    fun CreateTimeStamp(timeStamp: Any?){
        this.timeStamp = timeStamp
    }
}