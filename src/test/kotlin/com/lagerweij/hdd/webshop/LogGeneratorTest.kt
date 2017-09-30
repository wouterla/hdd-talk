package com.lagerweij.hdd.webshop

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.unit.Async
import io.vertx.ext.unit.TestContext
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
class MyFirstVerticleTest {

    private var vertx: Vertx? = null
    private var port: Int? = null

    /**
     * Before executing our test, let's deploy our verticle.
     *
     *
     * This method instantiates a new Vertx and deploy the verticle. Then, it waits in the verticle has successfully
     * completed its start sequence (thanks to `context.asyncAssertSuccess`).
     *
     * @param context the test context.
     */
    @Before
    @Throws(IOException::class)
    fun setUp(context: TestContext) {
        vertx = Vertx.vertx()

        // Let's configure the verticle to listen on the 'test' port (randomly picked).
        // We create deployment options and set the _configuration_ json object:
        val socket = ServerSocket(0)
        port = socket.localPort
        socket.close()

        val options = DeploymentOptions()
                .setConfig(JsonObject().put("http.port", port)
                )

        // We pass the options as the second parameter of the deployVerticle method.
        vertx!!.deployVerticle(MyFirstVerticle::class.java!!.getName(), options, context.asyncAssertSuccess())
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

    /**
     * Let's ensure that our application behaves correctly.
     *
     * @param context the test context
     */
    @Test
    fun testMyApplication(context: TestContext) {
        // This test is asynchronous, so get an async handler to inform the test when we are done.
        val async = context.async()

        // We create a HTTP client and query our application. When we get the response we check it contains the 'Hello'
        // message. Then, we call the `complete` method on the async handler to declare this async (and here the test) done.
        // Notice that the assertions are made on the 'context' object and are not Junit assert. This ways it manage the
        // async aspect of the test the right way.
        vertx!!.createHttpClient().getNow(port!!, "localhost", "/") { response ->
            response.handler { body ->
                context.assertTrue(body.toString().contains("Hello"))
                async.complete()
            }
        }
    }

    @Test
    fun checkThatTheIndexPageIsServed(context: TestContext) {
        val async = context.async()
        vertx!!.createHttpClient().getNow(port!!, "localhost", "/assets/index.html") { response ->
            context.assertEquals(response.statusCode(), 200)
            context.assertEquals(response.headers().get("content-type"), "text/html;charset=UTF-8")
            response.bodyHandler { body ->
                context.assertTrue(body.toString().contains("<title>My Whisky Collection</title>"))
                async.complete()
            }
        }
    }

    @Test
    fun checkThatWeCanAdd(context: TestContext) {
        val async = context.async()
        val json = Json.encodePrettily(Whisky("Jameson", "Ireland"))
        vertx!!.createHttpClient().post(port!!, "localhost", "/api/whiskies")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", Integer.toString(json.length))
                .handler { response ->
                    context.assertEquals(response.statusCode(), 201)
                    context.assertTrue(response.headers().get("content-type").contains("application/json"))
                    response.bodyHandler { body ->
                        val whisky = Json.decodeValue(body.toString(), Whisky::class.java)
                        context.assertEquals(whisky.getName(), "Jameson")
                        context.assertEquals(whisky.getOrigin(), "Ireland")
                        context.assertNotNull(whisky.getId())
                        async.complete()
                    }
                }
                .write(json)
                .end()
    }
}