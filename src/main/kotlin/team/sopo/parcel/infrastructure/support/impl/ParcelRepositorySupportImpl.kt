package team.sopo.parcel.infrastructure.support.impl

import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import team.sopo.common.exception.ParcelNotFoundException
import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.ParcelInfo
import team.sopo.parcel.domain.QParcel
import team.sopo.parcel.domain.QParcel.parcel
import team.sopo.parcel.infrastructure.support.ParcelRepositorySupport
import java.time.ZonedDateTime

class ParcelRepositorySupportImpl(private val queryFactory: JPAQueryFactory) : ParcelRepositorySupport {

    override fun getRegisterParcelCount(userId: Long): Long {
        val parcel = QParcel.parcel
        return queryFactory
            .selectFrom(parcel)
            .where(parcel.userId.eq(userId))
            .fetch().size.toLong()
    }

    override fun getRegisterParcelCountIn2Week(userId: Long): Long {
        val parcel = QParcel.parcel

        return queryFactory
            .selectFrom(parcel)
            .where(
                parcel.regDte.between(ZonedDateTime.now().minusWeeks(2L), ZonedDateTime.now())
                    .and(parcel.userId.eq(userId))
            )
            .fetch().size.toLong()
    }

    override fun getParcel(userId: Long, parcelId: Long): Parcel {

        return queryFactory
            .selectFrom(parcel)
            .where(
                parcel.id.eq(parcelId)
                    .and(parcel.userId.eq(userId))
            ).fetchOne() ?: throw ParcelNotFoundException()
    }

    override fun getParcelsOngoing(userId: Long): List<Parcel>? {
        return queryFactory
            .selectFrom(parcel)
            .where(
                parcel.userId.eq(userId)
                    .and(parcel.status.eq(Parcel.Activeness.ACTIVE))
                    .and(parcel.deliveryStatus.ne(Parcel.DeliveryStatus.DELIVERED))
            )
            .orderBy(parcel.auditDte.desc())
            .fetch()
    }

    override fun isAlreadyRegistered(userId: Long, waybillNum: String, carrier: String): Boolean {
        val flag = queryFactory
            .from(parcel)
            .where(
                parcel.userId.eq(userId)
                    .and(parcel.status.eq(Parcel.Activeness.ACTIVE))
                    .and(parcel.waybillNum.eq(waybillNum))
                    .and(parcel.carrier.eq(carrier))
            )
            .fetch().size

        return flag > 0
    }

    override fun getMonthlyParcelCntList(userId: Long): MutableList<ParcelInfo.MonthlyParcelCnt> {

        val dateFormatTemplate = Expressions.stringTemplate("DATE_FORMAT({0}, {1})", parcel.arrivalDte, "%Y-%m")
        val dateTimePath = Expressions.dateTimePath(ZonedDateTime::class.java, "time")

        val timeCountList = queryFactory
            .select(
                Projections.constructor(
                    ParcelInfo.MonthlyParcelCnt::class.java,
                    dateFormatTemplate.`as`(dateTimePath.toString()),
                    parcel.arrivalDte.count()
                )
            )
            .from(parcel)
            .leftJoin(parcel).on(parcel.userId.eq(parcel.userId))
            .where(
                parcel.userId.eq(userId)
                    .and(
                        parcel.status.eq(Parcel.Activeness.ACTIVE)
                            .and(parcel.deliveryStatus.eq(Parcel.DeliveryStatus.DELIVERED))
                    )
            )
            .groupBy(dateTimePath)
            .limit(12)
            .fetch()
        timeCountList.sortByDescending { it.time }

        return timeCountList
    }

    override fun isLimitCountOver(userId: Long): Boolean {
        return queryFactory
            .from(parcel)
            .where(
                parcel.userId.eq(userId)
                    .and(parcel.regDte.month().eq(Expressions.currentDate().month()))
            )
            .fetch().size > 50
    }

    override fun getCurrentMonthRegisteredCount(userId: Long): Int {
        return queryFactory
            .from(parcel)
            .where(
                parcel.userId.eq(userId)
                    .and(parcel.regDte.month().eq(Expressions.currentDate().month()))
            )
            .fetch().size
    }

}