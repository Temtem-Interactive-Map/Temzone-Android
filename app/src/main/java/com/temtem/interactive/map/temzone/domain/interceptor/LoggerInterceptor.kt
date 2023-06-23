package com.temtem.interactive.map.temzone.domain.interceptor

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import okhttp3.logging.HttpLoggingInterceptor.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoggerInterceptor @Inject constructor() : Logger {

    private companion object {
        private const val TAG = "ApiLogger"
    }

    override fun log(message: String) {
        if (message.startsWith("{") || message.startsWith("[")) {
            try {
                val json = JsonParser().parse(message)
                val prettyJson = GsonBuilder().setPrettyPrinting().create().toJson(json)

                Log.d(TAG, prettyJson)
            } catch (m: JsonSyntaxException) {
                Log.d(TAG, message)
            }
        } else {
            Log.d(TAG, message)
        }
    }
}
