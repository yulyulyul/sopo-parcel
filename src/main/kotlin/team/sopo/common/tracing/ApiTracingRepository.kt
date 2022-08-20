package team.sopo.common.tracing

import org.springframework.stereotype.Repository
import org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST
import org.springframework.web.context.request.RequestContextHolder
import team.sopo.common.exception.error.ErrorType
import team.sopo.common.tracing.content.ApiTracingContent
import java.lang.Exception

@Repository
class ApiTracingRepository: TracingRepository<ApiTracingContent> {

    companion object{
        private const val API_TRACING_CONTENT = "apiTracingContent"
    }

    fun saveHttpStatus(httpStatus: Int?){
        val content = get().apply {
            this.http_status = httpStatus
        }
        save(content)
    }

//    fun saveReturnMessage(returnMessage: String){
//        val content = get().apply {
//            this.return_message = returnMessage
//        }
//        save(content)
//    }

    fun saveErrorInfo(errorCode: Int, errorType: ErrorType, exception: Exception, exceptionMessage: String){
        val content = get().apply {
            this.error_code = errorCode
            this.error_type = errorType
            this.exception = exception::class.java.simpleName
            this.exception_message = exceptionMessage
        }
        save(content)
    }

    fun saveControllerInfo(controller: String, method: String, mapping_url: String){
        val content = get().apply {
            this.controller = controller
            this.method = method
            this.mapping_url = mapping_url
        }
        save(content)
    }

    fun saveRequestInfo(requestUrl: String, parameter: String, payload: String, httpMethod: String, user: String){
        val content = get().apply {
            if(this.request_url.isEmpty()){
                this.request_url = requestUrl
            }
            if(this.parameter.isEmpty()){
                this.parameter = parameter
            }
            if(this.payload.isEmpty()){
                this.payload = payload
            }
            if(this.http_method.isEmpty()){
                this.http_method = httpMethod
            }
            if(this.user.isEmpty()){
                this.user = user
            }
        }
        save(content)
    }

    override fun getContent(): ApiTracingContent {
        return get()
    }

    private fun get(): ApiTracingContent{
        val attribute = RequestContextHolder.getRequestAttributes()?.getAttribute(API_TRACING_CONTENT, SCOPE_REQUEST)
        if(attribute == null){
            save(ApiTracingContent())
            return get()
        }
        if(attribute is ApiTracingContent){
            return attribute
        }
        throw IllegalStateException()
    }

    override fun save(content: ApiTracingContent){
        RequestContextHolder.getRequestAttributes()?.setAttribute(API_TRACING_CONTENT, content, SCOPE_REQUEST)
    }
}