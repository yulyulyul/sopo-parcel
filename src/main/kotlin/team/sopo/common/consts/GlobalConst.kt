package team.sopo.common.consts

object GlobalConst {

    object API_PARAM{
        const val JOIN_SELF_TYPE = "self"
        const val JOIN_KAKAO_TYPE = "kakao"
    }

    object Example{

        object SELF{
            const val ACCOUNT = "sopotest@sooopo.com"
            const val PWD = "Password1234!!"
            const val DEVICE_INFO = "Galaxy S9"
            const val JOIN_TYPE = "self"
            const val FIREBASE_UID = "FQjyMlaCx6MMztHMR9iGcvtyzg63"
        }

        object KAKAO_USER1{
            const val ACCOUNT = "asle1221@naver.com"
            const val PWD = "Password1234!!"
            const val DEVICE_INFO = "Galaxy S9"
            const val JOIN_TYPE = "kakao"
            const val USER_ID = "1388739117"
            const val FIREBASE_UID = "ED5ED778EE7364D123B0FED932E98AA24E486BA0C1FD1C5EF1F365E9771FFE88"
        }
    }

    object JoinType{
        const val KAKAO = "kakao"
        const val SELF = "self"
    }

    object JwtToken{
        object UpdateDeviceInfo{
            const val USER = "USER"
            const val TARGET = "TARGET"
        }
    }

}