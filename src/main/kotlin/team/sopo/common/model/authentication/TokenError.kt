package team.sopo.common.model.authentication

data class TokenError(
    val errorMsg: String,
    val errorCode: String
)