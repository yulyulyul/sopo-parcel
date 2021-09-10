package team.sopo.common.exception

import team.sopo.common.enums.ResponseEnum

class APIException : RuntimeException
{
    val responseEnum: ResponseEnum
    val httpStatus: Int
    override val message: String

    constructor(responseCode: ResponseEnum)
    {
        this.responseEnum = responseCode
        this.message = responseCode.MSG
        this.httpStatus = responseCode.HTTP_STATUS
    }

    constructor(responseCode: ResponseEnum, extraMessage: String)
    {
        this.responseEnum = responseCode
        this.message = responseCode.MSG + " : " + extraMessage
        this.httpStatus = responseCode.HTTP_STATUS
    }
}
