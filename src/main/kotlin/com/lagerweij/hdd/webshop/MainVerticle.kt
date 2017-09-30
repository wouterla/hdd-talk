package com.lagerweij.hdd.webshop

import com.google.gson.Gson
import io.vertx.core.*
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.ext.web.Cookie
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CookieHandler
import io.vertx.ext.web.handler.StaticHandler
import java.io.File
import java.util.*
import java.util.logging.LogManager
import java.util.logging.Logger

@Suppress("unused")
class MainVerticle : AbstractVerticle() {
    private val logger =
            Logger.getLogger(MainVerticle::class.simpleName)
    private val gson = Gson()

    override fun start(startFuture: Future<Void>?) {
//        DeploymentOptions().setWorker(true)
//        var options = VertxOptions()
//        options.setMaxEventLoopExecuteTime(Long.MAX_VALUE)
//        vertx = Vertx.vertx(options)
        initLogging()

        val router = createRouter()
        router.route().handler(StaticHandler.create())

        vertx.createHttpServer()
                .requestHandler { router.accept(it) }
                .listen(config().getInteger("http.port", 8080)) { result ->
                    if (result.succeeded()) {
                        startFuture?.complete()
                    } else {
                        startFuture?.fail(result.cause())
                    }
                }
    }

    private fun createRouter() = Router.router(vertx).apply {

        route("/node_modules/*").handler(StaticHandler.create("node_modules"))

        route().handler(CookieHandler.create())
        route().handler(handlerCookieFeatureToggle)

        get("/").handler(handlerRoot)

        route("/buy").handler(BodyHandler.create())
        post("/buy").handler(handlerBuyButton)
    }

    // Handlers
    val handlerCookieFeatureToggle = Handler<RoutingContext> { context ->
        var someCookie = context.getCookie("timer")

        var timerToggle = false
        if (someCookie != null) {
            logJson(mapOf("action" to "existing-cookie",
                    "cookie-name" to someCookie.name,
                    "cookie-value" to someCookie.value,
                    "cookie-statue" to "passed-in"))
            var cookieValue = someCookie.getValue()
            try {
                timerToggle = java.lang.Boolean.parseBoolean(cookieValue)
            } catch(e: Exception) {
                timerToggle = false
            }
        } else {
            var rnd = Random().ints(1, 0, 2).findFirst().asInt
            if (rnd > 0) {
                timerToggle = true
            }
        }

        val newCookie = Cookie.cookie("timer", "${timerToggle}")
        // Add a cookie - this will get written back in the response automatically
        context.addCookie(Cookie.cookie("timer", "${timerToggle}"))
        logJson(mapOf("action" to "set-cookie",
                "cookie-name" to someCookie.name,
                "cookie-value" to someCookie.value,
                "cookie-statue" to "set"))

        context.next()
    }

    val handlerRoot = Handler<RoutingContext> { req ->
        logJson(mapOf("action" to "shop",
                "cookie-value" to getCookieValue(req, "timer")))
        req.response().sendFile("webroot/shop.html")
    }



    val handlerBuyButton = Handler<RoutingContext> { req ->
        var jsonData = req.bodyAsJson
        log( jsonData.encode() )

        // Here, we would actually do something

        req.response().end()
    }

    // Utilities
    /**
     * Extension to the HTTP response to output JSON objects.
     */
    fun HttpServerResponse.endWithJson(obj: Any) {
        this.putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(obj))
    }

    /**
     * Execute logging asynchronously
     */
    fun log(message: String) {
        vertx.executeBlocking<String> ({ future ->
            logger.info(message)
            future.complete(message)
        }, { _ ->
            //println("Logging init result is: ${res}")
        })
    }

    fun logJson(logMap: Map<String, String?>) {
        log(gson.toJson(logMap))
    }

    fun initLogging() {
        vertx.executeBlocking<String> ({ future ->
            val inputStream = File(System.getProperty("java.util.logging.config.file")).inputStream()
            LogManager.getLogManager().readConfiguration(inputStream)
            future.complete("Done initializing logging")
        }, { _ -> })
    }

    private fun getCookieValue(req: RoutingContext, cookieName: String): String? {
        var someCookie = req.getCookie(cookieName)
        if (someCookie != null) {
            return someCookie.value
        }
        return null
    }

}