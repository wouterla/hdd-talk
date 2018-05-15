package com.lagerweij.hdd.webshop

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.DeploymentOptions
import java.io.File
import java.util.logging.LogManager
import java.util.logging.Logger
import java.util.logging.Level
import io.vertx.core.eventbus.MessageConsumer
import com.google.gson.Gson

@Suppress("unused")
class LoggerVerticle : AbstractVerticle() {

    private val gson = Gson()
    private val logger : Logger = Logger.getLogger(LoggerVerticle::class.simpleName)

    override fun start(startFuture: Future<Void>?) {
        initLogging()

        val eb = vertx.eventBus()

        val consumer: MessageConsumer<String> = eb.consumer("logger")
        consumer.handler({ message ->
            val msgObj = gson.fromJson(message.body(), MsgObj::class.java)
            logger.log(Level.INFO, message.body(), msgObj.values)
        })
    }

    fun initLogging() {
        val logfile = File(System.getProperty("java.util.logging.config.file"))
        if (!logfile.exists()) {
          logfile.createNewFile()
        }
        val inputStream = logfile.inputStream()
        LogManager.getLogManager().readConfiguration(inputStream)
    }

}
