package com.example.weatherapp.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import com.example.weatherapp.R
import com.example.weatherapp.model.WeatherItem
import com.example.weatherapp.viewmodel.WeatherApiStatus
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("temperature")
fun bindTemperature(textView: TextView, temp: Double) {
    val tempInFahrenheit = convertTempToFahrenheit(temp)
    val tempAsString = tempInFahrenheit.toString().substringBefore(".") + "°F"
    textView.text = tempAsString
}

@BindingAdapter("description")
fun bindWeatherDescription(textView:TextView, data: List<WeatherItem>?) {
    val item = data?.get(0)
    textView.text = item?.description
}

@BindingAdapter("weatherIcon")
fun ImageView.setIcon(data: List<WeatherItem>?) {
    var resId = R.drawable.icon_01d
    data?.let {
        val item = data[0]
        resId = item.iconId.asResourceId()
    }
    setImageResource(resId)
}

@BindingAdapter("day")
fun bindDay(textView: TextView, dateTime: Long) {
    val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
    val day = sdf.format(convertDateFromTimeStamp(dateTime))
    textView.text = day
}

@BindingAdapter("date")
fun bindDate(textView: TextView, dateTime: Long) {
    val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
    val date = sdf.format(convertDateFromTimeStamp(dateTime))
    textView.text = date
}

@SuppressLint("SetTextI18n")
@BindingAdapter("humidity")
fun bindHumidity(textView: TextView, humidity: Double) {
    textView.text = humidity.toString().substringBefore(".") + "%"
}

@BindingAdapter("pressure")
fun bindPressure(textView: TextView, pressure: Double) {
    textView.text = truncateAfterDot(pressure) + " hPa"
}

@BindingAdapter("windSpeed")
fun bindWindSpeed(textView: TextView, wind: Double) {
    textView.text = truncateAfterDot(wind) + " m/s"
}

@BindingAdapter("visibility")
fun bindVisibility(textView: TextView, visibility: Double) {
    val mile = visibility * 0.000621371;
    textView.text = truncateAfterDot(mile) + "mi"
}

@BindingAdapter("time")
fun bindTimeFormatted(textView: TextView, dateTime: Long) {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val time = sdf.format(convertDateFromTimeStamp(dateTime))

    var hour = time.substring(0, 2).toInt()
    var end = "AM"
    if (hour >= 13) {
        hour = hour - 12
        end = "PM"
    }
    var timeDisplayed = hour.toString() + ":" + time.substring(3) + end
    textView.text = timeDisplayed
}

@BindingAdapter("hideSoftInputFromWindow")
fun dismissKeyboard(view: View, isTypingComplete: Boolean) {
    val inputMethodManager =
        view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    if (isTypingComplete)
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

}

@BindingAdapter("toastWarningMessage")
fun displayToastWarning(view: View, isEmpty: Boolean) {
    if (isEmpty) Toast.makeText(view.context, R.string.warning_message, Toast.LENGTH_SHORT).show()
}

@BindingAdapter("apiStatusResultVisibility")
fun bindResultsOnApiStatus(
    view: View,
    status: WeatherApiStatus?
) {
    when (status) {
        WeatherApiStatus.START -> {
            view.visibility = View.GONE
        }
        WeatherApiStatus.LOADING -> {
            view.visibility = View.GONE
        }
        WeatherApiStatus.ERROR -> {
            view.visibility = View.GONE
        }
        WeatherApiStatus.SUCCESS -> {
            view.visibility = View.VISIBLE
        }
        else -> {
            view.visibility = View.GONE
        }
    }
}

@BindingAdapter("visibleOnError")
fun bindStatus(
    statusImageView: ImageView,
    status: WeatherApiStatus?
) {
    when (status) {
        WeatherApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        WeatherApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_error)
        }
        else -> statusImageView.visibility = View.GONE
    }
}

@BindingAdapter("visibleOnError")
fun bindResultOnApiStatus(
    view: View,
    status: WeatherApiStatus?
) {

    when (status) {

        WeatherApiStatus.START -> {
            view.visibility = View.GONE
        }
        WeatherApiStatus.LOADING -> {
            view.visibility = View.GONE
        }
        WeatherApiStatus.ERROR -> {
            view.visibility = View.VISIBLE
        }
        WeatherApiStatus.SUCCESS -> {
            view.visibility = View.GONE
        }
        else -> {
            view.visibility = View.GONE
        }
    }
}

@BindingAdapter("errorText")
fun setErrorText(textView: TextView, errorMessage: String?) {
    var message = textView.context.getString(R.string.something_wrong_error)

    errorMessage?.let {
        if (isServerError(it))
            message = textView.context.getString(R.string.wrong_input_error)
        else if (isUnableResolveHost(it))
            message = textView.context.getString(R.string.no_internet_error)
    }
    textView.text = message
}

fun isServerError(errorMessage: String) = errorMessage.contains("Internal Server Error")

fun isUnableResolveHost(errorMessage: String) = errorMessage.contains("Unable to resolve host")

fun convertTempToFahrenheit(temp: Double) : Double{
    val tempInFahrenheit = (temp - 273) * 9/5 + 32
    return tempInFahrenheit
}

fun truncateAfterDot(num: Double): String {
    return num.toString().substringBefore(".")
}

fun convertDateFromTimeStamp(time: Long): Date {
    val timestamp = Timestamp(time * 1000L)
    return Date(timestamp.time)
}