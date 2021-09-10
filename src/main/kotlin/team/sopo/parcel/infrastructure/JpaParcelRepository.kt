package team.sopo.parcel.infrastructure

import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.infrastructure.support.ParcelRepositorySupport
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

interface JpaParcelRepository: JpaRepository<Parcel, Long>, ParcelRepositorySupport {

    @Query(value = "SELECT * FROM parcel WHERE user_id = :user_id AND status = 1 AND delivery_status = 'DELIVERED' AND arrival_dte BETWEEN :startDate AND :endDate", nativeQuery = true)
    fun getCompleteParcels(@Param("pageable") pageable: Pageable,
                           @Param("user_id") user_id: String,
                           @Param("startDate") startDate: String,
                           @Param("endDate") endDate: String): Page<Parcel>


    fun findAllByUserIdAndRegDtBetween(userId: String, regDt: LocalDate, regDt2: LocalDate): List<Parcel>?

    @Transactional
    fun deleteAllByUserId(userId: String)

}