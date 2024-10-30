package com.VCSDTN.fusion.api

import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class Translator(private val apiKey: String) {
    private val client = OkHttpClient()

    fun translateText(text: String, targetLanguage: String, callback: (String?) -> Unit) {
        val url = "https://translation.googleapis.com/language/translate/v2"
        val requestBody = FormBody.Builder()
            .add("q", text)
            .add("target", targetLanguage)
            .add("key", apiKey)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        callback(null)
                        throw IOException("Unexpected code $response")
                    }

                    val jsonData = response.body?.string()
                    if (jsonData != null) {
                        val jsonObject = JSONObject(jsonData)
                        val translatedText = jsonObject.getJSONObject("data")
                            .getJSONArray("translations")
                            .getJSONObject(0)
                            .getString("translatedText")
                        callback(translatedText)
                    } else {
                        callback(null)
                    }
                }
            }
        })
    }
}
