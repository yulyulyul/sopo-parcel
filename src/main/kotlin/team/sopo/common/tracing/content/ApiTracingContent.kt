package team.sopo.common.tracing.content

import com.fasterxml.uuid.EthernetAddress
import com.fasterxml.uuid.Generators
import team.sopo.common.exception.error.ErrorType

class ApiTracingContent(
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
){
    private val elasticId: String
    init {
        this.elasticId = generateElasticId()
    }
    private fun generateElasticId(): String {
        return Generators.timeBasedGenerator(EthernetAddress.fromInterface()).generate().toString()
    }
}
