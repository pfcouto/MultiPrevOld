package pt.ipleiria.estg.dei.pi.mymultiprev.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
/*
@Provides
@Singleton
fun provideKotlinJsonAdapterFactory(): KotlinJsonAdapterFactory = KotlinJsonAdapterFactory()

@Provides
@Singleton
fun provideMoshi(kotlinJsonAdapterFactory: KotlinJsonAdapterFactory): Moshi = Moshi.Builder()
    .add(kotlinJsonAdapterFactory)
    .build()

@Provides
@Singleton
fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory =
    MoshiConverterFactory.create(moshi)

@Provides
@Singleton
fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
}

@Provides
@Singleton
fun provideOkHttp(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
    OkHttpClient
        .Builder()
        .addInterceptor(httpLoggingInterceptor)
        .build()

@Provides
fun provideAuthInterceptor(sharedPreferencesRepository: SharedPreferencesRepository): Interceptor {
    val token = sharedPreferencesRepository.getToken();
    return Interceptor {
        val request = it.request().newBuilder().addHeader("Authorization","Bearer $token").build()
        it.proceed(request)
    }
}

@Provides
fun provideOkHttp(httpLoggingInterceptor: HttpLoggingInterceptor,authInterceptor: Interceptor): OkHttpClient {
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

    return OkHttpClient.Builder()
        .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        .hostnameVerifier { _, _ -> true }
        .addInterceptor(httpLoggingInterceptor)
        .addInterceptor(authInterceptor)
        .build()
}

@Provides
fun provideRetrofitClient(
    okHttp: OkHttpClient,
    moshiConverterFactory: MoshiConverterFactory
): Retrofit = Retrofit.Builder()
    .addConverterFactory(moshiConverterFactory)
    .client(okHttp)
    .baseUrl(API_BASE_URL)
    .build()


 */
}