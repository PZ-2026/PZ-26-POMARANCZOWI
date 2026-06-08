package com.example.barbershop.network

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object NetworkClient {
    private var tokenManager: TokenManager? = null
    private var retrofit: Retrofit? = null
    private var okHttpClient: OkHttpClient? = null

    private val _authState = MutableStateFlow<AuthState>(AuthState.LoggedOut)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    sealed class AuthState {
        object LoggedOut : AuthState()
        data class LoggedIn(
            val userId: Long,
            val name: String,
            val email: String,
            val phone: String,
            val role: String
        ) : AuthState()
    }

    fun init(context: Context) {
        Log.d("NetworkClient", "Initializing NetworkClient...")
        val tm = TokenManager(context)
        tokenManager = tm
        
        // Restore auth state from preferences
        if (tm.isLoggedIn()) {
            _authState.value = AuthState.LoggedIn(
                userId = tm.getUserId(),
                name = tm.getUserName() ?: "",
                email = tm.getUserEmail() ?: "",
                phone = tm.getUserPhone() ?: "",
                role = tm.getUserRole() ?: ""
            )
        }

        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("OkHttp", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val trustAllCerts = createTrustAllCerts()
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, SecureRandom())

        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tm))
            .addInterceptor(loggingInterceptor)
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(okHttpClient!!)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        Log.d("NetworkClient", "NetworkClient initialized with baseUrl: ${ApiConfig.BASE_URL}")
    }

    fun saveAuthResponse(authResponse: AuthResponse) {
        tokenManager?.saveAuthResponse(
            token = authResponse.token,
            userId = authResponse.userId,
            name = authResponse.name,
            email = authResponse.email,
            phone = authResponse.phone,
            role = authResponse.role
        )
        _authState.value = AuthState.LoggedIn(
            userId = authResponse.userId,
            name = authResponse.name,
            email = authResponse.email,
            phone = authResponse.phone,
            role = authResponse.role
        )
    }

    fun isLoggedIn(): Boolean {
        return authState.value is AuthState.LoggedIn
    }

    fun logout() {
        tokenManager?.logout()
        _authState.value = AuthState.LoggedOut
    }

    fun currentUser(): AuthState.LoggedIn? {
        return authState.value as? AuthState.LoggedIn
    }

    private fun createTrustAllCerts(): Array<TrustManager> {
        return arrayOf(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })
    }

    val authApi: AuthApi
        get() = retrofit?.create(AuthApi::class.java) ?: throw IllegalStateException("NetworkClient not initialized")

    val barberApi: BarberApi
        get() = retrofit?.create(BarberApi::class.java) ?: throw IllegalStateException("NetworkClient not initialized")

    val serviceApi: ServiceApi
        get() = retrofit?.create(ServiceApi::class.java) ?: throw IllegalStateException("NetworkClient not initialized")

    val appointmentApi: AppointmentApi
        get() = retrofit?.create(AppointmentApi::class.java) ?: throw IllegalStateException("NetworkClient not initialized")

    val userApi: UserApi
        get() = retrofit?.create(UserApi::class.java) ?: throw IllegalStateException("NetworkClient not initialized")
}