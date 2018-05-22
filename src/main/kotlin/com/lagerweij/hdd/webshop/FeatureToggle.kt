package com.lagerweij.hdd.webshop

import com.google.gson.Gson
import io.vertx.ext.web.Cookie
import io.vertx.ext.web.RoutingContext
import java.util.*
import com.lagerweij.logging.*


data class Toggle(val name: String, val percentage: Int)

class FeatureToggle(logger: Logger) {
    private var logger: Logger = logger

    private val gson = Gson()

    val toggles = mapOf<String, Toggle>("timer" to Toggle("timer", 50))

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
                var msgObj = MsgObj(action = "existing-cookie")
                msgObj.cookieName = toggleName
                msgObj.cookieValue = "${isOn(context.cookies(), toggleName)}"
                msgObj.cookieStatus = "passed-in"
                logger.log(msgObj)
            } else {
                // Get a value for the cookie, then set it in the session
                val toggleValue = generateToggleValue("timer")
                val newCookie = Cookie.cookie(toggleName, "${toggleValue}")
                context.addCookie(newCookie)

                // log that we set the cookie
                var msgObj = MsgObj(action = "set-cookie")
                msgObj.cookieName = newCookie.name
                msgObj.cookieValue = newCookie.value
                msgObj.cookieStatus = "set"
                logger.log(msgObj)
            }
        }
    }

}
