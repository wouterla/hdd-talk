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

@Suppress("unused")
class MainVerticle : AbstractVerticle() {
    lateinit var logger: Logger
    private val gson = Gson()

    override fun start(startFuture: Future<Void>?) {
        logger = Logger(vertx.eventBus())

        val options = DeploymentOptions().setWorker(true)
        vertx.deployVerticle("com.lagerweij.hdd.webshop.LoggerVerticle", options) { res ->
            if (res.succeeded()) {
                startFuture!!.complete()
            } else {
                startFuture!!.fail(res.cause())
            }
        }

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
        val toggles = FeatureToggle(logger)

        toggles.handleFeatureToggles(context)

        context.next()
    }

    val handlerRoot = Handler<RoutingContext> { req ->

        logger.log(MsgObj(action = "shop", cookieValue = getCookieValue(req, "timer")))

        req.response().sendFile("webroot/shop.html")
    }

    val handlerBuyButton = Handler<RoutingContext> { req ->
        var jsonData = req.bodyAsJson
        val msgObj = gson.fromJson(jsonData.encode(), MsgObj::class.java)
        logger.log( msgObj )

        // Here, we could actually do something

        req.response().end()
    }

    // Utilities
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
