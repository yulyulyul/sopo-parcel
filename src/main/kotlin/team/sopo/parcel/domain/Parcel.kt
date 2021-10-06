package team.sopo.parcel.domain

import com.google.gson.Gson
import team.sopo.common.extension.asHex
import team.sopo.common.util.TimeUtil
import team.sopo.parcel.domain.update.ChangeStatusToUnidentifiedDeliveredParcel
import team.sopo.parcel.domain.update.NoChange
import team.sopo.parcel.domain.update.UpdatePolicy
import team.sopo.parcel.domain.update.UsualUpdate
import team.sopo.parcel.domain.vo.deliverytracker.TrackingInfo
import java.security.MessageDigest
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "parcel")
class Parcel() {

    constructor(trackingInfo: TrackingInfo?, _userId: String, _waybillNum: String, _carrier: String, _alias: String): this(){
        this.userId = _userId
        this.waybillNum = _waybillNum
        this.carrier = _carrier
        this.alias = createParcelAlias(trackingInfo, _alias, _waybillNum)
        this.inquiryResult = createInquiryResult(trackingInfo)
        this.inquiryHash = createInquiryHash(this.inquiryResult)
        this.deliveryStatus = createDeliveryStatus(trackingInfo)
        this.arrivalDte = createArrivalDateTime(trackingInfo)
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parcel_id", nullable = false)
    var id: Long = 0L

    @Column(name = "user_id")
    var userId: String = ""

    @Column(name = "reg_dt")
    var regDt:LocalDate = TimeUtil.getLocalDate()

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
    var arrivalDte: LocalDateTime? = null

    @Column(name = "audit_dte")
    var auditDte: LocalDateTime = TimeUtil.getLocalDateTime()

    @Column(name = "status", columnDefinition = "int(11) COMMENT '0 - 사용 X, 1 - 사용 가능' ")
    var status: Int? = 1

    fun changeDeliveryStatus(status: DeliveryStatus){
        this.deliveryStatus = status
    }

    fun changeParcelAlias(alias: String){
        this.alias = alias
    }

    fun inactivate(){
        if(status != 0){
            status = ParcelActiveness.INACTIVE.STATUS
        }
    }

    fun isNeedToUpdate(newParcel: Parcel): Boolean{
        return this.inquiryHash != newParcel.inquiryHash
    }

    fun getUpdatePolicy(parcelRepository: ParcelRepository, refreshedParcel: Parcel): UpdatePolicy {
        return if(this.inquiryHash != refreshedParcel.inquiryHash){
            UsualUpdate(parcelRepository, this, refreshedParcel)
        }
        else if(auditDte.plusWeeks(2L).isBefore(LocalDateTime.now())){
            ChangeStatusToUnidentifiedDeliveredParcel(parcelRepository, this)
        }
        else{
            NoChange()
        }
    }

    fun updateParcel(refreshedParcel: Parcel): Parcel{
        inquiryResult = refreshedParcel.inquiryResult
        inquiryHash = refreshedParcel.inquiryHash
        deliveryStatus = refreshedParcel.deliveryStatus
        arrivalDte = refreshedParcel.arrivalDte
        auditDte = refreshedParcel.auditDte
        return this
    }

    fun updateParcel(trackingInfo: TrackingInfo): Parcel {
        inquiryResult = createInquiryResult(trackingInfo)
        inquiryHash = createInquiryHash(inquiryResult)
        deliveryStatus = createDeliveryStatus(trackingInfo)
        arrivalDte = createArrivalDateTime(trackingInfo)
        auditDte = TimeUtil.getLocalDateTime()
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

    private fun createInquiryResult(trackingInfo: TrackingInfo?): String{
        return if(trackingInfo == null){ "" }
        else{
            Gson().toJson(trackingInfo)
        }
    }

    private fun createDeliveryStatus(trackingInfo: TrackingInfo?): DeliveryStatus {
        val statusStr = trackingInfo?.state?.id?.toUpperCase() ?: return DeliveryStatus.NOT_REGISTERED
        return DeliveryStatus.valueOf(statusStr)
    }

    private fun createArrivalDateTime(trackingInfo: TrackingInfo?): LocalDateTime?{
        val status = createDeliveryStatus(trackingInfo)
        return if(trackingInfo != null && status == DeliveryStatus.DELIVERED){
            LocalDateTime.parse(trackingInfo.progresses.last()?.time.let {
                it?.substring(0, it.indexOf("+"))
            })
        }
        else{
            null
        }
    }

    private fun createParcelAlias(trackingInfo: TrackingInfo?, inputAlias: String, waybillNum: String): String{
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