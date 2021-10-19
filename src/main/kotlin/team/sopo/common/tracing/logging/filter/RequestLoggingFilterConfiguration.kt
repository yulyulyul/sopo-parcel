package team.sopo.common.tracing.logging.filter

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import team.sopo.common.tracing.ApiTracingRepository

@Configuration
class RequestLoggingFilterConfiguration(private val apiTracingRepository: ApiTracingRepository) {

    @Bean
    fun requestLoggingFilter(): FilterRegistrationBean<SopoRequestLoggingFilter> {
        val filter = SopoRequestLoggingFilter(apiTracingRepository)
        filter.setIncludeHeaders(false)
        filter.setIncludeQueryString(true)
        filter.setIncludePayload(true)
        filter.setIncludeClientInfo(true)
        filter.setMaxPayloadLength(10000)
        filter.setBeforeMessagePrefix("")
        filter.setAfterMessagePrefix("")
        filter.setBeforeMessageSuffix("")
        filter.setAfterMessageSuffix("")
        return FilterRegistrationBean(filter)
    }

}