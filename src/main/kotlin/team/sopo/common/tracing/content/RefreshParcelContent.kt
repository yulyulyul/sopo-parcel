package team.sopo.common.tracing.content

data class RefreshParcelContent(
    var return_message: String = "",
    var request_url: String = "",
    var carrier: String = "",
    var waybillNum: String = "",
    var http_status: Int? = null,
    var user: String = "",
)