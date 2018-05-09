package com.lagerweij.hdd.webshop

import com.google.gson.Gson
import io.vertx.ext.web.Cookie
import io.vertx.ext.web.RoutingContext
import java.util.*

data class Toggle(val name: String, val percentage: Int)

class FeatureToggle(logger: Logger) {
    private var logger: Logger = logger

    private val gson = Gson()

    private val toggles = mapOf<String, Toggle>("timer" to Toggle("timer", 50))

    fun isOn(cookies: Set<Cookie>, name: String) : Boolean? {
        val cookieMap : Map<String, Boolean?> = cookies.associateBy( { it.name }, { java.lang.Boolean.parseBoolean(it.value) })
        return cookieMap[name]
    }

    fun isSet(cookies: Set<Cookie>, name: String) : Boolean {
        val cookieMap : Map<String, Boolean> = cookies.associateBy( { it.name }, { java.lang.Boolean.parseBoolean(it.value) })
        return cookieMap.containsKey(name)
    }

    fun generateToggleValue(name: String) : Boolean {
        val rnd = Random().ints(1, 0, 100 ).findFirst().asInt
        if (rnd > toggles[name]?.percentage ?: 0) {
            return true
        }
        return false
    }

    fun handleFeatureToggles(context: RoutingContext) {
        for (toggleName in toggles.keys) {
            if (isSet(context.cookies(), toggleName)) {

                // Just log if the cookie is already set
                logger.logJson(mapOf("action" to "existing-cookie",
                        "cookie-name" to "timer",
                        "cookie-value" to "${isOn(context.cookies(), toggleName)}",
                        "cookie-statue" to "passed-in"))
            } else {
                // Get a value for the cookie, then set it in the session and log what we did
                val toggleValue = generateToggleValue("timer")
                val newCookie = Cookie.cookie(toggleName, "${toggleValue}")
                context.addCookie(newCookie)
                logger.logJson(mapOf("action" to "set-cookie",
                        "cookie-name" to newCookie.name,
                        "cookie-value" to newCookie.value,
                        "cookie-statue" to "set"))
            }
        }
    }

}