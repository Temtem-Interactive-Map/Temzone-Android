package com.temtem.interactive.map.temzone.data.remote

import com.temtem.interactive.map.temzone.domain.repository.auth.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import javax.inject.Inject
import javax.inject.Singleton

@Target(AnnotationTarget.FUNCTION)
annotation class AUTHENTICATED

@Singleton
class AuthInterceptor @Inject constructor(
    private val authRepository: AuthRepository,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val invocation = request.tag(Invocation::class.java) ?: return chain.proceed(request)
        val authenticate = invocation.method().annotations.any {
            it.annotationClass == AUTHENTICATED::class
        }

        return if (authenticate) {
            val token = runBlocking {
                return@runBlocking authRepository.getAuthToken()
            }

            chain.proceed(
                request.newBuilder().addHeader("Authorization", "Bearer $token").build()
            )
        } else {
            chain.proceed(request)
        }
    }
}