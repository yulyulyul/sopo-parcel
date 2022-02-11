package team.sopo.parcel.domain

import com.google.gson.Gson
import org.mapstruct.*
import team.sopo.parcel.domain.trackinginfo.TrackingInfo

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
interface ParcelInfoMapper {
    @Mappings(
        Mapping(source = "parcel.id", target = "parcelId"),
        Mapping(target = "inquiryResult", ignore = true),
        Mapping(target = "carrier", ignore = true),
        Mapping(target = "status", ignore = true)
    )
    fun of(parcel: Parcel): ParcelInfo.Main

    fun of(parcelList: List<Parcel>): List<ParcelInfo.Main>

    companion object {
        @JvmStatic
        @AfterMapping
        fun afterMapping(@MappingTarget parcelInfo: ParcelInfo.Main, parcel: Parcel) {

            parcelInfo.inquiryResult = Gson().fromJson(parcel.inquiryResult, TrackingInfo::class.java)

            parcelInfo.status = if (parcel.isActivate()) {
                1
            } else {
                0
            }

            parcelInfo.carrier = Carrier.getCarrierByCode(parcel.carrier)
        }
    }
}