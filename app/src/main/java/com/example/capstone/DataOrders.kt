package com.example.capstone


data class DataOrders(
    var tableId: Int? = null,
    var itemName: String? = null,
    var price: Long? = null,
    var quantity: Int? = null,
    var subTotal: Long? = null,
    var queueID: String? = null,
    var status: Boolean? = null,
    var category: String? = null,
    var imageUrl: String? = null,
    var isBucket: Boolean? = null,
) {

    fun swapTables(tableId: Int) {
        this.tableId = tableId
    }

    fun swapQueueId(queue: String){
        this.queueID = queue
    }
}
