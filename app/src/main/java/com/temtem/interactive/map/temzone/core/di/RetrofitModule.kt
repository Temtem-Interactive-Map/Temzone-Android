package com.temtem.interactive.map.temzone.core.di

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.temtem.interactive.map.temzone.BuildConfig
import com.temtem.interactive.map.temzone.data.remote.TemzoneApi
import com.temtem.interactive.map.temzone.domain.interceptor.AuthInterceptor
import com.temtem.interactive.map.temzone.domain.interceptor.CacheInterceptor
import com.temtem.interactive.map.temzone.domain.interceptor.LoggerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
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
    fun provideHttpLoggingInterceptor(loggerInterceptor: LoggerInterceptor): HttpLoggingInterceptor {
        return HttpLoggingInterceptor(loggerInterceptor).setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    fun provideCache(application: Application): Cache {
        return Cache(application.cacheDir, 10 * 1024 * 1024)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
        cache: Cache,
        cacheInterceptor: CacheInterceptor,
    ): OkHttpClient {
        return if (BuildConfig.DEBUG) {
            OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(authInterceptor)
                .addInterceptor(cacheInterceptor)
                .build()
        } else {
            OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(authInterceptor)
                .addInterceptor(cacheInterceptor)
                .build()
        }
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().serializeNulls().create()
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
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideTemzoneApi(retrofit: Retrofit): TemzoneApi {
        return retrofit.create(TemzoneApi::class.java)
    }
}
