package team.sopo.common.tracing.logging.aspect

import org.apache.logging.log4j.LogManager
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import team.sopo.common.tracing.DeliveryTracing
import team.sopo.common.tracing.DeliveryTrackerRepository
import team.sopo.parcel.domain.Carrier
import team.sopo.parcel.infrastructure.DeliveryClient

@Aspect
@Component
class FeignClientAspect(private val repository: DeliveryTrackerRepository) {

    private val logger = LogManager.getLogger(FeignClientAspect::class)

    @Pointcut("@within(org.springframework.cloud.openfeign.FeignClient)")
    fun feignClientPointcut() {}

    @Before("feignClientPointcut()")
    fun before(joinPoint: JoinPoint) {

        if (joinPoint.target is DeliveryClient) {
            saveUser(repository)
            saveSignature(joinPoint.signature.name, repository)
            saveQueries(joinPoint.args, repository)
        }
    }

    @After("feignClientPointcut()")
    fun after(joinPoint: JoinPoint){
        if (joinPoint.target is DeliveryClient) {
            DeliveryTracing(content = repository.getContent()).trace()
        }
    }

    private fun saveUser(repository: DeliveryTrackerRepository){
        val authentication = SecurityContextHolder.getContext().authentication
        if(authentication.isAuthenticated){
            val user = authentication.principal as String
            repository.saveUser(user)
        }
    }

    private fun saveSignature(method: String, repository: DeliveryTrackerRepository){
        repository.saveSignature(DeliveryClient::class.java.simpleName, method)
    }

    private fun saveQueries(queries: Array<Any>, repository: DeliveryTrackerRepository){
        if(queries.size == 2){
            val carrier = Carrier.getCarrierByCode(queries[0] as String)
            val waybillNum = queries[1] as String

            repository.saveQueries(carrier, waybillNum)
        }
    }

}