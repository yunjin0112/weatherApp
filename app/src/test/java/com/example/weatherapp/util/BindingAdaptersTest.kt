package com.example.weatherapp.util

import org.junit.Test
import org.junit.Assert.assertEquals
import java.util.*

internal class BindingAdaptersTest {

    @Test
    fun convertTempToFahrenheit() {
        val temp = 287.62
        val expected = 58.31600000000001

        val result = convertTempToFahrenheit(temp)
        assertEquals(expected, result, 0.000)
    }

    @Test
    fun truncateAfterDot() {
        val num = 524.643
        val expected = "524"

        val resut = com.example.weatherapp.util.truncateAfterDot(num)
        assertEquals(expected,resut)
    }

    @Test
    fun isUnableResolveHost() {
        val errorMessage = "Internal Server Error occurred"
        val expected = true

    val result = isServerError(errorMessage)
    assertEquals(expected, result)
    }

    @Test
    fun convertDateFromTimeStamp() {
        val time: Long = 1687438074
        val expected = Date(1687438074000)

        val result = convertDateFromTimeStamp(time)
        assertEquals(expected, result)

    }
}