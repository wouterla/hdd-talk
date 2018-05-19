package com.lagerweij.hdd.webshop

import com.google.gson.Gson
import io.vertx.core.eventbus.EventBus
import com.lagerweij.logging.MsgObj

class Logger(eventBus: EventBus) {

    private val gson = Gson()
    private var eventBus : EventBus = eventBus

    /**
     * Execute logging asynchronously
     */
    fun log(message: String) {
        eventBus.send("logger", message)
    }

    fun log(values: Map<String, String?>) {
        eventBus.send("logger", values)
    }

    fun log(msgObj: MsgObj) {
      eventBus.send("logger", gson.toJson(msgObj))
    }

    /* fun logMap(logMap: Map<String, String?>) {
        val obj = MsgObj(logMap)
        log(gson.toJson(obj))
    } */

    fun logJson(json: String) {
        log(json)
        try {
          val containedString = "{\"values\":" + json + "}"
          val obj = gson.fromJson(containedString, MsgObj::class.java)
          log(gson.toJson(obj))
        } catch (exception: Exception) {
          log(exception.toString())
        }
    }

}
