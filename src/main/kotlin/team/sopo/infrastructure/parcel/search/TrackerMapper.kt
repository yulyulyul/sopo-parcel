package team.sopo.infrastructure.parcel.search

import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy
import team.sopo.domain.parcel.trackinginfo.TrackingInfo

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
interface TrackerMapper {
    fun of(trackingInfo: team.sopo.infrastructure.parcel.tracker.TrackingInfo): TrackingInfo
}