package team.sopo.parcel.presentation

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.ReportingPolicy
import team.sopo.parcel.domain.ParcelInfo

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
interface ParcelDtoMapper {
    fun of(parcelInfo: ParcelInfo.Main): ParcelDto.Main
    fun of(parcelInfos: List<ParcelInfo.Main>): List<ParcelDto.Main>

    @Mapping(source = "updated", target = "isUpdated")
    fun toResponse(refreshedParcel: ParcelInfo.RefreshedParcel): ParcelDto.RefreshResponse
    fun toResponse(parcelUsage: ParcelInfo.ParcelUsage): ParcelDto.ParcelUsageResponse

    @Mappings(
        Mapping(source = "time", target = "_time"),
        Mapping(source = "count", target = "_count")
    )
    fun toResponse(monthlyParcelCnt: ParcelInfo.MonthlyParcelCnt): ParcelDto.MonthlyParcelCntResponse
    fun toResponse(monthlyParcelCnts: List<ParcelInfo.MonthlyParcelCnt>): List<ParcelDto.MonthlyParcelCntResponse>
}