package team.sopo.parcel.infrastructure.datasource.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.vo.ParcelCntInfo
import team.sopo.parcel.infrastructure.JpaParcelRepository
import team.sopo.parcel.infrastructure.datasource.ParcelPersistenceDataSource
import java.time.LocalDate
import java.time.ZonedDateTime

@Component
class JpaPersistenceDataSource(
    @Autowired private val datasource: JpaParcelRepository
): ParcelPersistenceDataSource {
    override fun getRegisterParcelCount(userId: String): Long {
        return datasource.getRegisterParcelCount(userId)
    }

    override fun getRegisterParcelCountIn2Week(userId: String): Long {
        return datasource.getRegisterParcelCountIn2Week(userId)
    }

    override fun getParcelHistoryWithInOneWeek(userId: String): List<Parcel>? {
        val now = ZonedDateTime.now()
        val oneWeek = ZonedDateTime.now().minusWeeks(1L)

        return datasource.findAllByUserIdAndRegDteBetween(userId, oneWeek, now)
    }

    override fun getCompleteParcels(
        pageable: Pageable,
        userId: String,
        startDate: String,
        endDate: String
    ): Page<Parcel> {
        return datasource.getCompleteParcels(pageable, userId, startDate, endDate)
    }

    override fun getParcel(userId: String, parcelId: Long): Parcel {
        return datasource.getParcel(userId, parcelId)
    }

    override fun getIncompleteMonth(userId: String): List<ParcelCntInfo> {
        return datasource.getIncompleteMonthList(userId)
    }

    override fun getOngoingParcels(userId: String): List<Parcel>? {
        return datasource.getParcelsOngoing(userId)
    }

    override fun saveParcel(parcel: Parcel): Parcel {
        return datasource.save(parcel)
    }

    override fun deleteParcel(parcel: Parcel) {
        datasource.delete(parcel)
    }

    override fun isAlreadyRegistered(userId: String, waybillNum: String, carrier: String): Boolean {
        return datasource.isAlreadyRegistered(userId, waybillNum, carrier)
    }

    override fun isLimitCountOver(userId: String): Boolean {
        return datasource.isLimitCountOver(userId)
    }

}