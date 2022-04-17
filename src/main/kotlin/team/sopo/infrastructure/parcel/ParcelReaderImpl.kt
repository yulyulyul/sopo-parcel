package team.sopo.infrastructure.parcel

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import team.sopo.common.exception.ParcelNotFoundException
import team.sopo.common.util.OffsetBasedPageRequest
import team.sopo.domain.parcel.Carrier
import team.sopo.domain.parcel.Parcel
import team.sopo.domain.parcel.ParcelInfo
import team.sopo.domain.parcel.ParcelReader
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.streams.toList

@Component
class ParcelReaderImpl(private val repository: JpaParcelRepository) : ParcelReader {

    override fun getParcel(parcelId: Long, userToken: String): Parcel {
        return repository.findByIdAndUserTokenAndStatusEquals(parcelId, userToken).orElseThrow { ParcelNotFoundException() }
    }

    override fun getParcel(userToken: String, carrier: Carrier, waybillNum: String): Parcel {
        return repository.findByUserTokenAndCarrierAndWaybillNumAndStatusEquals(userToken, carrier.CODE, waybillNum)
            .orElseThrow { ParcelNotFoundException() }
    }

    override fun getParcels(parcelIds: List<Long>, userToken: String): List<Parcel> {
        val parcels = repository.findAllByIdInAndUserTokenAndStatusEquals(parcelIds, userToken)
        val ids = parcels.parallelStream().map(Parcel::id).toList()
        parcelIds.filter { !ids.contains(it) }.toList().apply {
            if (this.isNotEmpty()) {
                throw ParcelNotFoundException("${this}에 해당하는 택배를 찾을 수 없습니다.")
            }
        }

        return parcels
    }

    override fun getOngoingParcels(userToken: String): List<Parcel> {
        return repository.getParcelsOngoing(userToken).orEmpty()
    }

    override fun getCompleteParcels(userToken: String, inquiryDate: String, pageable: Pageable, itemCnt: Int): List<Parcel> {
        val inquiryYearMonth = YearMonth.parse(
            inquiryDate,
            DateTimeFormatter.ofPattern("yyyyMM")
        )
        val completeParcels = repository.getCompleteParcels(
            pageable = OffsetBasedPageRequest(pageable.pageNumber * itemCnt, itemCnt),
            user_token = userToken,
            startDate = "${inquiryYearMonth.year}-${inquiryYearMonth.monthValue}-01",
            endDate = "${inquiryYearMonth.year}-${inquiryYearMonth.monthValue}-31"
        )

        return completeParcels.content
    }

    override fun getRegisteredCountIn2Week(userToken: String): Long {
        return repository.getRegisterParcelCountIn2Week(userToken)
    }

    override fun getRegisteredParcelCount(userToken: String): Long {
        return repository.getRegisterParcelCount(userToken)
    }

    override fun getMonthlyParcelCntList(userToken: String): List<ParcelInfo.MonthlyParcelCnt> {
        return repository.getMonthlyParcelCntList(userToken)
    }

    override fun getCurrentMonthRegisteredCount(userToken: String): Int {
        return repository.getCurrentMonthRegisteredCount(userToken)
    }
}