package com.temtem.interactive.map.temzone.repositories.temzone.retrofit

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject

class ApiLogger @Inject constructor() : HttpLoggingInterceptor.Logger {

    private companion object {
        private const val TAG = "ApiLogger"
    }

    override fun log(message: String) {
        if (message.startsWith("{") || message.startsWith("[")) {
            try {
                val prettyPrintJson =
                    GsonBuilder().setPrettyPrinting().create().toJson(JsonParser().parse(message))

                Log.d(TAG, prettyPrintJson)
            } catch (m: JsonSyntaxException) {
                Log.d(TAG, message)
            }
        } else {
            Log.d(TAG, message)
        }
    }
}
