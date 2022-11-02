package com.example.capstone

data class DataSwapQueue(
    var tableId: Int? = null,
    var timeStamp: Int? = null,
    val status: String? = null,
    var QueueId: String? = null
) {

    fun addQueueId(queueId: String){
        this.QueueId = queueId
    }
    fun swapTables(tableId: Int) {
        this.tableId = tableId
    }
}