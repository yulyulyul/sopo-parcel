package team.sopo.parcel.domain

import com.google.gson.Gson
import team.sopo.common.exception.UnauthorizedException
import team.sopo.common.exception.ValidationException
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

    enum class Activeness {
        ACTIVE, INACTIVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parcel_id", nullable = false)
    var id: Long = 0L

    @Column(name = "user_id", nullable = false)
    var userId: Long = 0L

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
    @Enumerated(EnumType.STRING)
    var status: Activeness = Activeness.ACTIVE

    constructor(
        trackingInfo: TrackingInfo?,
        _userId: Long,
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
        if(alias.length > 25){
            throw ValidationException("택배 별칭은 25글자를 초과할 수 없습니다.")
        }
        this.alias = alias
    }

    fun isActivate(): Boolean {
        return status == Activeness.ACTIVE
    }

    fun isEntireRefreshable(): Boolean {
        return this.deliveryStatus != DeliveryStatus.ORPHANED
    }

    fun verifyDeletable(command: ParcelCommand.DeleteParcel){
        if(userId != command.userId){
            throw UnauthorizedException("삭제하시려는 택배는 ${command.userId}님의 택배가 아닙니다.")
        }
    }

    fun verifyRefreshable(){
      /*
        TODO 택배서버에서 일어나는 이슈를 좀 더 알아보고 결정하기 위해서 아래 사항은 후에 적용하기로 의사결정함. (의사결정 일자 : 2021-11-21)
        if(this.deliveryStatus == DeliveryStatus.DELIVERED){
            throw ParcelRefreshValidateException("이미 완료된 택배는 업데이트할 수 없습니다.")
        }
      */
    }

    fun inactivate() {
        if (status != Activeness.INACTIVE) {
            status = Activeness.INACTIVE
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
        val statusStr = trackingInfo?.state?.id?.uppercase() ?: return DeliveryStatus.NOT_REGISTERED
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