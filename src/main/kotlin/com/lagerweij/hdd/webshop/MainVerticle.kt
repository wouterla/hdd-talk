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
      startLoggingVerticle(startFuture)
      logger = Logger(vertx.eventBus())

      startHttpServer(startFuture)

      toggles = FeatureToggle(logger)
      toggles.addToggle("timer", 50)
    }

    private fun createRouter() = Router.router(vertx).apply {

      route("/node_modules/*").handler(StaticHandler.create("node_modules"))
      get("/status").handler(statusHandler)

      route().handler(CookieHandler.create())
      route().handler(handlerCookieFeatureToggle)

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
      logAction(req, "shop", null)

      req.response().sendFile("webroot/shop.html")
    }

    val handlerBuyButton = Handler<RoutingContext> { req ->
      var jsonData = req.bodyAsJson

      try {
        logAction(req, "buy", jsonData.encode())
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

    private fun logAction(req: RoutingContext, logAction: String, clientMsg: String?) {
      var msgObj: MsgObj
      if (clientMsg != null) {
        msgObj = gson.fromJson(clientMsg, MsgObj::class.java)
      } else {
        msgObj = MsgObj(action = logAction)
      }

      toggles.toggles.forEach() { _, definedToggle ->
        if (toggles.isSet(req.cookies(), definedToggle.name)) {
          msgObj.cookieName = definedToggle.name
          msgObj.cookieValue = getCookieValue(req, definedToggle.name) as String
        }
      }
      logger.log( msgObj )
    }

    private fun startLoggingVerticle(startFuture: Future<Void>?) {
      val options = DeploymentOptions().setWorker(true)
      vertx.deployVerticle("com.lagerweij.logging.LoggerVerticle", options) { res ->
        if (res.succeeded()) {
            startFuture!!.complete()
        } else {
            startFuture!!.fail(res.cause())
        }
      }
    }

    private fun startHttpServer(startFuture: Future<Void>?) {

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
}
