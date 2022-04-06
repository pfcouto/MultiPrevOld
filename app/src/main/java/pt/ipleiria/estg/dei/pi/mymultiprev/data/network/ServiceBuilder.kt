package pt.ipleiria.estg.dei.pi.mymultiprev.data.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.requests.AuthToken
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.services.AuthService
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.AuthRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.SharedPreferencesRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.security.cert.X509Certificate
import javax.inject.Inject
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class ServiceBuilder @Inject constructor(val sharedPreferencesRepository: SharedPreferencesRepository) {
    @Inject
    lateinit var authRepository: AuthRepository

    private fun provideKotlinJsonAdapterFactory(): KotlinJsonAdapterFactory =
        KotlinJsonAdapterFactory()

    private fun provideMoshi(kotlinJsonAdapterFactory: KotlinJsonAdapterFactory): Moshi =
        Moshi.Builder()
            .add(kotlinJsonAdapterFactory)
            .build()

    private fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory =
        MoshiConverterFactory.create(moshi)


    private fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    /*@Provides
    @Singleton
    fun provideOkHttp(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()*/


    private fun provideAuthInterceptor(sharedPreferencesRepository: SharedPreferencesRepository): Interceptor {
        val token = sharedPreferencesRepository.getToken()
        return Interceptor {
            val request =
                it.request().newBuilder().addHeader("Authorization", "Bearer $token").build()
            it.proceed(request)
        }
    }

    private fun provideOkHttp(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: Interceptor,
        isRefresh: Boolean = false
    ): OkHttpClient {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun getAcceptedIssuers() = arrayOf<X509Certificate>()
        })

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory = sslContext.socketFactory

        if (isRefresh) {
            return OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true }
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(authInterceptor)
                .build()
        }
        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(authInterceptor)
            .authenticator(provideAuthenticator())
            .build()
    }


    private fun provideRetrofitClient(
        okHttp: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(moshiConverterFactory)
        .client(okHttp)
        .baseUrl(Constants.API_BASE_URL)
        .build()

    private fun provideAuthenticator(): Authenticator {
        return Authenticator { _, response ->
            val oldToken = sharedPreferencesRepository.getToken()
            val refreshService = buildRefreshService(AuthService::class.java)
            try {
                val newToken =
                    refreshService.refresh(AuthToken(null, null, oldToken, null)).execute()
                        .body()!!.token
                sharedPreferencesRepository.saveToken(newToken!!)
                response.request.newBuilder().header("Authorization", "Bearer $newToken")
                    .build()
            } catch (exception: Exception) {
                null
            }
        }
    }


    fun <API> buildService(serviceType: Class<API>): API {
        return provideRetrofitClient(
            provideOkHttp(
                provideHttpLoggingInterceptor(),
                provideAuthInterceptor(sharedPreferencesRepository)
            ),
            provideMoshiConverterFactory(provideMoshi(provideKotlinJsonAdapterFactory()))
        ).create(serviceType)
    }

    private fun <API> buildRefreshService(serviceType: Class<API>): API {
        return provideRetrofitClient(
            provideOkHttp(
                provideHttpLoggingInterceptor(),
                provideAuthInterceptor(sharedPreferencesRepository),
                true
            ),
            provideMoshiConverterFactory(provideMoshi(provideKotlinJsonAdapterFactory()))
        ).create(serviceType)
    }
}