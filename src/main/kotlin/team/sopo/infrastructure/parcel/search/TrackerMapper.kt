package team.sopo.infrastructure.parcel.search

import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy
import team.sopo.domain.parcel.trackinginfo.TrackingInfo
import team.sopo.infrastructure.parcel.tracker.delivery.DeliveryTrackingInfo

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
interface TrackerMapper {
    fun of(trackingInfo: DeliveryTrackingInfo): TrackingInfo
}