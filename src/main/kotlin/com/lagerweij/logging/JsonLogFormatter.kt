package com.lagerweij.logging

import com.google.gson.Gson
import java.util.logging.ConsoleHandler
import java.util.logging.Formatter
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger
import java.util.Date
import java.text.SimpleDateFormat

@Suppress("unused")
class JsonLogFormatter : Formatter() {

  private val gson = Gson()
  val timeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

  override public fun format(record: LogRecord): String {

    val time = Date(record.millis)
    val timeString = timeFormat.format(time)
    var params = record.getParameters()
    var jsonInString: String?

    if (params != null && params.get(0) != null) {
      var msgObj = params.get(0) as MsgObj
      msgObj.time = timeString
      jsonInString = gson.toJson(msgObj)
    } else {
      jsonInString = record.message
    }
    jsonInString += "\n"

    return jsonInString
  }
}
