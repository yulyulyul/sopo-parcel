package team.sopo.common.tracing

import org.springframework.context.ApplicationContext

class BeanUtils {
    companion object{
        fun getBean(beanName: String): Any {
            val applicationContext: ApplicationContext = ApplicationContextProvider.applicationContext
            return applicationContext.getBean(beanName)
        }
    }
}