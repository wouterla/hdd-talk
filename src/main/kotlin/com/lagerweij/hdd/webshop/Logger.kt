package com.lagerweij.hdd.webshop

import com.google.gson.Gson
import io.vertx.core.eventbus.EventBus

data class MsgObj(val values: Map<String, String?>)

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

    fun logJson(logMap: Map<String, String?>) {
        val obj = MsgObj(logMap)
        log(gson.toJson(obj))
    }


}
