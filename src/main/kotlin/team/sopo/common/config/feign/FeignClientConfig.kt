package team.sopo.common.config.feign

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.cloud.openfeign.FeignFormatterRegistrar
import org.springframework.context.annotation.Bean
import org.springframework.format.FormatterRegistry
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import java.util.*

class FeignClientConfig: Jackson2ObjectMapperBuilderCustomizer {

    @Bean
    fun localDateFeignFormatterRegister(): FeignFormatterRegistrar {
        return FeignFormatterRegistrar { registry: FormatterRegistry ->
            val registrar = DateTimeFormatterRegistrar()
            registrar.setUseIsoFormat(true)
            registrar.registerFormatters(registry)
        }
    }

    override fun customize(jacksonObjectMapperBuilder: Jackson2ObjectMapperBuilder?) {
        if(jacksonObjectMapperBuilder == null)
            throw NullPointerException("jacksonObjectMapperBuilder is null")

        jacksonObjectMapperBuilder
            .featuresToEnable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
            .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .timeZone(TimeZone.getDefault())
            .modulesToInstall(JavaTimeModule())
            .locale(Locale.getDefault())
            .simpleDateFormat("yyyy-MM-dd HH:mm:ss")
    }
}