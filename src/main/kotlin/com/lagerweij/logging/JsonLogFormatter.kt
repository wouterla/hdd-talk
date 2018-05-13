package com.lagerweij.logging

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
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

  val timeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

  data class LogObject(val log: String, val stream: String, val time: String, val logRecord: LogRecord)

  override public fun format(record: LogRecord): String {

    val time = Date(record.millis)
    val timeString = timeFormat.format(time)

    val logObject = LogObject(record.message, "webshop.log", timeString, record)
    val mapper = ObjectMapper()
    var jsonInString = mapper.writeValueAsString(logObject)

    jsonInString += "\n"

    return jsonInString
  }
}
