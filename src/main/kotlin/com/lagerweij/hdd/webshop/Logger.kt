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

    fun log(msgObj: MsgObj) {
      eventBus.send("logger", gson.toJson(msgObj))
    }

}
