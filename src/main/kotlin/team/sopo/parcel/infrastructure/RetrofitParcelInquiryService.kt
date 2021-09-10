package team.sopo.parcel.infrastructure

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import team.sopo.common.config.retrofit.RetrofitConfiguration
import team.sopo.common.enums.ResponseEnum
import team.sopo.common.exception.APIException
import team.sopo.common.extension.checkProgressesSort
import team.sopo.common.extension.removeSpecialCharacter
import team.sopo.parcel.application.InquiryService
import team.sopo.parcel.domain.vo.deliverytracker.DeliveryTrackerApiError
import team.sopo.parcel.domain.vo.deliverytracker.TrackingInfo

@Service
class RetrofitParcelInquiryService(
    @Autowired private val networkManager: RetrofitConfiguration
) : InquiryService {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun inquiryParcel(carrier: String, waybillNum: String): TrackingInfo? {
        return try {
            val response = networkManager
                .getInquiryApi()
                .create(RetrofitParcelInquiryApi::class.java)
                .getTrackingInfo(carrier = carrier, waybillNum = waybillNum)
                .execute()

            if(response.isSuccessful){
                logger.debug("택배 API 요청 결과[성공] => ${response.body()}")
                val trackingInfo = response.body() ?: return null
                return trackingInfo
                        .removeSpecialCharacter()
                        .checkProgressesSort()
            }
            else{
                val errorBody = response.errorBody()?.charStream()?.readText()
                val errorResponse = Gson().fromJson<DeliveryTrackerApiError>(errorBody, object: TypeToken<DeliveryTrackerApiError>(){}.type)
                logger.debug("택배 API 요청 결과[실패] => $errorResponse")
                null
            }
        }
        catch (e: Exception){
            throw APIException(ResponseEnum.FAIL_TO_SEARCH_PARCEL, e.localizedMessage)
        }
    }
}