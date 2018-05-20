package com.lagerweij.hdd.webshop

import com.google.gson.Gson
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.unit.Async
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.Repeat
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import java.io.IOException
import java.net.ServerSocket

/**
 * This is our JUnit test for our verticle. The test uses vertx-unit, so we declare a custom runner.
 */
@RunWith(VertxUnitRunner::class)
class LogGeneratorTest {

    private var vertx: Vertx? = null
    private var port: Int? = null
    private var host: String? = null

    private val gson = Gson()


    /**
     * Before executing our test, let's deploy our verticle.
     *
     *
     * This method instantiates a new Vertx and deploy the verticle. Then, it waits in the verticle has successfully
     * completed its start sequence (thanks to `context.asyncAssertSuccess`).
     *
     * @param context the test context.
     */
    /* @Before
    @Throws(IOException::class)
    fun setUp(context: TestContext) {

      var definedHost: String? = System.getenv("test.host") ?: null
      var definedPort: String? = System.getenv("test.port") ?: null
      if (definedPort != null) {
        host = definedHost
        port = definedPort.toInt()
      } else {
        host = "localhost"

        val socket = ServerSocket(0)
        port = socket.localPort
        socket.close()

        vertx = Vertx.vertx()

        // Let's configure the verticle to listen on the 'test' port (randomly picked).
        // We create deployment options and set the _configuration_ json object:

        val options = DeploymentOptions()
          .setConfig(JsonObject().put("http.port", port))

        // We pass the options as the second parameter of the deployVerticle method.
        vertx!!.deployVerticle(MainVerticle::class.qualifiedName, options, context.asyncAssertSuccess())
      }
    }

    /**
     * This method, called after our test, just cleanup everything by closing the vert.x instance
     *
     * @param context the test context
     */
    @After
    fun tearDown(context: TestContext) {
        vertx!!.close(context.asyncAssertSuccess())
    }

    @Test
    fun generateShopVisit(context: TestContext) {

        for (count in 1..300) {
            val async = context.async()

            vertx!!.createHttpClient().getNow(port!!, host, "/") { response ->
                context.assertEquals(response.statusCode(), 200)
                async.complete()
            }
        }
    }

    @Test
    fun generateBuyWithTimer(context: TestContext) {
        for (count in 1..30) {
            val async = context.async()
            val json = gson.toJson(mapOf("action" to "buy",
                    "products" to listOf(1, 2),
                    "with_timer" to true,
                    "in_time" to true,
                    "price" to "150"
            ))
            vertx!!.createHttpClient().post(port!!, host, "/buy")
                    .putHeader("content-type", "application/json")
                    .putHeader("content-length", Integer.toString(json.length))
                    .handler { response ->
                        context.assertEquals(response.statusCode(), 200)
                        response.bodyHandler { _ ->
                            async.complete()
                        }
                    }
                    .write(json)
                    .end()
        }
    }

    @Test
    fun generateBuyWithoutTimer(context: TestContext) {
        for (count in 1..20) {
            val async = context.async()
            val json = gson.toJson(mapOf("action" to "buy",
                    "products" to listOf(1, 2),
                    "with_timer" to false,
                    "price" to "150"
            ))
            vertx!!.createHttpClient().post(port!!, "localhost", "/buy")
                    .putHeader("content-type", "application/json")
                    .putHeader("content-length", Integer.toString(json.length))
                    .handler { response ->
                        context.assertEquals(response.statusCode(), 200)
                        response.bodyHandler { _ ->
                            async.complete()
                        }
                    }
                    .write(json)
                    .end()
        }
    } */

//    /**
//     * Let's ensure that our application behaves correctly.
//     *
//     * @param context the test context
//     */
//    @Test
//    fun testMyApplication(context: TestContext) {
//        // This test is asynchronous, so get an async handler to inform the test when we are done.
//        val async = context.async()
//
//        // We create a HTTP client and query our application. When we get the response we check it contains the 'Hello'
//        // message. Then, we call the `complete` method on the async handler to declare this async (and here the test) done.
//        // Notice that the assertions are made on the 'context' object and are not Junit assert. This ways it manage the
//        // async aspect of the test the right way.
//        vertx!!.createHttpClient().getNow(port!!, "localhost", "/") { response ->
//            response.bodyHandler { body ->
//                context.assertTrue(body.toString().contains("<h1>My Book Shop</h1>"))
//                async.complete()
//            }
//        }
//    }
//
//    @Test
//    fun checkThatTheIndexPageIsServed(context: TestContext) {
//        val async = context.async()
//        vertx!!.createHttpClient().getNow(port!!, "localhost", "/") { response ->
//            context.assertEquals(response.statusCode(), 200)
//            response.bodyHandler { body ->
//                context.assertTrue(body.toString().contains("<title>My book shop</title>"))
//                async.complete()
//            }
//        }
//    }
//
//    @Test
//    fun checkThatWeCanBuy(context: TestContext) {
//        val async = context.async()
//        val json = gson.toJson(mapOf("action" to "buy",
//                "products" to "[ 1, 2 ]",
//                "with_timer" to true,
//                "in_time" to true,
//                "price" to "150"
//                ))
//        vertx!!.createHttpClient().post(port!!, "localhost", "/buy")
//                .putHeader("content-type", "application/json")
//                .putHeader("content-length", Integer.toString(json.length))
//                .handler { response ->
//                    context.assertEquals(response.statusCode(), 200)
//                    response.bodyHandler { body ->
//                        async.complete()
//                    }
//                }
//                .write(json)
//                .end()
//    }
}
