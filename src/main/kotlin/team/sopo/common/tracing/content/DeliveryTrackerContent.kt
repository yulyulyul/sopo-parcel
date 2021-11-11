package team.sopo.common.tracing.content

import team.sopo.parcel.domain.Carrier

data class DeliveryTrackerContent(
    var client_name: String? = null,
    var method: String? = null,
    var http_method: String? = null,
    var request_url: String? = null,
    var carrier: Carrier? = null,
    var elapsedTime: Long? = null,
    var waybillNum: String? = null,
    var user: String? = null,
    var http_status: Int? = null,
    var return_message: String? = null,
    var exception: String? = null,
    var exception_message: String? = null
)