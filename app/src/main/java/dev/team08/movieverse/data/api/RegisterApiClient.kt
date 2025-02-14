package dev.team08.movieverse.data.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.InetAddress
import java.net.Socket
import java.util.concurrent.TimeUnit
import javax.net.SocketFactory
import okhttp3.ConnectionSpec
import okhttp3.Protocol
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

object RegisterApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Custom SocketFactory for IPv4
    private class Ipv4PreferredSocketFactory : SocketFactory() {
        private val delegate = getDefault()

        override fun createSocket(): Socket = delegate.createSocket()

        override fun createSocket(host: String, port: Int): Socket {
            return try {
                val address = InetAddress.getByName(host)
                delegate.createSocket(address, port)
            } catch (e: Exception) {
                throw e
            }
        }

        override fun createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int): Socket {
            return try {
                val address = InetAddress.getByName(host)
                delegate.createSocket(address, port, localHost, localPort)
            } catch (e: Exception) {
                throw e
            }
        }

        override fun createSocket(host: InetAddress, port: Int): Socket = delegate.createSocket(host, port)

        override fun createSocket(address: InetAddress, port: Int, localAddress: InetAddress, localPort: Int): Socket =
            delegate.createSocket(address, port, localAddress, localPort)
    }

    private val okHttpClient = OkHttpClient.Builder().apply {
        socketFactory(Ipv4PreferredSocketFactory())
        protocols(listOf(Protocol.HTTP_1_1))
        connectionSpecs(listOf(ConnectionSpec.CLEARTEXT, ConnectionSpec.MODERN_TLS))
        addInterceptor(loggingInterceptor)
        connectTimeout(30, TimeUnit.SECONDS)
        readTimeout(30, TimeUnit.SECONDS)
        writeTimeout(30, TimeUnit.SECONDS)
        retryOnConnectionFailure(true)
        followRedirects(true)
        followSslRedirects(true)
    }.build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val registerApiService: RegisterApiService = retrofit.create(RegisterApiService::class.java)
}
