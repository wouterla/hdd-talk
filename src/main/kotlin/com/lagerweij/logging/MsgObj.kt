package com.lagerweij.logging

data class MsgObj(val action: String) {
      var time: String = ""
      var cookieName: String = ""
      var cookieValue: String = ""
      var cookieStatus: String = ""
      var withTimer: String = ""
      var products: List<Int> = emptyList<Int>()
      var inTime: String = ""
      var price: Int = 0
    }
