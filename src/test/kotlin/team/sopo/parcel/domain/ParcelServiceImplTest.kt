package team.sopo.parcel.domain

import com.github.springtestdbunit.DbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseOperation
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseTearDown
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import io.mockk.every
import io.mockk.mockk
import org.apache.logging.log4j.LogManager
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import team.sopo.common.exception.*
import team.sopo.parcel.ParcelInfo
import team.sopo.parcel.TestConfig
import team.sopo.parcel.domain.register.RegisterProcessor
import team.sopo.parcel.domain.search.SearchProcessor
import team.sopo.parcel.domain.update.UpdateProcessor
import team.sopo.parcel.domain.vo.deliverytracker.From
import team.sopo.parcel.domain.vo.deliverytracker.State
import team.sopo.parcel.domain.vo.deliverytracker.To
import team.sopo.parcel.domain.vo.deliverytracker.TrackingInfo
import team.sopo.parcel.infrastructure.JpaParcelRepository
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.transaction.Transactional
import kotlin.streams.toList

@Transactional
@Import(TestConfig::class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["spring.config.location=classpath:/application.yml"]
)
@TestExecutionListeners(DbUnitTestExecutionListener::class, DependencyInjectionTestExecutionListener::class)
@DbUnitConfiguration(databaseConnection = ["dbUnitDatabaseConnection"])
@ActiveProfiles("test")
class ParcelServiceImplTest() {
    @Autowired
    lateinit var mapper: ParcelInfoMapper

    /*
     *   [TEST DB 구축 방법]
     *   1. resources-test/ 아래에서 docker-compose -p parcel-db up -d
     *   2. 프로젝트 폴더로 이동 후, ./gradlew flywayClean flywayMigrate
     */

    @Autowired
    lateinit var parcelService: ParcelService

    @Autowired
    lateinit var parcelReader: ParcelReader

    @Autowired
    lateinit var parcelStore: ParcelStore

    @Autowired
    lateinit var searchProcessor: SearchProcessor

    @Autowired
    lateinit var registerProcessor: RegisterProcessor

    @Autowired
    lateinit var updateProcessor: UpdateProcessor

    @Autowired
    lateinit var parcelRepository: JpaParcelRepository

    private val logger = LogManager.getLogger(ParcelServiceImplTest::class.java)

