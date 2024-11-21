package com.test.weatherapp1.di.module

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.test.weatherapp1.R
import com.test.weatherapp1.data.api.NetworkService
import com.test.weatherapp1.data.repository.NetworkRepository
import com.test.weatherapp1.data.repository.NetworkRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val HTTP_TIMEOUT: Long = 60 // SECONDS

/**
 * Hilt module that provides network-related dependencies such as `OkHttpClient`,
 * `Retrofit`, and repository implementations.
 *
 * This module is installed in the `SingletonComponent`, ensuring that the provided
 * dependencies are scoped to the application's lifecycle.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideHttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient {
        val okhttpBuilder = OkHttpClient.Builder().connectTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(ChuckerInterceptor(context))
        return okhttpBuilder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder().baseUrl(context.getString(R.string.HTTPS_API_URL))
            .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build()

    @Provides
    @Singleton
    fun provideNetworkService(retrofit: Retrofit): NetworkService =
        retrofit.create(NetworkService::class.java)

    @Provides
    @Singleton
    fun provideNetworkRepository(
        vendorLoginService: NetworkService
    ): NetworkRepository = NetworkRepositoryImpl(vendorLoginService)
}
