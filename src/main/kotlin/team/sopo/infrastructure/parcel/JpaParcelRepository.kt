package team.sopo.infrastructure.parcel

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import team.sopo.domain.parcel.Parcel
import team.sopo.infrastructure.parcel.support.ParcelRepositorySupport
import java.util.*

interface JpaParcelRepository : JpaRepository<Parcel, Long>, ParcelRepositorySupport {

    @Query(
        value = "SELECT * FROM parcel WHERE user_id = :user_id AND status = 'ACTIVE' AND delivery_status = 'DELIVERED' AND date(arrival_dte) BETWEEN :startDate AND :endDate",
        nativeQuery = true
    )
    fun getCompleteParcels(
        @Param("pageable") pageable: Pageable,
        @Param("user_id") user_id: Long,
        @Param("startDate") startDate: String,
        @Param("endDate") endDate: String
    ): Page<Parcel>

    fun findByIdAndUserIdAndStatusEquals(
        id: Long,
        userId: Long,
        status: Parcel.Activeness = Parcel.Activeness.ACTIVE
    ): Optional<Parcel>

    fun findAllByIdInAndUserIdAndStatusEquals(
        ids: List<Long>,
        userId: Long,
        status: Parcel.Activeness = Parcel.Activeness.ACTIVE
    ): List<Parcel>

    fun findByUserIdAndCarrierAndWaybillNumAndStatusEquals(
        userId: Long,
        carrier: String,
        waybillNum: String,
        status: Parcel.Activeness = Parcel.Activeness.ACTIVE
    ): Optional<Parcel>

}