package team.sopo.domain.parcel

import java.lang.NullPointerException

enum class Carrier(val CODE: String, val NAME: String) {
    CHUNILPS("kr.chunilps", "천일택배"),
    CJ_LOGISTICS("kr.cjlogistics", "CJ대한통운"),
    CU_POST("kr.cupost", "CU 편의점 택배"),
    CVSNET("kr.cvsnet", "GS Postbox 택배"),
    DAESIN("kr.daesin", "대신택배"),
    EPOST("kr.epost", "우체국택배"),
    HANJINS("kr.hanjin", "한진택배"),
    HDEXP("kr.hdexp", "합동택배"),
    KDEXP("kr.kdexp", "경동택배"),
    LOGEN("kr.logen", "로젠택배"),
    LOTTE("kr.lotte", "롯데택배");

    companion object {
        fun getCarrierByCode(code: String): Carrier {
            val carrier = values().findLast {
                it.CODE == code
            }
            carrier ?: throw NullPointerException("정의되지 않은 배송사입니다.")
            return carrier
        }
    }
}