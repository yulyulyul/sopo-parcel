package team.sopo.common.config.sleuth

import brave.baggage.BaggageField
import brave.baggage.CorrelationScopeConfig
import brave.context.slf4j.MDCScopeDecorator
import brave.propagation.CurrentTraceContext
import org.apache.logging.log4j.LogManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SleuthConfiguration {
    private val logger = LogManager.getLogger(this.javaClass.name)

    @Bean
    fun elasticId(): BaggageField{
        logger.info("sleuthConfiguration => elasticId")
        return BaggageField.create("elasticId")
    }

    @Bean
    fun mdcScopeDecorator(): CurrentTraceContext.ScopeDecorator{
        logger.info("sleuthConfiguration => mdcScopeDecorator")

        return MDCScopeDecorator.newBuilder()
            .clear()
            .add(CorrelationScopeConfig.SingleCorrelationField.newBuilder(elasticId())
                .flushOnUpdate()
                .build())
            .build()
    }
}