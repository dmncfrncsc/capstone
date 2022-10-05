package com.example.capstone

data class DataCartList(
    var tableType: String,
    var num: Int,
    var itemName: String,
    var price: Long,
    var qty: Int,
    var subTotal: Long
)

{

     fun editQty(qty:Int){
        this.qty = qty
    }
}