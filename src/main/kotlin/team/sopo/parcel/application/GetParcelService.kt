package team.sopo.parcel.application

import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import team.sopo.common.consts.CompletedParcelConst.pageableLimit
import team.sopo.common.consts.CompletedParcelConst.pageableOffSet
import team.sopo.common.consts.CompletedParcelConst.yearMonthDateTimeFormatPattern
import team.sopo.common.util.MonthListBuilder
import team.sopo.common.util.OffsetBasedPageRequest
import team.sopo.parcel.domain.ParcelRepository
import team.sopo.parcel.domain.command.GetParcelCommand
import team.sopo.parcel.domain.dto.ParcelDTO
import team.sopo.parcel.domain.vo.ParcelCntInfo
import team.sopo.parcel.infrastructure.converter.ParcelConverter
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Service
class GetParcelService(
    @Autowired private val parcelRepo: ParcelRepository
) {
    private val logger = LogManager.getLogger(this.javaClass)

    fun getParcel(command: GetParcelCommand): ParcelDTO {
        return ParcelConverter.entityToDto(parcel = parcelRepo.getParcel(command.userId, command.parcelId))
    }

    fun getMonths(userId: String): List<ParcelCntInfo> {
        val usedMonthList = parcelRepo.getIncompleteMonth(userId)
        return MonthListBuilder.makeMonthlyDataList(usedMonthList)
    }

    fun getOngoings(userId: String): List<ParcelDTO> {
        val ongoingParcelList = parcelRepo.getOngoingParcels(userId = userId) ?: listOf()

        return if (ongoingParcelList.isEmpty()) {
            listOf()
        } else {
            ParcelConverter.entityToDto(ongoingParcelList)
        }
    }

    fun getCompletes(userId: String, inquiryDate: String, pageable: Pageable): List<ParcelDTO>{
        val inquiryYearMonth = YearMonth.parse(inquiryDate, DateTimeFormatter.ofPattern(yearMonthDateTimeFormatPattern))

        val completeParcelEntityPage = parcelRepo.getCompleteParcels(
            pageable = OffsetBasedPageRequest(pageable.pageNumber * pageableOffSet, pageableLimit),
            userId = userId,
            startDate = "${inquiryYearMonth.year}-${inquiryYearMonth.monthValue}-01",
            endDate = "${inquiryYearMonth.year}-${inquiryYearMonth.monthValue}-31"
        )

        logger.debug("completeParcelEntityPage : $completeParcelEntityPage.")

        return if (completeParcelEntityPage.size == 0) {
            listOf()
        } else {
            ParcelConverter.entityToDto(completeParcelEntityPage.content)
        }
    }
}