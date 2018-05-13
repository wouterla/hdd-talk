package com.lagerweij.hdd.webshop

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.DeploymentOptions
import java.io.File
import java.util.logging.LogManager
import java.util.logging.Logger
import io.vertx.core.eventbus.MessageConsumer

@Suppress("unused")
class LoggerVerticle : AbstractVerticle() {

    private val logger : Logger = Logger.getLogger(LoggerVerticle::class.simpleName)

    override fun start(startFuture: Future<Void>?) {
        initLogging()

        val eb = vertx.eventBus()

        val consumer: MessageConsumer<String> = eb.consumer("logger")
        consumer.handler({ message ->
            logger.info(message.body())
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
