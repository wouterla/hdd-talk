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

      RestAssured.baseURI = "http://" + host;
      RestAssured.port = port;
    }

    @AfterClass @JvmStatic
    fun unconfigureRestAssured() {
      RestAssured.reset();
    }
  }

  private val gson = Gson()

  @Test
  fun generateShopVisit(context: TestContext) {

    for (count in 1..300) {
      RestAssured.get("/").then().assertThat().statusCode(200)
    }

  }

  @Test
  fun generateBuyWithTimer(context: TestContext) {
    for (count in 1..50) {
      val json = gson.toJson(mapOf("action" to "buy",
        "products" to listOf(1, 2),
        "with_timer" to true,
        "in_time" to true,
        "price" to "150"
      ))
      RestAssured.given().body(json).request().post("/buy").then().assertThat().statusCode(200)
    }
  }

  @Test
  fun generateBuyWithoutTimer(context: TestContext) {
    for (count in 1..20) {
      val json = gson.toJson(mapOf("action" to "buy",
              "products" to listOf(1, 2),
              "with_timer" to false,
              "price" to "150"
      ))
      RestAssured.given().body(json).request().post("/buy").then().assertThat().statusCode(200)
    }
  }
}
