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
import com.lagerweij.logging.MsgObj
import com.lagerweij.logging.Logger

@Suppress("unused")
class MainVerticle : AbstractVerticle() {
    lateinit var logger: Logger
    lateinit var toggles: FeatureToggle

    private val gson = Gson()

    override fun start(startFuture: Future<Void>?) {
        logger = Logger(vertx.eventBus())
        toggles = FeatureToggle(logger)

        val options = DeploymentOptions().setWorker(true)
        vertx.deployVerticle("com.lagerweij.logging.LoggerVerticle", options) { res ->
            if (res.succeeded()) {
                startFuture!!.complete()
            } else {
                startFuture!!.fail(res.cause())
            }
        }

        val router = createRouter()
        router.route().handler(StaticHandler.create())

        var port = 8080
        var portProperty = System.getProperty("vertx.port")
        if (portProperty != null) {
          port = portProperty.toInt()
        }
        println("Starting on port: " + port)

        vertx.createHttpServer()
                .requestHandler { router.accept(it) }
                .listen(config().getInteger("http.port", port)) { result ->
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

        get("/status").handler(statusHandler)

        get("/").handler(handlerRoot)

        route("/buy").handler(BodyHandler.create())
        post("/buy").handler(handlerBuyButton)
    }

    // Handlers
    val handlerCookieFeatureToggle = Handler<RoutingContext> { context ->

        toggles.handleFeatureToggles(context)

        context.next()
    }

    val statusHandler = Handler<RoutingContext> { req ->
      req.response().end()
    }

    val handlerRoot = Handler<RoutingContext> { req ->

        val msgObj = MsgObj(action = "shop")
        msgObj.cookieValue = getCookieValue(req, "timer") as String
        logger.log(msgObj)

        req.response().sendFile("webroot/shop.html")
    }

    val handlerBuyButton = Handler<RoutingContext> { req ->
        var jsonData = req.bodyAsJson

        try {
          var msgObj = gson.fromJson(jsonData.encode(), MsgObj::class.java)
          msgObj.cookieName = "timer"
          msgObj.cookieValue = getCookieValue(req, "timer") as String

          logger.log( msgObj )
        } catch (e: Exception) {
            logger.log("Exception buying: " + e.toString())
        }

        // Here, we could actually do something to make money!

        req.response().end()
    }

    // Utility methods
    /**
     * Extension to the HTTP response to output JSON objects.
     */
    fun HttpServerResponse.endWithJson(obj: Any) {
        this.putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(obj))
    }

    private fun getCookieValue(req: RoutingContext, cookieName: String): String? {
        var someCookie = req.getCookie(cookieName)
        if (someCookie != null) {
            return someCookie.value
        }
        return null
    }
}
