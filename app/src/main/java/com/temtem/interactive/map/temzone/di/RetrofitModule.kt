package com.temtem.interactive.map.temzone.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.temtem.interactive.map.temzone.BuildConfig
import com.temtem.interactive.map.temzone.repositories.temzone.retrofit.ApiLogger
import com.temtem.interactive.map.temzone.repositories.temzone.retrofit.AuthInterceptor
import com.temtem.interactive.map.temzone.repositories.temzone.retrofit.TemzoneApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(apiLogger: ApiLogger): HttpLoggingInterceptor {
        return HttpLoggingInterceptor(apiLogger).setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor)
            .addInterceptor(authInterceptor).build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
    ): Retrofit {
        return Retrofit.Builder().client(okHttpClient).baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(gsonConverterFactory).build()
    }

    @Provides
    @Singleton
    fun provideTemzoneApi(retrofit: Retrofit): TemzoneApi {
        return retrofit.create(TemzoneApi::class.java)
    }
}
