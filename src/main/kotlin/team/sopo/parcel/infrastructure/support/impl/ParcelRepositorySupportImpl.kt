package team.sopo.parcel.infrastructure.support.impl

import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import team.sopo.common.exception.ParcelNotFoundException
import team.sopo.parcel.domain.DeliveryStatus
import team.sopo.parcel.domain.Parcel
import team.sopo.parcel.domain.QParcel
import team.sopo.parcel.domain.QParcel.parcel
import team.sopo.parcel.domain.vo.ParcelCntInfo
import team.sopo.parcel.infrastructure.support.ParcelRepositorySupport
import java.time.LocalDate
import java.time.LocalDateTime

class ParcelRepositorySupportImpl(@Autowired private val queryFactory: JPAQueryFactory) : ParcelRepositorySupport {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun getRegisterParcelCount(userId: String): Long{
        val parcel = QParcel.parcel
        return queryFactory
            .selectFrom(parcel)
            .where(parcel.userId.eq(userId))
            .fetchCount()
    }

    override fun getRegisterParcelCountIn2Week(userId: String): Long {
        val parcel = QParcel.parcel

        return queryFactory
            .selectFrom(parcel)
            .where(
                parcel.regDt.between(LocalDate.now().minusWeeks(2L), LocalDate.now())
                    .and(parcel.userId.eq(userId))
            )
            .fetchCount()
    }

    override fun getParcel(userId: String, parcelId: Long): Parcel {

        return  queryFactory
                .selectFrom(parcel)
                .where(parcel.id.eq(parcelId)
                    .and(parcel.userId.eq(userId))).fetchOne() ?: throw ParcelNotFoundException()
    }

    override fun getParcelsOngoing(userId: String): List<Parcel>? {
       return  queryFactory
                .selectFrom(parcel)
                .where(
                    parcel.userId.eq(userId)
                        .and(parcel.status.eq(1))
                        .and(parcel.deliveryStatus.ne(DeliveryStatus.DELIVERED))
                )
                .orderBy(parcel.auditDte.desc())
                .limit(20)
               .fetch()
    }

    override fun isAlreadyRegistered(userId: String, waybillNum: String, carrier: String): Boolean {
        val flag = queryFactory
                .from(parcel)
                .where(
                    parcel.userId.eq(userId)
                                .and(parcel.status.eq(1))
                                .and(parcel.waybillNum.eq(waybillNum))
                                .and(parcel.carrier.eq(carrier))
                )
                .fetchCount().toInt()

        return flag > 0
    }

    override fun getIncompleteMonthList(userId: String): MutableList<ParcelCntInfo>{

        val dateFormatTemplate = Expressions.stringTemplate("DATE_FORMAT({0}, {1})", parcel.arrivalDte, "%Y-%m")
        val dateTimePath = Expressions.dateTimePath(LocalDateTime::class.java, "time")

        val timeCountList = queryFactory
                .select(
                        Projections.constructor(
                            ParcelCntInfo::class.java,
                            dateFormatTemplate.`as`(dateTimePath.toString()),
                            parcel.arrivalDte.count()
                        )
                )
                .from(parcel)
                .leftJoin(parcel).on(parcel.userId.eq(parcel.userId))
                .where(parcel.userId.eq(userId)
                        .and(parcel.status.eq(1)
                        .and(parcel.deliveryStatus.eq(DeliveryStatus.DELIVERED))))
                .groupBy(dateTimePath)
                .limit(12)
                .fetch()
        timeCountList.sortByDescending { it.time }

        return timeCountList
    }

    override fun isLimitCountOver(userId: String): Boolean {
        return queryFactory
                .from(parcel)
                .where(parcel.userId.eq(userId)
                        .and(parcel.regDt.month().eq(Expressions.currentDate().month())))
                .fetchCount().toInt() > 50
    }
}