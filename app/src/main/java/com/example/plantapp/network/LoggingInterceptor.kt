package com.example.plantapp.network

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Logger
import okio.Buffer
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class LoggingInterceptor : Interceptor {

    private val logger: Logger = Logger.DEFAULT

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Log yêu cầu
        logger.log("${request.method} ${request.url}")
        logger.log("Headers: ${request.headers}")

        val requestBody = request.body
        if (requestBody != null) {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            val charset: Charset = requestBody.contentType()?.charset(StandardCharsets.UTF_8)
                ?: StandardCharsets.UTF_8
            logger.log("Request Body: ${buffer.readString(charset)}")
        }

        // Gửi yêu cầu và nhận phản hồi
        val response = chain.proceed(request)

        // Log phản hồi
        logger.log("Response Code: ${response.code}")
        logger.log("Headers: ${response.headers}")

        val responseBody = response.body
        if (responseBody != null) {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Đọc toàn bộ nội dung phản hồi
            val buffer = source.buffer
            val charset: Charset = responseBody.contentType()?.charset(StandardCharsets.UTF_8)
                ?: StandardCharsets.UTF_8
            logger.log("Response Body: ${buffer.clone().readString(charset)}")
        }

        return response
    }
}
