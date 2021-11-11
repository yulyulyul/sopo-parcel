package team.sopo.parcel.infrastructure.converter

import team.sopo.parcel.domain.Carrier
import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.dto.ParcelDTO
import kotlin.streams.toList

object ParcelConverter {
    fun entityToDto(parcel: Parcel): ParcelDTO {
        return ParcelDTO(parcelId = parcel.id,
                         userId = parcel.userId,
                         waybillNum = parcel.waybillNum,
                         carrier = Carrier.getCarrierByCode(parcel.carrier),
                         alias = parcel.alias,
                         inquiryResult = parcel.inquiryResult,
                         inquiryHash = parcel.inquiryHash,
                         deliveryStatus = parcel.deliveryStatus,
                         regDte = parcel.regDte,
                         arrivalDte = parcel.arrivalDte,
                         auditDte = parcel.auditDte,
                         status = parcel.status)
    }

    fun entityToDto(list: List<Parcel>): List<ParcelDTO>{
        return list.stream().map { entity -> entityToDto(entity) }.toList()
    }
}