package com.lagerweij.logging

/* import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper */
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

  data class LogObject(val log: String, val stream: String, val time: String, val values: Any)

  override public fun format(record: LogRecord): String {

    val time = Date(record.millis)
    val timeString = timeFormat.format(time)
    var values = record.getParameters()

    if (values == null) {
      values = emptyArray<String>()
    }
    val logObject = LogObject(record.message, "webshop.log", timeString, values)

    var jsonInString = gson.toJson(logObject)

    jsonInString += "\n"

    return jsonInString
  }
}
