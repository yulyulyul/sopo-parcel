package team.sopo.common.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import team.sopo.common.tracing.ApiTracingRepository
import team.sopo.common.tracing.logging.interceptor.ControllerTracingInterceptor

@Configuration
class WebMvcConfiguration(private val apiTracingRepository: ApiTracingRepository): WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(ControllerTracingInterceptor(apiTracingRepository))
    }
}