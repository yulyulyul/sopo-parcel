package team.sopo.common.config.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Configuration
class JacksonObjectMapperConfig {

    @Bean
    @Primary
    fun serializingObjectMapper(): ObjectMapper {
        val objectMapper = ObjectMapper()
        val javaTimeModule = JavaTimeModule()
        javaTimeModule.addSerializer(LocalDate::class.java, LocalDateSerializer())
        javaTimeModule.addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer())
        javaTimeModule.addDeserializer(LocalDate::class.java, LocalDateDeserializer())
        javaTimeModule.addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer())
        objectMapper.registerModule(javaTimeModule)
        return objectMapper
    }

    inner class LocalDateTimeSerializer : JsonSerializer<LocalDateTime?>() {
        override fun serialize(value: LocalDateTime?, gen: JsonGenerator?, serializers: SerializerProvider?) {
            gen?.writeString(value?.format(LOCAL_DATE_TIME_FORMATTER))
        }
    }

    inner class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime?>() {
        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): LocalDateTime? {
            return LocalDateTime.parse(p?.valueAsString, LOCAL_DATE_TIME_FORMATTER)
        }
    }

    inner class LocalDateSerializer : JsonSerializer<LocalDate?>() {
        override fun serialize(value: LocalDate?, gen: JsonGenerator?, serializers: SerializerProvider?) {
            gen?.writeString(value?.format(LOCAL_DATE_FORMATTER))
        }
    }

    inner class LocalDateDeserializer : JsonDeserializer<LocalDate?>() {
        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): LocalDate? {
            return LocalDate.parse(p?.valueAsString, LOCAL_DATE_FORMATTER)
        }
    }

    companion object {
        val LOCAL_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val LOCAL_DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }
}