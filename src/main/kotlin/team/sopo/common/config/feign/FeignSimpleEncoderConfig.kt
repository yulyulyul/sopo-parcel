package team.sopo.common.config.feign

import feign.codec.Encoder
import feign.form.FormEncoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeignSimpleEncoderConfig {
    @Bean
    fun encoder() : Encoder {
        return FormEncoder()
    }
}