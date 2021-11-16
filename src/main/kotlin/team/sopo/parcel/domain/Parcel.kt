package team.sopo.parcel.domain

import com.google.gson.Gson
import team.sopo.common.extension.asHex
import team.sopo.parcel.domain.vo.deliverytracker.TrackingInfo
import java.security.MessageDigest
import java.time.ZonedDateTime
import javax.persistence.*

@Entity
@Table(name = "parcel")
class Parcel() : AbstractEntity() {

    enum class DeliveryStatus {
        NOT_REGISTERED,
        CHANGED,
        UNCHANGED,
        ORPHANED,
        DELIVERED, // 배송완료
        OUT_FOR_DELIVERY, // 배송출발
        IN_TRANSIT, // 상품이동중
        AT_PICKUP, // 상품인수
        INFORMATION_RECEIVED // 상품준비중
    }

    enum class Activeness(val STATUS: Int) {
        ACTIVE(1),
        INACTIVE(0)
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parcel_id", nullable = false)
    var id: Long = 0L

    @Column(name = "user_id")
    var userId: String = ""

    @Column(name = "waybill_num")
    var waybillNum: String = ""

    @Column(name = "carrier")
    var carrier: String = ""

    @Column(name = "alias")
    var alias: String = ""

    @Column(name = "inquiry_result", columnDefinition = "TEXT")
    var inquiryResult: String = ""

    @Column(name = "inquiry_hash")
    var inquiryHash: String = ""

    @Column(name = "delivery_status")
    @Enumerated(EnumType.STRING)
    var deliveryStatus: DeliveryStatus? = null

    @Column(name = "arrival_dte")
    var arrivalDte: ZonedDateTime? = null

    @Column(name = "status", columnDefinition = "int(11) COMMENT '0 - 사용 X, 1 - 사용 가능' ")
    var status: Int? = 1

    constructor(
        trackingInfo: TrackingInfo?,
        _userId: String,
        _waybillNum: String,
        _carrier: String,
        _alias: String
    ) : this() {
        userId = _userId
        waybillNum = _waybillNum
        carrier = _carrier
        alias = createParcelAlias(trackingInfo, _alias, _waybillNum)
        inquiryResult = createInquiryResult(trackingInfo)
        inquiryHash = createInquiryHash(inquiryResult)
        deliveryStatus = createDeliveryStatus(trackingInfo)
        arrivalDte = createArrivalDateTime(trackingInfo)
    }

    fun changeToOrphaned() {
        this.deliveryStatus = DeliveryStatus.ORPHANED
    }

    fun changeParcelAlias(alias: String) {
        this.alias = alias
    }

    fun isActivate(): Boolean {
        return status != null && status == Activeness.ACTIVE.STATUS
    }

    fun inactivate() {
        if (status != 0) {
            status = Activeness.INACTIVE.STATUS
        }
    }

    fun updateParcel(refreshedParcel: Parcel): Parcel {
        inquiryResult = refreshedParcel.inquiryResult
        inquiryHash = refreshedParcel.inquiryHash
        deliveryStatus = refreshedParcel.deliveryStatus
        arrivalDte = refreshedParcel.arrivalDte
        auditDte = refreshedParcel.auditDte
        return this
    }

    private fun createInquiryHash(inquiryResult: String): String {
        return MessageDigest
            .getInstance("MD5")
            .let {
                it.update(inquiryResult.toByteArray())
                it.digest().asHex
            }
    }

    private fun createInquiryResult(trackingInfo: TrackingInfo?): String {
        return if (trackingInfo == null) {
            ""
        } else {
            Gson().toJson(trackingInfo)
        }
    }

    private fun createDeliveryStatus(trackingInfo: TrackingInfo?): DeliveryStatus {
        val statusStr = trackingInfo?.state?.id?.toUpperCase() ?: return DeliveryStatus.NOT_REGISTERED
        return DeliveryStatus.valueOf(statusStr)
    }

    private fun createArrivalDateTime(trackingInfo: TrackingInfo?): ZonedDateTime? {
        val status = createDeliveryStatus(trackingInfo)
        return if (trackingInfo != null && status == DeliveryStatus.DELIVERED) {
            ZonedDateTime.parse(trackingInfo.progresses.last()?.time.let {
                it?.plus("[Asia/Seoul]")
            })
        } else {
            null
        }
    }

    private fun createParcelAlias(trackingInfo: TrackingInfo?, inputAlias: String, waybillNum: String): String {
        val returnObj by lazy {

            inputAlias.ifEmpty {
                if (trackingInfo != null) {
                    trackingInfo.from?.let { "보내는 이 (${it.name})" } ?: waybillNum
                } else {
                    waybillNum
                }
            }
        }
        return returnObj
    }
}