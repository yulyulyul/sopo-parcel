package team.sopo.parcel.domain

import org.mapstruct.*
import team.sopo.parcel.ParcelInfo

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
interface ParcelInfoMapper {
    @Mappings(
        Mapping(source = "parcel.id", target = "parcelId"),
        Mapping(target = "carrier", ignore = true)
    )
    fun of(parcel: Parcel): ParcelInfo.Main

    fun of(parcelList: List<Parcel>): List<ParcelInfo.Main>

    companion object {
        @JvmStatic
        @AfterMapping
        fun afterMapping(@MappingTarget parcelDto: ParcelInfo.Main, parcel: Parcel) {
            parcelDto.carrier = Carrier.getCarrierByCode(parcel.carrier)
        }
    }
}