package team.sopo.common.tracing.content

import team.sopo.common.exception.error.ErrorType

data class ApiTracingContent(
    var controller: String = "",
    var method: String = "",
    var mapping_url: String = "",
    var http_method: String = "",
    var user: String = "",
    var payload: String = "",
    var parameter: String = "",
    var error_code: Int? = null,
    var error_type: ErrorType? = null,
    var return_message: String = "",
    var request_url: String = "",
    var http_status: Int? = null
)
