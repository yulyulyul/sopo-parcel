package team.sopo.domain.parcel

import com.google.gson.Gson
import org.mapstruct.*
import team.sopo.domain.parcel.carrier.Carrier
import team.sopo.domain.parcel.carrier.CarrierStatus
import team.sopo.domain.parcel.trackinginfo.TrackingInfo

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
interface ParcelInfoMapper {
    @Mappings(
        Mapping(source = "parcel.id", target = "parcelId"),
        Mapping(target = "inquiryResult", qualifiedByName = ["convertInquiryResult"]),
        Mapping(target = "carrier", qualifiedByName = ["convertCarrier"]),
        Mapping(target = "status", qualifiedByName = ["convertStatus"])
    )
    fun of(parcel: Parcel): ParcelInfo.Main

    fun of(parcelList: List<Parcel>): List<ParcelInfo.Main>

    fun of(carrier: CarrierStatus): ParcelInfo.CarrierStatus

    fun ofInfo(carrierList: List<CarrierStatus>): List<ParcelInfo.CarrierStatus>

    companion object {

        @JvmStatic
        @Named("convertCarrier")
        fun convertCarrier(carrier: String): Carrier = Carrier.getCarrierByCode(carrier)

        @JvmStatic
        @Named("convertInquiryResult")
        fun convertInquiryResult(inquiryResult: String): TrackingInfo? {
            return if(inquiryResult.isEmpty()){
                null
            }
            else{
                Gson().fromJson(inquiryResult, TrackingInfo::class.java)
            }
        }

        @JvmStatic
        @Named("convertStatus")
        fun convertStatus(status: Parcel.Activeness): Int {
            return if (status== Parcel.Activeness.ACTIVE) {
                1
            } else {
                0
            }
        }
    }
}