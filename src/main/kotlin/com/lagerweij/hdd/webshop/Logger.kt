package com.lagerweij.hdd.webshop

import com.google.gson.Gson
import io.vertx.core.eventbus.EventBus

class Logger(eventBus: EventBus) {

    private val gson = Gson()
    private var eventBus : EventBus = eventBus

    /**
     * Execute logging asynchronously
     */
    fun log(message: String) {
        eventBus.send("logger", message)
    }

    fun logJson(logMap: Map<String, String?>) {
        log(gson.toJson(logMap))
    }


}