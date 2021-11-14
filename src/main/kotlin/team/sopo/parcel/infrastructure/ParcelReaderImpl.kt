package team.sopo.parcel.infrastructure

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import team.sopo.common.consts.CompletedParcelConst
import team.sopo.common.exception.ParcelNotFoundException
import team.sopo.common.util.OffsetBasedPageRequest
import team.sopo.parcel.ParcelInfo
import team.sopo.parcel.domain.Carrier
import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.ParcelReader
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Component
class ParcelReaderImpl(private val repository: JpaParcelRepository): ParcelReader {

    override fun getParcel(parcelId: Long, userId: String): Parcel {
        return repository.findByIdAndUserId(parcelId, userId).orElseThrow{ ParcelNotFoundException() }
    }

    override fun getParcel(userId: String, carrier: Carrier, waybillNum: String): Parcel {
        return repository.findByUserIdAndCarrierAndWaybillNum(userId, carrier.CODE, waybillNum).orElseThrow{ ParcelNotFoundException() }

    }

    override fun getOngoingParcels(userId: String): List<Parcel> {
        return repository.getParcelsOngoing(userId).orEmpty()
    }

    override fun getCompleteParcels(userId: String, inquiryDate: String, pageable: Pageable): List<Parcel> {
        val inquiryYearMonth = YearMonth.parse(inquiryDate, DateTimeFormatter.ofPattern(CompletedParcelConst.yearMonthDateTimeFormatPattern))
        val completeParcels = repository.getCompleteParcels(
            pageable = OffsetBasedPageRequest(pageable.pageNumber * CompletedParcelConst.pageableOffSet, CompletedParcelConst.pageableLimit),
            user_id = userId,
            startDate = "${inquiryYearMonth.year}-${inquiryYearMonth.monthValue}-01",
            endDate = "${inquiryYearMonth.year}-${inquiryYearMonth.monthValue}-31")

        return completeParcels.content
    }

    override fun getRegisteredCountIn2Week(userId: String): Long {
        return repository.getRegisterParcelCount(userId)
    }

    override fun getRegisteredParcelCount(userId: String): Long {
        return repository.getRegisterParcelCountIn2Week(userId)
    }

    override fun getMonthlyParcelCntList(userId: String): List<ParcelInfo.MonthlyParcelCnt> {
        return repository.getMonthlyParcelCntList(userId)
    }

    override fun getCurrentMonthRegisteredCount(userId: String): Int {
        return repository.getCurrentMonthRegisteredCount(userId)
    }
}