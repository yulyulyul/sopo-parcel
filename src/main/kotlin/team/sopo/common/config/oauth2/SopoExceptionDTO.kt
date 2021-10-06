package team.sopo.common.config.oauth2

data class SopoExceptionDTO(
    val error: String,
    val error_description: String,
    val sopoErrorCode: String
)