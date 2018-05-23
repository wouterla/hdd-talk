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
import org.junit.*
import org.junit.runner.RunWith

import com.jayway.restassured.RestAssured
import org.assertj.core.api.Assertions.*



import java.io.IOException
import java.net.ServerSocket

/**
 * This is our JUnit test for our verticle. The test uses vertx-unit, so we declare a custom runner.
 */
@RunWith(VertxUnitRunner::class)
class LogGeneratorIntegrationTest {

  companion object {

    var port: Int = 30114
    var host: String? = "192.168.99.100"

    @BeforeClass @JvmStatic
    fun configureRestAssured() {
      /* var definedHost: String? = System.getenv("test.host") ?: null
      var definedPort: String? = System.getenv("test.port") ?: null

      if (definedPort != null) {
        port = definedPort.toInt();
      } */

      var portProperty = System.getProperty("test.port")
      if (portProperty != null) {
        port = portProperty.toInt()
      }
      var hostProperty = System.getProperty("test.host")
      if (hostProperty != null) {
        host = hostProperty
      }

      RestAssured.baseURI = "http://" + host;
      RestAssured.port = port;
      println("Testing on: " + host + ":" + port)
    }

    @AfterClass @JvmStatic
    fun unconfigureRestAssured() {
      RestAssured.reset();
    }
  }

  private val gson = Gson()

  @Test
  fun generateShopVisit() {

    for (count in 1..300) {
      RestAssured.get("/").then().assertThat().statusCode(200)
    }

  }

  @Test
  fun generateBuyWithTimer() {
    for (count in 1..75) {
      val json = gson.toJson(mapOf("action" to "buy",
        "products" to listOf(1, 2),
        "with_timer" to true,
        "in_time" to true,
        "price" to "150"
      ))
      RestAssured.given().cookie("timer", "true").body(json).request().post("/buy").then().assertThat().statusCode(200)
    }
  }

  @Test
  fun generateBuyWithoutTimer() {
    for (count in 1..10) {
      val json = gson.toJson(mapOf("action" to "buy",
              "products" to listOf(1, 2),
              "with_timer" to false,
              "price" to "150"
      ))
      RestAssured.given().cookie("timer", "false").body(json).request().post("/buy").then().assertThat().statusCode(200)
    }
  }
}
