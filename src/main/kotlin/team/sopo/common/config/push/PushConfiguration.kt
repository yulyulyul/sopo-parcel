package team.sopo.common.config.push

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.context.annotation.RequestScope
import team.sopo.push.dto.UpdatedParcelInfo

@Configuration
class PushConfiguration {
    @Bean
    @RequestScope
    fun pushList():MutableList<UpdatedParcelInfo>{
        return mutableListOf()
    }
}