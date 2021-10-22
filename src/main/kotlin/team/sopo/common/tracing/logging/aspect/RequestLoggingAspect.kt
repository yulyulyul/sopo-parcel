package team.sopo.common.tracing.logging.aspect

import feign.Request
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component
import team.sopo.parcel.infrastructure.DeliveryClient

//@Aspect
//@Component
class RequestLoggingAspect {
//    private val logger: Logger = LogManager.getLogger(this.javaClass)
//
//    @Pointcut("execution(* org.springframework.cloud.netflix.feign.ribbon.LoadBalancerFeignClient.*(..)) ")
//    fun feignClientPointcut() {}
//
//    @Around("feignClientPointcut()")
//    @Throws(Throwable::class)
//    fun logAction(joinPoint: ProceedingJoinPoint): Any? {
//
//        try {
//            val client = joinPoint.target
//            if(client !is DeliveryClient){
//                return joinPoint.proceed()
//            }
//
//            val args = joinPoint.args
//            args.forEach {
//                if(it is Request){
//                    val url = it.url()
//                    val body = it.body()
//                }
//            }
//
//
//
//            return joinPoint.proceed()
//        }
//        catch (throwable: Throwable) {
//            logger.error("logger aop: $throwable")
//            throw throwable
//        }
//    }
}