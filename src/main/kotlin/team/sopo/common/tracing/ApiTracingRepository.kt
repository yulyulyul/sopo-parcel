package team.sopo.common.tracing

import org.springframework.stereotype.Repository
import team.sopo.common.exception.error.ErrorType

@Repository
class ApiTracingRepository: TracingRepository<ApiTracingContent> {
    companion object{
        private val threadLocal: ThreadLocal<ApiTracingContent> = ThreadLocal.withInitial{ ApiTracingContent() }
    }

    fun saveHttpStatus(httpStatus: Int?){
        threadLocal.get().apply {
            this.http_status = httpStatus
        }
    }

    fun saveReturnMessage(returnMessage: String){
        threadLocal.get().apply {
            this.return_message = returnMessage
        }
    }

    fun saveErrorInfo(errorCode: Int, errorType: ErrorType){
        threadLocal.get().apply {
            this.error_code = errorCode
            this.error_type = errorType
        }
    }

    fun saveControllerInfo(controller: String, method: String, mapping_url: String){
        threadLocal.get().apply {
            this.controller = controller
            this.method = method
            this.mapping_url = mapping_url
        }
    }

    fun saveRequestInfo(requestUrl: String, parameter: String, payload: String, httpMethod: String, user: String){
        threadLocal.get().apply {
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
    }

    override fun getContent(): ApiTracingContent {
        return threadLocal.get()
    }

}