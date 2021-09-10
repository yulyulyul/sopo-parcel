package team.sopo.parcel.infrastructure

import team.sopo.parcel.domain.vo.deliverytracker.TrackingInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitParcelInquiryApi {
    @GET("{carrier}/tracks/{waybillNum}")
    fun getTrackingInfo(
        @Path("carrier") carrier : String,
        @Path("waybillNum") waybillNum: String
    ): Call<TrackingInfo?>
}