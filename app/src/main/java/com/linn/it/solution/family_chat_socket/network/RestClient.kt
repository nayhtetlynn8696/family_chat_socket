package com.linn.it.solution.family_chat_socket.network

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class RestClient {
    companion object {

        private fun getClient(): OkHttpClient {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            return OkHttpClient.Builder()
                .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                    val origin: Request = chain.request()
                    val requestBuilder: Request.Builder = origin.newBuilder()
                        .addHeader("Accept", "application/json")
                        .addHeader("Accept", "text/plain")
                    val request: Request = requestBuilder.build()
                    chain.proceed(request)
                })
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()
        }

        private fun getInstance(): Retrofit {
            val gSon = GsonBuilder()
                .setLenient()
                .create()

            return Retrofit.Builder()
                .baseUrl("http://192.168.144.45/RestaurantOrderingSystem/") //Mpt=http://192.168.0.195/RestaurantOrderingSystem/  employee=http://192.168.144.94/RestaurantOrderingSystem/
                .client(getClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gSon))
                .build()
        }

        fun getApiService(): ApiService {
            return getInstance().create(ApiService::class.java)
        }
    }
}