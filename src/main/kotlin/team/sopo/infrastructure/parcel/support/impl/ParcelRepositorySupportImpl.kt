package team.sopo.infrastructure.parcel.support.impl

import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import team.sopo.common.exception.ParcelNotFoundException
import team.sopo.domain.parcel.Parcel
import team.sopo.domain.parcel.ParcelInfo
import team.sopo.domain.parcel.QParcel
import team.sopo.domain.parcel.QParcel.parcel
import team.sopo.infrastructure.parcel.support.ParcelRepositorySupport
import java.time.ZonedDateTime

class ParcelRepositorySupportImpl(private val queryFactory: JPAQueryFactory) : ParcelRepositorySupport {

    override fun getRegisterParcelCount(userToken: String): Long {
        val parcel = QParcel.parcel
        return queryFactory
            .selectFrom(parcel)
            .where(parcel.userToken.eq(userToken))
            .fetch().size.toLong()
    }

    override fun getRegisterParcelCountIn2Week(userToken: String): Long {
        val parcel = QParcel.parcel

        return queryFactory
            .selectFrom(parcel)
            .where(
                parcel.regDte.between(ZonedDateTime.now().minusWeeks(2L), ZonedDateTime.now())
                    .and(parcel.userToken.eq(userToken))
            )
            .fetch().size.toLong()
    }

    override fun getParcel(userToken: String, parcelId: Long): Parcel {

        return queryFactory
            .selectFrom(parcel)
            .where(
                parcel.id.eq(parcelId)
                    .and(parcel.userToken.eq(userToken))
            ).fetchOne() ?: throw ParcelNotFoundException()
    }

    override fun getParcelsOngoing(userToken: String): List<Parcel>? {
        return queryFactory
            .selectFrom(parcel)
            .where(
                parcel.userToken.eq(userToken)
                    .and(parcel.status.eq(Parcel.Activeness.ACTIVE))
                    .and(parcel.deliveryStatus.ne(Parcel.DeliveryStatus.DELIVERED))
            )
            .orderBy(parcel.auditDte.desc())
            .fetch()
    }

    override fun isAlreadyRegistered(userToken: String, waybillNum: String, carrier: String): Boolean {
        val flag = queryFactory
            .from(parcel)
            .where(
                parcel.userToken.eq(userToken)
                    .and(parcel.status.eq(Parcel.Activeness.ACTIVE))
                    .and(parcel.waybillNum.eq(waybillNum))
                    .and(parcel.carrier.eq(carrier))
            )
            .fetch().size

        return flag > 0
    }

    override fun getMonthlyParcelCntList(userToken: String): MutableList<ParcelInfo.MonthlyParcelCnt> {

        val dateFormatTemplate = Expressions.stringTemplate("DATE_FORMAT({0}, {1})", parcel.arrivalDte, "%Y%m")
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
                parcel.userToken.eq(userToken)
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

    override fun isLimitCountOver(userToken: String): Boolean {
        return queryFactory
            .from(parcel)
            .where(
                parcel.userToken.eq(userToken)
                    .and(parcel.regDte.month().eq(Expressions.currentDate().month()))
            )
            .fetch().size > 50
    }

    override fun getCurrentMonthRegisteredCount(userToken: String): Int {
        return queryFactory
            .from(parcel)
            .where(
                parcel.userToken.eq(userToken)
                    .and(parcel.regDte.month().eq(Expressions.currentDate().month()))
            )
            .fetch().size
    }

}