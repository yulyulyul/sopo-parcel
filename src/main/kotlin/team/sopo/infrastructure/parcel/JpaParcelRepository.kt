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
        value = "SELECT * FROM parcel WHERE user_token = :user_token AND status = 'ACTIVE' AND delivery_status = 'DELIVERED' AND date(arrival_dte) BETWEEN :startDate AND :endDate",
        countQuery = "SELECT COUNT(*) FROM parcel WHERE user_token = :user_token AND status = 'ACTIVE' AND delivery_status = 'DELIVERED' AND date(arrival_dte) BETWEEN :startDate AND :endDate",
        nativeQuery = true
    )
    fun getCompleteParcels(
        @Param("pageable") pageable: Pageable,
        @Param("user_token") user_token: String,
        @Param("startDate") startDate: String,
        @Param("endDate") endDate: String
    ): Page<Parcel>

    fun findByIdAndUserTokenAndStatusEquals(
        id: Long,
        userToken: String,
        status: Parcel.Activeness = Parcel.Activeness.ACTIVE
    ): Optional<Parcel>

    fun findAllByIdInAndUserTokenAndStatusEquals(
        ids: List<Long>,
        userToken: String,
        status: Parcel.Activeness = Parcel.Activeness.ACTIVE
    ): List<Parcel>

    fun findByUserTokenAndCarrierAndWaybillNumAndStatusEquals(
        userToken: String,
        carrier: String,
        waybillNum: String,
        status: Parcel.Activeness = Parcel.Activeness.ACTIVE
    ): Optional<Parcel>

}