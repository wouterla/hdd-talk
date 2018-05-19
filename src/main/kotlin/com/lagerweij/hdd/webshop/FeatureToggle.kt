package com.lagerweij.hdd.webshop

import com.google.gson.Gson
import io.vertx.ext.web.Cookie
import io.vertx.ext.web.RoutingContext
import java.util.*
import com.lagerweij.logging.MsgObj


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
                logger.log(MsgObj(action = "existing-cookie",
                        cookieName = "timer",
                        cookieValue = "${isOn(context.cookies(), toggleName)}",
                        cookieStatus = "passed-in"))
            } else {
                // Get a value for the cookie, then set it in the session and log what we did
                val toggleValue = generateToggleValue("timer")
                val newCookie = Cookie.cookie(toggleName, "${toggleValue}")
                context.addCookie(newCookie)
                logger.log(MsgObj(action = "set-cookie",
                        cookieName = newCookie.name,
                        cookieValue = newCookie.value,
                        cookieStatus = "set"))
            }
        }
    }

}
