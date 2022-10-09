package team.sopo.common.tracing.content

import feign.Request
import team.sopo.domain.parcel.carrier.Carrier

data class DeliveryTrackerContent(
    var api_id: String,
    var http_method: String? = null,
    var request_url: String? = null,
    var carrier: Carrier,
    var elapsedTime: Long? = null,
    var waybillNum: String,
    var userToken: String,
    var http_status: Int? = null,
    var return_message: String? = null,
    var exception: String? = null,
    var exception_message: String? = null
){
    fun updateResponseInfo(elapsedTime: Long, httpStatus: Int, returnMessage: String){
        this.elapsedTime = elapsedTime
        this.http_status = httpStatus
        this.return_message = returnMessage
    }

    fun updateRequestInfo(requestUrl: String, httpMethod: Request.HttpMethod){
        this.request_url = requestUrl
        this.http_method = httpMethod.toString()
    }

    fun updateErrorInfo(exception: String, exception_message: String?){
        this.exception = exception
        this.exception_message = exception_message
    }
}