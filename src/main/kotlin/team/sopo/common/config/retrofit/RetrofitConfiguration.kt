package team.sopo.common.config.retrofit

import team.sopo.common.network.BasicAuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Configuration
class RetrofitConfiguration {

    @Value("\${sopo.apigateway.url.base}")
    private lateinit var baseUrl: String

    @Value("\${sopo.id}")
    private lateinit var apiSvId: String

    @Value("\${sopo.pwd}")
    private lateinit var apiSvPwd: String

    @Value("\${delivery.track.url.main}")
    private lateinit var inquiryUrl: String


    private fun getHttpLoggingInterceptor(): HttpLoggingInterceptor{
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Bean
    fun getSopoApi(): Retrofit{
        val basicAuthInterceptor = BasicAuthInterceptor(apiSvId, apiSvPwd)

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(OkHttpClient
                .Builder()
                .addInterceptor(basicAuthInterceptor)
                .build()
            )
            .build()
    }

    @Bean
    fun getInquiryApi(): Retrofit{
        return Retrofit.Builder()
            .baseUrl(inquiryUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(OkHttpClient
                .Builder()
                .addInterceptor(getHttpLoggingInterceptor())
                .build()
            )
            .build()
    }
}