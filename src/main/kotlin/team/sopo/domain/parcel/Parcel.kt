package team.sopo.domain.parcel

import com.google.gson.Gson
import org.apache.commons.lang3.StringUtils
import team.sopo.common.exception.UnauthorizedException
import team.sopo.common.exception.ValidationException
import team.sopo.common.extension.asHex
import team.sopo.domain.parcel.trackinginfo.TrackingInfo
import java.security.MessageDigest
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException
import javax.persistence.*

@Entity
@Table(name = "parcel")
class Parcel(
    @Column(name = "user_token", nullable = false)
    var userToken: String,
    @Column(name = "waybill_num")
    var waybillNum: String,
    @Column(name = "carrier")
    var carrier: String,
    @Column(name = "alias")
    var alias: String
) : AbstractEntity() {

    constructor(
//        userId: Long,
        userToken: String,
        waybillNum: String,
        carrier: String,
        alias: String,
        trackingInfo: TrackingInfo?
    ) : this(userToken, waybillNum, carrier, alias) {
        this.alias = createParcelAlias(trackingInfo, alias, waybillNum)
        this.inquiryResult = createInquiryResult(trackingInfo)
        this.inquiryHash = createInquiryHash(inquiryResult)
        this.deliveryStatus = createDeliveryStatus(trackingInfo)
        this.arrivalDte = createArrivalDateTime(trackingInfo)
    }

    enum class DeliveryStatus(val id: String, val description: String) {
        NOT_REGISTERED("not_registered", "등록되지 않았음"),
        CHANGED("changed", "상태가 변경되었음"),
        UNCHANGED("unchanged", "상태가 변경되지 않았음"),
        ORPHANED("orphaned", "택배가 오랫동안 변경이 없음"),
        DELIVERED("delivered", "배송완료"), // 배송완료
        OUT_FOR_DELIVERY("out_for_delivery", "배송출발"), // 배송출발
        IN_TRANSIT("in_transit", "상품이동중"), // 상품이동중
        AT_PICKUP("at_pickup", "상품인수"), // 상품인수
        INFORMATION_RECEIVED("information_received", "상품준비중") // 상품준비중
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

    @Column(name = "is_reported")
    var reported: Boolean = false

    fun reporting(){
        if(!reported)
            reported = true
    }

    fun changeToOrphaned() {
        this.deliveryStatus = DeliveryStatus.ORPHANED
    }

    fun changeParcelAlias(alias: String) {
        if (alias.length > 25) {
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

    fun verifyDeletable(command: ParcelCommand.DeleteParcel) {
        if (!StringUtils.equals(userToken, command.userToken)) {
            throw UnauthorizedException("삭제하시려는 택배는 ${command.userToken}님의 택배가 아닙니다.")
        }
    }

    fun verifyRefreshable() {
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
        if(deliveryStatus.toString() != refreshedParcel.deliveryStatus.toString()){
            reported = false
        }
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
        val time = trackingInfo?.progresses?.lastOrNull()?.time ?: return null

        return try{
            if (status == DeliveryStatus.DELIVERED) {
                ZonedDateTime.parse(time.plus("[Asia/Seoul]"))
            } else {
                null
            }
        }
        catch (e: DateTimeParseException){
            ZonedDateTime.parse(time)
        }
    }

    private fun createParcelAlias(trackingInfo: TrackingInfo?, inputAlias: String, waybillNum: String): String {
        val returnObj by lazy {

            inputAlias.ifEmpty {
                if (trackingInfo == null) {
                    return@ifEmpty waybillNum
                }

                if (trackingInfo.item != null) {
                    return@ifEmpty trackingInfo.item
                }

                if (trackingInfo.from != null && StringUtils.isNotBlank(trackingInfo.from.name)) {
                    return@ifEmpty "보낸이 : ${trackingInfo.from.name}"
                }

                waybillNum
            }
        }
        return returnObj
    }
}