    @Nested
    @DisplayName("단일 택배 조회 테스트")
    @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.CLEAN_INSERT)
    @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
    inner class RetrieveParcelTest() {

        @Test
        @DisplayName("정상 케이스")
        fun retrieveParcelTestCase1() {
            // given
            val userId = "sopo@sooopo.com"
            val parcelId = 1L
            val command = ParcelCommand.GetParcel(userId, parcelId)

            // when
            val retrieveParcel = parcelService.retrieveParcel(command)

            // then
            Assertions.assertTrue(retrieveParcel.userId == userId)
            Assertions.assertEquals(retrieveParcel.parcelId?.equals(parcelId), true)
        }

        @Test
        @DisplayName("유저에게 존재하지 않은 택배를 조회했을 때는 ParcelNotFoundException이 발생되어야한다.")
        fun retrieveParcelTestCase2() {
            logger.error("start")
            // given
            val userId = "sopo@sooopo.com"
            val parcelId = 9999L
            val command = ParcelCommand.GetParcel(userId, parcelId)

            // when run retrieveParcel(command)
            // then throw ParcelNotFoundException
            Assertions.assertThrows(ParcelNotFoundException::class.java) { parcelService.retrieveParcel(command) }
        }
    }

    @Nested
    @DisplayName("현재 진행중인 택배 조회 테스트")
    inner class RetrieveOngoingParcelsTest() {

        @Test
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.CLEAN_INSERT)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DisplayName("정상 케이스 - 현재 진행중인 택배는 parcelInfo 객체로 리턴되어야하고 택배의 deliveryStatus가 (DELIVERED & ORPHANED)가 아니어야하며 status는 1이어야한다.")
        fun retrieveOngoingParcelsTestCase1() {
            // given
            val userId = "sopo@sooopo.com"
            val command = ParcelCommand.GetOngoingParcels(userId)

            // when
            val ongoingParcels = parcelService.retrieveOngoingParcels(command)

            // then
            ongoingParcels.parallelStream().forEach { parcel ->
                Assertions.assertTrue(parcel is ParcelInfo.Main)
                Assertions.assertTrue(parcel.userId == userId)
                Assertions.assertNotEquals(Parcel.DeliveryStatus.DELIVERED, parcel.deliveryStatus)
                Assertions.assertEquals(parcel.status?.equals(1), true)
            }
        }

        @Test
        @DisplayName("진행 중인 택배가 없다면 빈 리스트를 리턴해야한다.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun retrieveOngoingParcelsTestCase2() {
            // given
            val userId = "sopo@sooopo.com"
            val command = ParcelCommand.GetOngoingParcels(userId)

            // when
            val ongoingParcels = parcelService.retrieveOngoingParcels(command)

            // then
            Assertions.assertTrue(ongoingParcels.isEmpty())
        }

        @Test
        @DisplayName("진행 중인 택배가 20개 이상 존재할 수 있다.(과거에는 20개로 제한되었음)")
        @DatabaseSetup(value = ["classpath:/dbunit/Retrieve_ongoing_Test_DataSet.xml"], type = DatabaseOperation.CLEAN_INSERT)
        @DatabaseTearDown(value = ["classpath:/dbunit/Retrieve_ongoing_Test_DataSet.xml"], type = DatabaseOperation.DELETE_ALL)
        fun retrieveOngoingParcelsTestCase3() {
            // given
            val userId = "sopo@sooopo.com"
            val command = ParcelCommand.GetOngoingParcels(userId)

            // when
            val ongoingParcels = parcelService.retrieveOngoingParcels(command)

            // then
            Assertions.assertTrue(ongoingParcels.size >= 20)
        }
    }

    @Nested
    @DisplayName("완료된 택배 조회 테스트")
    inner class RetrieveCompleteParcelsTest() {

        @Test
        @DisplayName("정상 케이스")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.CLEAN_INSERT)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun retrieveCompletesTestCase1() {
            // given
            val userId = "sopo@sooopo.com"
            val inquiryDate = "202108"
            val command = ParcelCommand.GetCompleteParcels(userId, inquiryDate, PageRequest.of(0, 20))

            // when
            val completeParcels = parcelService.retrieveCompleteParcels(command)

            // then
            Assertions.assertTrue(completeParcels.size <= 20)
            completeParcels.parallelStream().forEach { parcel ->
                Assertions.assertTrue(parcel is ParcelInfo.Main)
                Assertions.assertEquals(parcel.userId, userId)
                Assertions.assertEquals(parcel.status, 1)
                Assertions.assertEquals(parcel.deliveryStatus, Parcel.DeliveryStatus.DELIVERED)
            }
        }

        @Test
        @DisplayName("진행 중인 택배가 없다면 빈 리스트를 리턴해야한다.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun retrieveCompletesTestCase2() {
            // given
            val userId = "sopo@sooopo.com"
            val inquiryDate = "202108"
            val command = ParcelCommand.GetCompleteParcels(userId, inquiryDate, PageRequest.of(0, 20))

            // when
            val completeParcels = parcelService.retrieveCompleteParcels(command)

            Assertions.assertTrue(completeParcels.isEmpty())
        }
    }

    @Nested
    @DisplayName("월별 등록된 '완료 택배' 개수 조회 테스트")
    inner class RetrieveMonthlyParcelCntListTest() {
        @Test
        @DisplayName("정상 케이스 - 유저에게 월별 특정 개수의 택배가 주어진다면, 월별 등록된 완료된 택배의 개수는 특정 개수랑 일치해야한다.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun retrieveMonthlyParcelCntListTestCase1() {
            // given
            val userId = "sopo@sooopo.com"
            val monthCnt = 5
            val dayCnt = 3

            for (i in 1..monthCnt) {
                for (j in 1..dayCnt) {
                    val initParcel = Parcel(
                        null,
                        userId,
                        "test_waybillNum",
                        Carrier.CJ_LOGISTICS.CODE,
                        "test_parcel ($i/$j)"
                    ).apply {
                        regDte = ZonedDateTime.of(LocalDateTime.of(2021, i, j, 0, 0), ZoneId.of("Asia/Seoul"))
                        arrivalDte = ZonedDateTime.of(LocalDateTime.of(2021, i, j, 0, 0), ZoneId.of("Asia/Seoul"))
                        deliveryStatus = Parcel.DeliveryStatus.DELIVERED
                    }
                    parcelStore.store(initParcel)
                }
            }

            // when
            val monthlyParcelCntList =
                parcelService.retrieveMonthlyParcelCntList(ParcelCommand.GetMonthlyParcelCnt(userId))
            logger.info("list : $monthlyParcelCntList")

            // then
            Assertions.assertEquals(monthlyParcelCntList.size, monthCnt)
            monthlyParcelCntList.parallelStream().forEach { monthlyCnt ->
                Assertions.assertEquals(monthlyCnt.count.toInt(), dayCnt)
            }
        }
    }

    @Nested
    @DisplayName("택배 별칭 수정 테스트")
    inner class ChangeParcelAliasTest() {
        @Test
        @DisplayName("정상 케이스 - 유저가 택배의 별칭을 변경한다면, 변경되어야한다.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun changeParcelAliasTestCase1() {
            // given
            val userId = "sopo@sooopo.com"
            val initParcel = Parcel(null, userId, "test_waybillNum", Carrier.CJ_LOGISTICS.CODE, "test_parcel")
            val parcel = parcelStore.store(initParcel)
            val aliasContent = "change_alias"

            // when
            parcelService.changeParcelAlias(ParcelCommand.ChangeParcelAlias(userId, parcel.id, aliasContent))
            val target = parcelReader.getParcel(parcel.id, userId)

            // then
            Assertions.assertTrue(aliasContent == target.alias)
        }

        @Test
        @DisplayName("별칭은 최대 25글자까지만 변경이 가능하다.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun changeParcelAliasTestCase2() {
            // given
            val userId = "sopo@sooopo.com"
            val initParcel = Parcel(null, userId, "test_waybillNum", Carrier.CJ_LOGISTICS.CODE, "test_parcel")
            val parcel = parcelStore.store(initParcel)
            val aliasContent = "change_alias_change_alias_change_alias"

            // when
            // then
            Assertions.assertThrows(ValidationException::class.java) {
                parcelService.changeParcelAlias(ParcelCommand.ChangeParcelAlias(userId, parcel.id, aliasContent))
            }
        }
    }

    @Nested
    @DisplayName("택배 삭제 테스트")
    inner class DeleteParcelTest() {
        @Test
        @DisplayName("현재 진행중인 택배가 복수로 주어지고, 모두 삭제한다고 다시 조회한다면, 현재 진행중인 택배는 0건이어야한다.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.CLEAN_INSERT)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun deleteParcelsTestCase1() {
            // given
            val userId = "sopo@sooopo.com"
            val ongoingParcels = parcelReader.getOngoingParcels(userId)
            Assertions.assertTrue(ongoingParcels.isNotEmpty())
            val idList = ongoingParcels.stream().map(Parcel::id).toList()

            // when
            parcelService.deleteParcel(ParcelCommand.DeleteParcel(userId, idList))
            val afterDelete = parcelReader.getOngoingParcels(userId)

            // then
            Assertions.assertEquals(0, afterDelete.size)
        }

        @Test
        @DisplayName("유저 소유가 아닌 택배를 삭제하려한다면, UnauthorizedException가 발생되어야한다.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.CLEAN_INSERT)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun deleteParcelsTestCase2() {
            // given
            val user1 = "sopo@sooopo.com"
            val user1ParcelId = 1L
            val user2 = "sopo2@sooopo.com"
            val user2ParcelId = 2L

            val user1Parcel = parcelReader.getParcel(user1ParcelId, user1)
            val user2Parcel = parcelReader.getParcel(user2ParcelId, user2)

            // when / then
            Assertions.assertThrows(UnauthorizedException::class.java) {
                parcelService.deleteParcel(ParcelCommand.DeleteParcel(user1, listOf(user2Parcel.id)))
                parcelService.deleteParcel(ParcelCommand.DeleteParcel(user2, listOf(user1Parcel.id)))
            }
        }
    }

    @Nested
    @DisplayName("택배 등록 테스트")
    inner class RegisterParcelTest() {
        @Test
        @DisplayName("searchProcessor에서 조회한 결과가 null이라면, 배송 상태가 NOT_REGISTERED로 설정되어야한다.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun registerParcelTestCase1() {
            // given
            val userId = "sopo@sooopo.com"
            val carrier = Carrier.CJ_LOGISTICS

            val mockedSearchProcessor: SearchProcessor = mockk()
            every { mockedSearchProcessor.search(any()) } returns null

            // when
            val parcelService =
                ParcelServiceImpl(parcelReader, mockedSearchProcessor, updateProcessor, registerProcessor, mapper)
            val registerParcel =
                parcelService.registerParcel(ParcelCommand.RegisterParcel(userId, carrier, "test_123123", "test_alias"))

            // then
            Assertions.assertEquals(Parcel.DeliveryStatus.NOT_REGISTERED, registerParcel.deliveryStatus)
        }

        @Test
        @DisplayName("이미 등록한 택배라면, AlreadyRegisteredParcelException가 발생해야한다.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun registerParcelTestCase2() {
            // given
            val userId = "sopo@sooopo.com"
            val carrier = Carrier.CJ_LOGISTICS
            val alias = "test_alias"
            val waybillNum = "test_num"
            val initParcel = Parcel(null, userId, waybillNum, carrier.CODE, alias)
            parcelStore.store(initParcel)

            val mockedSearchProcessor: SearchProcessor = mockk()
            every { mockedSearchProcessor.search(any()) } returns null
            val parcelService =
                ParcelServiceImpl(parcelReader, mockedSearchProcessor, updateProcessor, registerProcessor, mapper)

            // when, then
            Assertions.assertThrows(AlreadyRegisteredParcelException::class.java) {
                parcelService.registerParcel(ParcelCommand.RegisterParcel(userId, carrier, waybillNum, alias))
            }
        }

        @Test
        @DisplayName("한달에 50개 초과의 택배는 등록할 수 없다.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun registerParcelTestCase3() {
            val userId = "sopo@sooopo.com"
            val carrier = Carrier.CJ_LOGISTICS
            val alias = "test_alias"
            val waybillNum = "test_num"

            for (i in 1..50) {
                val initParcel =
                    Parcel(null, userId, "test_waybillNum_$i", Carrier.CJ_LOGISTICS.CODE, "test_parcel ($i)").apply {
                        regDte = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                        deliveryStatus = Parcel.DeliveryStatus.IN_TRANSIT
                    }
                parcelStore.store(initParcel)
            }
            val mockedSearchProcessor: SearchProcessor = mockk()
            every { mockedSearchProcessor.search(any()) } returns null
            val parcelService =
                ParcelServiceImpl(parcelReader, mockedSearchProcessor, updateProcessor, registerProcessor, mapper)

            Assertions.assertThrows(OverRegisteredParcelException::class.java) {
                parcelService.registerParcel(ParcelCommand.RegisterParcel(userId, carrier, waybillNum, alias))
            }
        }

        @Test
        @DisplayName("택배를 등록할 때, 택배 별칭이 빈 값이고 조회되는 정보가 없다면(null) 택배별칭은 송장번호가 된다.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun registerParcelTestCase4() {
            // given
            val userId = "sopo@sooopo.com"
            val carrier = Carrier.CJ_LOGISTICS
            val alias = ""
            val waybillNum = "test_num"

            val mockedSearchProcessor: SearchProcessor = mockk()
            every { mockedSearchProcessor.search(any()) } returns null
            val parcelService =
                ParcelServiceImpl(parcelReader, mockedSearchProcessor, updateProcessor, registerProcessor, mapper)

            // when
            val parcelInfo =
                parcelService.registerParcel(ParcelCommand.RegisterParcel(userId, carrier, waybillNum, alias))

            // then
            Assertions.assertEquals(waybillNum, parcelInfo.alias)
        }

        @Test
        @DisplayName("택배를 등록할 때, 택배별칭이 빈 값이 아니고 조회되는 정보가 없다면(null) 택배별칭은 처음에 설정한 값이 되어야한다.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun registerParcelTestCase5() {
            // given
            val userId = "sopo@sooopo.com"
            val carrier = Carrier.CJ_LOGISTICS
            val alias = "test_alias"
            val waybillNum = "test_num"

            val mockedSearchProcessor: SearchProcessor = mockk()
            every { mockedSearchProcessor.search(any()) } returns null
            val parcelService =
                ParcelServiceImpl(parcelReader, mockedSearchProcessor, updateProcessor, registerProcessor, mapper)

            // when
            val parcelInfo =
                parcelService.registerParcel(ParcelCommand.RegisterParcel(userId, carrier, waybillNum, alias))

            // then
            Assertions.assertEquals(alias, parcelInfo.alias)
        }

        @Test
        @DisplayName("택배를 등록할 때, 택배별칭이 빈 값이고 조회되는 정보(From)에 택배별칭이 있으면 택배별칭은 from에 있는 값이 되어야한다.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun registerParcelTestCase6() {
            // given
            val userId = "sopo@sooopo.com"
            val carrier = Carrier.CJ_LOGISTICS
            val alias = ""
            val waybillNum = "test_num"
            val from = From("sopo", "")

            val mockedSearchProcessor: SearchProcessor = mockk()
            every { mockedSearchProcessor.search(any()) } returns TrackingInfo(
                from,
                null,
                State("IN_TRANSIT", "text"),
                null,
                arrayListOf(),
                null
            )
            val parcelService =
                ParcelServiceImpl(parcelReader, mockedSearchProcessor, updateProcessor, registerProcessor, mapper)

            // when
            val parcelInfo =
                parcelService.registerParcel(ParcelCommand.RegisterParcel(userId, carrier, waybillNum, alias))

            // then
            Assertions.assertEquals("보내는 이 (${from.name})", parcelInfo.alias)
        }

        @Test
        @DisplayName("택배를 등록할 때, 택배별칭이 빈 값이 아니고 조회되는 정보(From)에 택배별칭이 있으면 택배별칭은 처음에 설정한 택배별칭이 되어야한다.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun registerParcelTestCase7() {
            // given
            val userId = "sopo@sooopo.com"
            val carrier = Carrier.CJ_LOGISTICS
            val alias = "test_alias"
            val waybillNum = "test_num"
            val from = From("sopo", "")

            val mockedSearchProcessor: SearchProcessor = mockk()
            every { mockedSearchProcessor.search(any()) } returns TrackingInfo(
                from,
                null,
                State("IN_TRANSIT", "text"),
                null,
                arrayListOf(),
                null
            )
            val parcelService =
                ParcelServiceImpl(parcelReader, mockedSearchProcessor, updateProcessor, registerProcessor, mapper)

            // when
            val parcelInfo =
                parcelService.registerParcel(ParcelCommand.RegisterParcel(userId, carrier, waybillNum, alias))

            // then
            Assertions.assertEquals(alias, parcelInfo.alias)
        }

        @Test
        @DisplayName("택배를 등록할 때, 조회되는 정보가 없으면(null) 도착시간은 null이 되어야한다.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun registerParcelTestCase8() {
            // given
            val userId = "sopo@sooopo.com"
            val carrier = Carrier.CJ_LOGISTICS
            val alias = "test_alias"
            val waybillNum = "test_num"
            val from = From("sopo", "")

            val mockedSearchProcessor: SearchProcessor = mockk()
            every { mockedSearchProcessor.search(any()) } returns TrackingInfo(
                from,
                null,
                State("IN_TRANSIT", "text"),
                null,
                arrayListOf(),
                null
            )
            val parcelService =
                ParcelServiceImpl(parcelReader, mockedSearchProcessor, updateProcessor, registerProcessor, mapper)

            // when
            val parcelInfo =
                parcelService.registerParcel(ParcelCommand.RegisterParcel(userId, carrier, waybillNum, alias))

            // then
            Assertions.assertEquals(null, parcelInfo.arrivalDte)
        }

    }

    @Nested
    @DisplayName("단일 택배 업데이트 테스트")
    inner class SingleRefreshTest() {
        @Test
        @DisplayName("택배가 주어졌을 때, 추적 정보가 변경된다면, 택배는 업데이트 되어야한다.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun singleRefreshTestCase1() {
            // given
            val mockParcelReader: ParcelReader = mockk()
            val mockSearchProc: SearchProcessor = mockk()

            val userId = "sopo@sooopo.com"
            val parcelId = 1L
            val originalParcel =
                Parcel(null, userId, "test_waybillNum", Carrier.CJ_LOGISTICS.CODE, "test_parcel").apply {
                    id = parcelId
                    inquiryHash = "1111111"
                }
            val updatedTracingInfo =
                TrackingInfo(From(null, null), To("name", null), State("IN_TRANSIT", "text"), null, arrayListOf(), null)

            every { mockParcelReader.getParcel(any(), any()) } returns originalParcel
            every { mockSearchProc.search(any()) } returns updatedTracingInfo

            val mockedService =
                ParcelServiceImpl(mockParcelReader, mockSearchProc, updateProcessor, registerProcessor, mapper)

            // when
            val singleRefresh =
                mockedService.singleRefresh(refreshCommand = ParcelCommand.SingleRefresh(userId, parcelId))

            // then
            Assertions.assertTrue(singleRefresh.isUpdated)
        }

        @Test
        @DisplayName("2주간 택배의 배송 상태가 NOT_REGISTERED이고 단일 택배 업데이트를 시도했을 때 결과값이 null이면 택배 상태가 ORPHANED으로 변경되어야한다.")
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun singleRefreshTestCase2() {

            // given
            val userId = "sopo@sooopo.com"
            val minusWeeks = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).minusWeeks(3L)

            val parcel =
                Parcel(null, userId, "test_waybillNum_1", Carrier.CJ_LOGISTICS.CODE, "test_parcel (1)").apply {
                    deliveryStatus = Parcel.DeliveryStatus.NOT_REGISTERED
                    regDte = minusWeeks
                    auditDte = minusWeeks
                }

            val mockSearchProc: SearchProcessor = mockk()
            val mockReader: ParcelReader = mockk()
            every { mockSearchProc.search(any()) } returns null
            every { mockReader.getParcel(any(), any()) } returns parcel

            val mockedService =
                ParcelServiceImpl(mockReader, mockSearchProc, updateProcessor, registerProcessor, mapper)

            // when
            val result = mockedService.singleRefresh(ParcelCommand.SingleRefresh(userId, parcel.id))

            // then
            Assertions.assertEquals(Parcel.DeliveryStatus.ORPHANED, result.parcel.deliveryStatus)
        }
    }

    @Nested
    @DisplayName("전체 택배 업데이트 테스트")
    @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
    inner class EntireRefreshTest() {
        @Test
        @DisplayName("5개의 진행 중인 택배가 주어지고, 3번째 업데이트 시도에서 Exception이 발생하더라도 4번째, 5번째 택배까지 업데이트 시도가 되어야하며, 업데이트된 택배는 4개여야한다.")
        @DatabaseSetup(
            value = ["classpath:/dbunit/EntireRefresh_Test_DataSet.xml"],
            type = DatabaseOperation.CLEAN_INSERT
        )
        fun entireRefreshTestCase1() {
            val userId = "sopo@sooopo.com"
            val mockSearchProc: SearchProcessor = mockk()

            for (id in 1L..5L) {
                val parcel = parcelReader.getParcel(id, userId)
                if (id == 3L) {
                    // 3번째 택배를 조회했을 땐, 에러를 내뿜는다.
                    every {
                        mockSearchProc.search(
                            ParcelCommand.SearchRequest(
                                parcel.userId,
                                Carrier.getCarrierByCode(parcel.carrier),
                                parcel.waybillNum
                            )
                        )
                    } throws ParcelNotFoundException()
                } else {
                    every {
                        mockSearchProc.search(
                            ParcelCommand.SearchRequest(
                                parcel.userId,
                                Carrier.getCarrierByCode(parcel.carrier),
                                parcel.waybillNum
                            )
                        )
                    } returns null
                }
            }
            val mockedService =
                ParcelServiceImpl(parcelReader, mockSearchProc, updateProcessor, registerProcessor, mapper)

            // when
            val refreshedParcelIds = mockedService.entireRefresh(ParcelCommand.EntireRefresh(userId)).sorted().toTypedArray()

            // then
            Assertions.assertEquals(4, refreshedParcelIds.size)
            Assertions.assertArrayEquals(arrayOf(1L, 2L, 4L, 5L), refreshedParcelIds, "")
        }
    }

}