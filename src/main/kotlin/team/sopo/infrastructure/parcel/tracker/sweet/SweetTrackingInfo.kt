package team.sopo.infrastructure.parcel.tracker.sweet

import com.fasterxml.jackson.annotation.JsonProperty
import team.sopo.domain.parcel.Parcel
import team.sopo.domain.parcel.trackinginfo.*
import java.sql.Timestamp
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.streams.toList

data class SweetTrackingInfo(
    @JsonProperty("result") val result: String,
    @JsonProperty("senderName") val senderName: String,
    @JsonProperty("receiverName") val receiverName: String,
    @JsonProperty("itemName") val itemName: String,
    @JsonProperty("invoiceNo") val invoiceNo: String,
    @JsonProperty("receiverAddr") val receiverAddr: String,
    @JsonProperty("orderNumber") val orderNumber: String?,
    @JsonProperty("adUrl") val adUrl: String?,
    @JsonProperty("estimate") val estimate: String?,
    @JsonProperty("level") val level: Int,
    @JsonProperty("complete") val complete: Boolean,
    @JsonProperty("recipient") val recipient: String,
    @JsonProperty("itemImage") val itemImage: String?,
    @JsonProperty("trackingDetails") val trackingDetails: List<TrackingDetail>,
    @JsonProperty("productInfo") val productInfo: String?,
    @JsonProperty("zipCode") val zipCode: String?,
    @JsonProperty("lastDetail") val lastDetail: TrackingDetail,
    @JsonProperty("lastStateDetail") val lastStateDetail: TrackingDetail,
    @JsonProperty("firstDetail") val firstDetail: TrackingDetail,
    @JsonProperty("completeYN") val completeYN: String
) {
    fun toTrackingInfo(code: String): TrackingInfo {
        return TrackingInfo(
            from = From(senderName, null, null),
            to = To(receiverName, null),
            state = toState(level),
            item = itemName,
            progresses = toProgresses(),
            carrier = toCarrier(code)
        )
    }

    private fun toState(level: Int): State {
        return when (level) {
            1 -> State(
                Parcel.DeliveryStatus.INFORMATION_RECEIVED.id,
                Parcel.DeliveryStatus.INFORMATION_RECEIVED.description
            )
            2 -> State(Parcel.DeliveryStatus.IN_TRANSIT.id, Parcel.DeliveryStatus.IN_TRANSIT.description)
            3 -> State(Parcel.DeliveryStatus.IN_TRANSIT.id, Parcel.DeliveryStatus.IN_TRANSIT.description)
            4 -> State(Parcel.DeliveryStatus.IN_TRANSIT.id, Parcel.DeliveryStatus.IN_TRANSIT.description)
            5 -> State(Parcel.DeliveryStatus.OUT_FOR_DELIVERY.id, Parcel.DeliveryStatus.OUT_FOR_DELIVERY.description)
            6 -> State(Parcel.DeliveryStatus.DELIVERED.id, Parcel.DeliveryStatus.DELIVERED.description)
            else -> throw IllegalStateException("1~6을 벗어나는 level을 가지고 있습니다. 확인 요망!")
        }
    }

    private fun toStatus(level: Int): Status {
        return when (level) {
            1 -> Status(
                Parcel.DeliveryStatus.INFORMATION_RECEIVED.id,
                Parcel.DeliveryStatus.INFORMATION_RECEIVED.description
            )
            2 -> Status(Parcel.DeliveryStatus.IN_TRANSIT.id, Parcel.DeliveryStatus.IN_TRANSIT.description)
            3 -> Status(Parcel.DeliveryStatus.IN_TRANSIT.id, Parcel.DeliveryStatus.IN_TRANSIT.description)
            4 -> Status(Parcel.DeliveryStatus.IN_TRANSIT.id, Parcel.DeliveryStatus.IN_TRANSIT.description)
            5 -> Status(Parcel.DeliveryStatus.OUT_FOR_DELIVERY.id, Parcel.DeliveryStatus.OUT_FOR_DELIVERY.description)
            6 -> Status(Parcel.DeliveryStatus.DELIVERED.id, Parcel.DeliveryStatus.DELIVERED.description)
            else -> throw IllegalStateException("1~6을 벗어나는 level을 가지고 있습니다. 확인 요망!")
        }
    }

    private fun toProgresses(): MutableList<Progresses?> {
        return trackingDetails.stream().map { toProgresses(it) }.toList() as MutableList<Progresses?>
    }

    private fun toProgresses(trackingDetail: TrackingDetail): Progresses {
        return Progresses(
            toZonedDateTime(trackingDetail.time),
            Location(trackingDetail.where),
            toStatus(trackingDetail.level),
            trackingDetail.kind
        )
    }

    private fun toZonedDateTime(time: Long): String {
        val localDateTime = Timestamp(time).toLocalDateTime()
        return ZonedDateTime.of(localDateTime, ZoneId.of("Asia/Seoul")).toString().replace("[Asia/Seoul]", "")
    }

    private fun toCarrier(code: String): Carrier {
        return when (code) {
            team.sopo.domain.parcel.carrier.Carrier.EPOST.CODE -> Carrier(
                team.sopo.domain.parcel.carrier.Carrier.EPOST.CODE,
                team.sopo.domain.parcel.carrier.Carrier.EPOST.NAME,
                null
            )
            team.sopo.domain.parcel.carrier.Carrier.HANJINS.CODE -> Carrier(
                team.sopo.domain.parcel.carrier.Carrier.HANJINS.CODE,
                team.sopo.domain.parcel.carrier.Carrier.HANJINS.NAME,
                null
            )
            team.sopo.domain.parcel.carrier.Carrier.CJ_LOGISTICS.CODE -> Carrier(
                team.sopo.domain.parcel.carrier.Carrier.CJ_LOGISTICS.CODE,
                team.sopo.domain.parcel.carrier.Carrier.CJ_LOGISTICS.NAME,
                null
            )
            team.sopo.domain.parcel.carrier.Carrier.LOGEN.CODE -> Carrier(
                team.sopo.domain.parcel.carrier.Carrier.LOGEN.CODE,
                team.sopo.domain.parcel.carrier.Carrier.LOGEN.NAME,
                null
            )
            team.sopo.domain.parcel.carrier.Carrier.LOTTE.CODE -> Carrier(
                team.sopo.domain.parcel.carrier.Carrier.LOTTE.CODE,
                team.sopo.domain.parcel.carrier.Carrier.LOTTE.NAME,
                null
            )
            team.sopo.domain.parcel.carrier.Carrier.CHUNILPS.CODE -> Carrier(
                team.sopo.domain.parcel.carrier.Carrier.CHUNILPS.CODE,
                team.sopo.domain.parcel.carrier.Carrier.CHUNILPS.NAME,
                null
            )
            team.sopo.domain.parcel.carrier.Carrier.DAESIN.CODE -> Carrier(
                team.sopo.domain.parcel.carrier.Carrier.DAESIN.CODE,
                team.sopo.domain.parcel.carrier.Carrier.DAESIN.NAME,
                null
            )
            team.sopo.domain.parcel.carrier.Carrier.KDEXP.CODE -> Carrier(
                team.sopo.domain.parcel.carrier.Carrier.KDEXP.CODE,
                team.sopo.domain.parcel.carrier.Carrier.KDEXP.NAME,
                null
            )
            team.sopo.domain.parcel.carrier.Carrier.CVSNET.CODE -> Carrier(
                team.sopo.domain.parcel.carrier.Carrier.CVSNET.CODE,
                team.sopo.domain.parcel.carrier.Carrier.CVSNET.NAME,
                null
            )
            team.sopo.domain.parcel.carrier.Carrier.HDEXP.CODE -> Carrier(
                team.sopo.domain.parcel.carrier.Carrier.HDEXP.CODE,
                team.sopo.domain.parcel.carrier.Carrier.HDEXP.NAME,
                null
            )
            team.sopo.domain.parcel.carrier.Carrier.CU_POST.CODE -> Carrier(
                team.sopo.domain.parcel.carrier.Carrier.CU_POST.CODE,
                team.sopo.domain.parcel.carrier.Carrier.CU_POST.NAME,
                null
            )
            else -> throw IllegalStateException("정의되지 않은 배송사입니다.")
        }
    }
}