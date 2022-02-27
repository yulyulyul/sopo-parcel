package team.sopo.infrastructure.parcel

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import team.sopo.common.consts.CompletedParcelConst
import team.sopo.common.exception.ParcelNotFoundException
import team.sopo.common.util.OffsetBasedPageRequest
import team.sopo.domain.parcel.ParcelInfo
import team.sopo.domain.parcel.Carrier
import team.sopo.domain.parcel.Parcel
import team.sopo.domain.parcel.ParcelReader
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.streams.toList

@Component
class ParcelReaderImpl(private val repository: JpaParcelRepository) : ParcelReader {

    override fun getParcel(parcelId: Long, userId: Long): Parcel {
        return repository.findByIdAndUserIdAndStatusEquals(parcelId, userId).orElseThrow { ParcelNotFoundException() }
    }

    override fun getParcel(userId: Long, carrier: Carrier, waybillNum: String): Parcel {
        return repository.findByUserIdAndCarrierAndWaybillNumAndStatusEquals(userId, carrier.CODE, waybillNum)
            .orElseThrow { ParcelNotFoundException() }
    }

    override fun getParcels(parcelIds: List<Long>, userId: Long): List<Parcel> {
        val parcels = repository.findAllByIdInAndUserIdAndStatusEquals(parcelIds, userId)
        val ids = parcels.parallelStream().map(Parcel::id).toList()
        parcelIds.filter { !ids.contains(it) }.toList().apply {
            if(this.isNotEmpty()){
                throw ParcelNotFoundException("${this}에 해당하는 택배를 찾을 수 없습니다.")
            }
        }

        return parcels
    }

    override fun getOngoingParcels(userId: Long): List<Parcel> {
        return repository.getParcelsOngoing(userId).orEmpty()
    }

    override fun getCompleteParcels(userId: Long, inquiryDate: String, pageable: Pageable): List<Parcel> {
        val inquiryYearMonth = YearMonth.parse(
            inquiryDate,
            DateTimeFormatter.ofPattern(CompletedParcelConst.yearMonthDateTimeFormatPattern)
        )
        val completeParcels = repository.getCompleteParcels(
            pageable = OffsetBasedPageRequest(
                pageable.pageNumber * CompletedParcelConst.pageableOffSet,
                CompletedParcelConst.pageableLimit
            ),
            user_id = userId,
            startDate = "${inquiryYearMonth.year}-${inquiryYearMonth.monthValue}-01",
            endDate = "${inquiryYearMonth.year}-${inquiryYearMonth.monthValue}-31"
        )

        return completeParcels.content
    }

    override fun getRegisteredCountIn2Week(userId: Long): Long {
        return repository.getRegisterParcelCountIn2Week(userId)
    }

    override fun getRegisteredParcelCount(userId: Long): Long {
        return repository.getRegisterParcelCount(userId)
    }

    override fun getMonthlyParcelCntList(userId: Long): List<ParcelInfo.MonthlyParcelCnt> {
        return repository.getMonthlyParcelCntList(userId)
    }

    override fun getCurrentMonthRegisteredCount(userId: Long): Int {
        return repository.getCurrentMonthRegisteredCount(userId)
    }
}