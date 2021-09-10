package team.sopo.common.consts

object ErrorMsg {
    object INVALID_USER{
        const val CANNOT_FIND_KAKAO_USER = "Can not find kakao account by userid"
        const val FIREBASE_EMAIL_NOT_MATCHED = "Email is not matched with firebase info(find by uid)"
        const val KAKAO_EMAIL_NOT_MATCHED = "Email is not matched with kakao info(find by userid)"
        const val NOT_APPROPRIATE_DEVICE_INFO = "deviceInfo is not appropriate"
        const val NOT_APPROPRIATE_JOIN_TYPE = "joinType is not appropriate"
        const val NOT_MATCHED_PASSWORD = "password is not matched"
        const val INVALID_EMAIL_FROM_JWT_TOKEN = "The account is different from account in jwt token"
    }

    object VALIDATION{
        const val KAKAO_USER_ID_IS_EMPTY ="Kakao user id is empty"
        const val INVALID_LENGTH_JOIN_TYPE = "Invalid length(joinType)"
        const val INVALID_LENGTH_DEVICE_INFO = "Invalid length(deviceInfo)"
        const val INVALID_LENGTH_EMAIL = "Invalid length(email)"
        const val INVALID_LENGTH_PASSWORD = "Invalid length(password)"
        const val INVALID_LENGTH_KAKAO_USER_ID = "Invalid length(kakao user id)"
        const val CAN_NOT_FIND_FIREBASE_UID = "Can`t find the firebase uid"
        const val CAN_NOT_FIND_NICK_NAME = "Can`t find the nick name"
        const val CAN_NOT_FIND_EMAIL = "Can`t find the Email"
        const val CAN_NOT_FIND_DEVICE_INFO = "Can`t find the deviceInfo"
        const val CAN_NOT_FIND_JOIN_TYPE = "Can`t find the joinType"
        const val INVALID_EMAIL = "Please provide a valid email address"
        const val INVALID_PARCEL_TYPE = "Please provide a valid Parcel type"
        const val INVALID_INQUIRY_DATE_FORMAT="Invalid inquiryDate format(only yyyyMM)"
    }
}