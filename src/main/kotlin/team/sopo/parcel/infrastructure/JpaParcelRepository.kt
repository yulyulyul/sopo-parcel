package team.sopo.parcel.infrastructure

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.infrastructure.support.ParcelRepositorySupport
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

    fun findByIdAndUserId(id: Long, userId: Long): Optional<Parcel>

    fun findAllByIdIn(ids: List<Long>): List<Parcel>

    fun findByUserIdAndCarrierAndWaybillNum(userId: Long, carrier: String, waybillNum: String): Optional<Parcel>

}