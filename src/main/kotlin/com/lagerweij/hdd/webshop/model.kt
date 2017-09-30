package com.lagerweij.hdd.webshop

data class Country(val name: String, val code: String)

data class Island(val name: String, val country: Country)