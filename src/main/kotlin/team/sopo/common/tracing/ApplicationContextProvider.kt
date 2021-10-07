package team.sopo.common.tracing

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
class ApplicationContextProvider: ApplicationContextAware {

    companion object{
        lateinit var applicationContext: ApplicationContext
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        ApplicationContextProvider.applicationContext = applicationContext
    }
}