package com.example.capstone
data class DataCartList(
    val ItemCode: Any,
    var TableId: Int,
    var ItemName: String,
    var price: Long,
    var quantity: Int,
    var subTotal: Long,
    var category: String,
    val status: Boolean,
    val isBucket: Boolean,
    val ImageUrl: String
) {
    fun editQty(currentQty: Int) {
        this.quantity = this.quantity + currentQty
        this.subTotal = this.quantity * price
    }

    fun subQty(currentQty: Int) {
        this.quantity = currentQty
        this.subTotal = this.quantity * price
    }

    fun addQty(currentQty: Int){
        this.quantity = currentQty
        this.subTotal = this.quantity * price
    }
}



