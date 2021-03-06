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
import team.sopo.common.exception.AlreadyRegisteredParcelException
import team.sopo.common.exception.OverRegisteredParcelException
import team.sopo.common.exception.ParcelNotFoundException
import team.sopo.common.exception.ValidationException
import team.sopo.common.util.OffsetBasedPageRequest
import team.sopo.domain.parcel.*
import team.sopo.domain.parcel.register.RegisterProcessor
import team.sopo.domain.parcel.search.SearchProcessor
import team.sopo.domain.parcel.trackinginfo.From
import team.sopo.domain.parcel.trackinginfo.State
import team.sopo.domain.parcel.trackinginfo.To
import team.sopo.domain.parcel.trackinginfo.TrackingInfo
import team.sopo.domain.parcel.update.UpdateProcessor
import team.sopo.parcel.TestConfig
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.streams.toList

//@Transactional
@Import(TestConfig::class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["spring.config.location=classpath:/application.yml"]
)
@TestExecutionListeners(DbUnitTestExecutionListener::class, DependencyInjectionTestExecutionListener::class)
@DbUnitConfiguration(databaseConnection = ["dbUnitDatabaseConnection"])
@ActiveProfiles("test")
class ParcelServiceImplTest {

    @Autowired
    lateinit var mapper: ParcelInfoMapper

    /*
     *   [TEST DB ?????? ??????]
     *   1. resources-test/ ???????????? docker-compose -p parcel-db up -d
     *   2. ???????????? ????????? ?????? ???, ./gradlew flywayClean flywayMigrate
     */

    @Autowired
    lateinit var parcelService: ParcelService

    @Autowired
    lateinit var parcelReader: ParcelReader

    @Autowired
    lateinit var parcelStore: ParcelStore

//    @Autowired
//    lateinit var searchProcessor: SearchProcessor

    @Autowired
    lateinit var registerProcessor: RegisterProcessor

    @Autowired
    lateinit var updateProcessor: UpdateProcessor

//    @Autowired
//    lateinit var parcelRepository: JpaParcelRepository

    private val logger = LogManager.getLogger(ParcelServiceImplTest::class.java)

    @Nested
    @DisplayName("?????? ?????? ?????? ?????????")
    @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.CLEAN_INSERT)
    @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
    inner class GetParcelTest {

        @Test
        @DisplayName("?????? ?????????")
        fun getParcelTestCase1() {
            // given
            val userId = 1L
            val parcelId = 1L
            val command = ParcelCommand.GetParcel(userId, parcelId)

            // when
            val getParcel = parcelService.getParcel(command)

            // then
            Assertions.assertTrue(getParcel.userId == userId)
            Assertions.assertEquals(getParcel.parcelId?.equals(parcelId), true)
        }

        @Test
        @DisplayName("???????????? ???????????? ?????? ????????? ???????????? ?????? ParcelNotFoundException??? ?????????????????????.")
        fun getParcelTestCase2() {
            logger.error("start")
            // given
            val userId = 1L
            val parcelId = 9999L
            val command = ParcelCommand.GetParcel(userId, parcelId)

            // when run getParcel(command)
            // then throw ParcelNotFoundException
            Assertions.assertThrows(ParcelNotFoundException::class.java) { parcelService.getParcel(command) }
        }
    }

    @Nested
    @DisplayName("?????? ?????? ?????? ?????????")
    @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.CLEAN_INSERT)
    @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
    inner class GetParcelsTest {

        @Test
        @DisplayName("?????? ?????????")
        fun getParcelsTestCase1() {
            // given
            val userId = 1L
            val parcelId1 = 1L
            val parcelId2 = 3L
            val parcelId3 = 4L
            val parcelIds = arrayListOf(parcelId1, parcelId2, parcelId3)
            val command = ParcelCommand.GetParcels(userId, parcelIds)

            // when
            val parcels = parcelService.getParcels(command)

            // then
            val ids = parcels.map(ParcelInfo.Main::parcelId).toList()
            Assertions.assertEquals(ids.containsAll(parcelIds), true)
        }

        @Test
        @DisplayName("???????????? ?????? ????????? ???????????? ?????? ParcelNotFoundException??? ?????????????????????.")
        fun getParcelsTestCase2() {
            // given
            val userId = 1L
            val parcelId1 = 1L
            val parcelId2 = 3L
            val parcelId3 = 999L
            val parcelIds = arrayListOf(parcelId1, parcelId2, parcelId3)
            val command = ParcelCommand.GetParcels(userId, parcelIds)

            // when run getParcels(command)
            // then
            Assertions.assertThrows(ParcelNotFoundException::class.java) { parcelService.getParcels(command) }
        }
    }

    @Nested
    @DisplayName("?????? ???????????? ?????? ?????? ?????????")
    inner class GetOngoingParcelsTest {

        @Test
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.CLEAN_INSERT)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DisplayName("?????? ????????? - ?????? ???????????? ????????? parcelInfo ????????? ????????????????????? ????????? deliveryStatus??? (DELIVERED & ORPHANED)??? ?????????????????? status??? 1???????????????.")
        fun getOngoingParcelsTestCase1() {
            // given
            val userId = 1L
            val command = ParcelCommand.GetOngoingParcels(userId)

            // when
            val ongoingParcels = parcelService.getOngoingParcels(command)

            // then
            ongoingParcels.parallelStream().forEach { parcel ->
                Assertions.assertTrue(parcel is ParcelInfo.Main)
                Assertions.assertTrue(parcel.userId == userId)
                Assertions.assertNotEquals(Parcel.DeliveryStatus.DELIVERED, parcel.deliveryStatus)
                Assertions.assertEquals(parcel.status?.equals(1), true)
            }
        }

        @Test
        @DisplayName("?????? ?????? ????????? ????????? ??? ???????????? ??????????????????.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun getOngoingParcelsTestCase2() {
            // given
            val userId = 1L
            val command = ParcelCommand.GetOngoingParcels(userId)

            // when
            val ongoingParcels = parcelService.getOngoingParcels(command)

            // then
            Assertions.assertTrue(ongoingParcels.isEmpty())
        }

        @Test
        @DisplayName("?????? ?????? ????????? 20??? ?????? ????????? ??? ??????.(???????????? 20?????? ???????????????)")
        @DatabaseSetup(
            value = ["classpath:/dbunit/get_ongoing_Test_DataSet.xml"],
            type = DatabaseOperation.CLEAN_INSERT
        )
        @DatabaseTearDown(
            value = ["classpath:/dbunit/get_ongoing_Test_DataSet.xml"],
            type = DatabaseOperation.DELETE_ALL
        )
        fun getOngoingParcelsTestCase3() {
            // given
            val userId = 1L
            val command = ParcelCommand.GetOngoingParcels(userId)

            // when
            val ongoingParcels = parcelService.getOngoingParcels(command)

            // then
            Assertions.assertTrue(ongoingParcels.size >= 20)
        }
    }

    @Nested
    @DisplayName("????????? ?????? ?????? ?????????")
    inner class GetCompleteParcelsTest {

        @Test
        @DisplayName("?????? ?????????")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.CLEAN_INSERT)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun getCompletesTestCase1() {
            // given
            val userId = 1L
            val inquiryDate = "202108"
            val command = ParcelCommand.GetCompleteParcels(userId, inquiryDate, 10, OffsetBasedPageRequest(0, 10))

            // when
            val completeParcels = parcelService.getCompleteParcels(command)

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
        @DisplayName("?????? ?????? ????????? ????????? ??? ???????????? ??????????????????.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun getCompletesTestCase2() {
            // given
            val userId = 1L
            val inquiryDate = "202108"
            val command = ParcelCommand.GetCompleteParcels(userId, inquiryDate, 5, PageRequest.of(0, 10))

            // when
            val completeParcels = parcelService.getCompleteParcels(command)

            Assertions.assertTrue(completeParcels.isEmpty())
        }

        @Test
        @DisplayName("itemCnt??? ???????????? ????????? ????????? ????????? ??? ???????????????.")
        @DatabaseSetup(
            value = ["classpath:/dbunit/Get_Complete_Test_DataSet.xml"],
            type = DatabaseOperation.CLEAN_INSERT
        )
        @DatabaseTearDown(
            value = ["classpath:/dbunit/Get_Complete_Test_DataSet.xml"],
            type = DatabaseOperation.DELETE_ALL
        )
        fun getCompletesTestCase3() {
            // given
            val userId = 1L
            val inquiryDate = "202201"
            val itemCnt = 4
            val command = ParcelCommand.GetCompleteParcels(userId, inquiryDate, itemCnt, PageRequest.of(0, itemCnt))

            // when
            val completeParcels = parcelService.getCompleteParcels(command)

            Assertions.assertEquals(itemCnt, completeParcels.size)
        }

        @Test
        @DisplayName("itemCnt??? ???????????? ????????? ????????? ???????????????.")
        @DatabaseSetup(
            value = ["classpath:/dbunit/Get_Complete_Test_DataSet.xml"],
            type = DatabaseOperation.CLEAN_INSERT
        )
        @DatabaseTearDown(
            value = ["classpath:/dbunit/Get_Complete_Test_DataSet.xml"],
            type = DatabaseOperation.DELETE_ALL
        )
        fun getCompletesTestCase4() {
            // given
            val userId = 1L
            val inquiryDate = "202201"
            val itemCnt = 7 // ??? 18??? = ( 7 / 7 / 4 )
            var offset = 0

            val command1 =
                ParcelCommand.GetCompleteParcels(userId, inquiryDate, itemCnt, PageRequest.of(offset++, itemCnt))
            val command2 =
                ParcelCommand.GetCompleteParcels(userId, inquiryDate, itemCnt, PageRequest.of(offset++, itemCnt))
            val command3 =
                ParcelCommand.GetCompleteParcels(userId, inquiryDate, itemCnt, PageRequest.of(offset, itemCnt))

            // when
            val completeParcels1 = parcelService.getCompleteParcels(command1)
            val completeParcels2 = parcelService.getCompleteParcels(command2)
            val completeParcels3 = parcelService.getCompleteParcels(command3)

            Assertions.assertEquals(itemCnt, completeParcels1.size)
            Assertions.assertEquals(itemCnt, completeParcels2.size)
            Assertions.assertEquals(4, completeParcels3.size)

        }
    }

    @Nested
    @DisplayName("?????? ????????? '?????? ??????' ?????? ?????? ?????????")
    inner class GetMonthlyParcelCntListTest {
        @Test
        @DisplayName("?????? ????????? - ???????????? ?????? ?????? ????????? ????????? ???????????????, ?????? ????????? ????????? ????????? ????????? ?????? ????????? ??????????????????.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun getMonthlyParcelCntListTestCase1() {
            // given
            val userId = 1L
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
                parcelService.getMonthlyParcelCntList(ParcelCommand.GetMonthlyParcelCnt(userId))
            logger.info("list : $monthlyParcelCntList")

            // then
            Assertions.assertEquals(monthlyParcelCntList.size, monthCnt)
            monthlyParcelCntList.parallelStream().forEach { monthlyCnt ->
                Assertions.assertEquals(monthlyCnt.count.toInt(), dayCnt)
            }
        }
    }

    @Nested
    @DisplayName("?????? ?????? ?????? ?????????")
    inner class ChangeParcelAliasTest {
        @Test
        @DisplayName("?????? ????????? - ????????? ????????? ????????? ???????????????, ?????????????????????.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun changeParcelAliasTestCase1() {
            // given
            val userId = 1L
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
        @DisplayName("????????? ?????? 25??????????????? ????????? ????????????.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun changeParcelAliasTestCase2() {
            // given
            val userId = 1L
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
    @DisplayName("?????? ?????? ?????????")
    inner class DeleteParcelTest {
        @Test
        @DisplayName("?????? ???????????? ????????? ????????? ????????????, ?????? ??????????????? ?????? ???????????????, ?????? ???????????? ????????? 0??????????????????.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.CLEAN_INSERT)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun deleteParcelsTestCase1() {
            // given
            val userId = 1L
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
        @DisplayName("?????? ????????? ?????? ????????? ?????????????????????, ParcelNotFoundException??? ?????????????????????.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.CLEAN_INSERT)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun deleteParcelsTestCase2() {
            // given
            val user1 = 1L
            val user1ParcelId = 1L
            val user2 = 2L
            val user2ParcelId = 2L

            val user1Parcel = parcelReader.getParcel(user1ParcelId, user1)
            val user2Parcel = parcelReader.getParcel(user2ParcelId, user2)

            // when / then
            Assertions.assertThrows(ParcelNotFoundException::class.java) {
                parcelService.deleteParcel(ParcelCommand.DeleteParcel(user1, listOf(user2Parcel.id)))
                parcelService.deleteParcel(ParcelCommand.DeleteParcel(user2, listOf(user1Parcel.id)))
            }
        }
    }

    @Nested
    @DisplayName("?????? ?????? ?????????")
    inner class RegisterParcelTest {
        @Test
        @DisplayName("searchProcessor?????? ????????? ????????? null?????????, ?????? ????????? NOT_REGISTERED??? ?????????????????????.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun registerParcelTestCase1() {
            // given
            val userId = 1L
            val carrier = Carrier.CJ_LOGISTICS

            val mockedSearchProcessor: SearchProcessor = mockk()
            every { mockedSearchProcessor.search(any()) } returns null

            // when
            val parcelService =
                ParcelServiceImpl(parcelReader, mockedSearchProcessor, updateProcessor, registerProcessor, mapper)
            val registerParcel =
                parcelService.registerParcel(
                    ParcelCommand.RegisterParcel(
                        userId,
                        carrier.CODE,
                        "test_123123",
                        "test_alias"
                    )
                )

            // then
            Assertions.assertEquals(Parcel.DeliveryStatus.NOT_REGISTERED, registerParcel.deliveryStatus)
        }

        @Test
        @DisplayName("?????? ????????? ????????????, AlreadyRegisteredParcelException??? ??????????????????.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun registerParcelTestCase2() {
            // given
            val userId = 1L
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
                parcelService.registerParcel(ParcelCommand.RegisterParcel(userId, carrier.CODE, waybillNum, alias))
            }
        }

        @Test
        @DisplayName("????????? 50??? ????????? ????????? ????????? ??? ??????.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun registerParcelTestCase3() {
            val userId = 1L
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
                parcelService.registerParcel(ParcelCommand.RegisterParcel(userId, carrier.CODE, waybillNum, alias))
            }
        }

        @Test
        @DisplayName("????????? ????????? ???, ?????? ????????? ??? ????????? ???????????? ????????? ?????????(null) ??????????????? ??????????????? ??????.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun registerParcelTestCase4() {
            // given
            val userId = 1L
            val carrier = Carrier.CJ_LOGISTICS
            val alias = ""
            val waybillNum = "test_num"

            val mockedSearchProcessor: SearchProcessor = mockk()
            every { mockedSearchProcessor.search(any()) } returns null
            val parcelService =
                ParcelServiceImpl(parcelReader, mockedSearchProcessor, updateProcessor, registerProcessor, mapper)

            // when
            val parcelInfo =
                parcelService.registerParcel(ParcelCommand.RegisterParcel(userId, carrier.CODE, waybillNum, alias))

            // then
            Assertions.assertEquals(waybillNum, parcelInfo.alias)
        }

        @Test
        @DisplayName("????????? ????????? ???, ??????????????? ??? ?????? ????????? ???????????? ????????? ?????????(null) ??????????????? ????????? ????????? ?????? ???????????????.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun registerParcelTestCase5() {
            // given
            val userId = 1L
            val carrier = Carrier.CJ_LOGISTICS
            val alias = "test_alias"
            val waybillNum = "test_num"

            val mockedSearchProcessor: SearchProcessor = mockk()
            every { mockedSearchProcessor.search(any()) } returns null
            val parcelService =
                ParcelServiceImpl(parcelReader, mockedSearchProcessor, updateProcessor, registerProcessor, mapper)

            // when
            val parcelInfo =
                parcelService.registerParcel(ParcelCommand.RegisterParcel(userId, carrier.CODE, waybillNum, alias))

            // then
            Assertions.assertEquals(alias, parcelInfo.alias)
        }

        @Test
        @DisplayName("????????? ????????? ???, ??????????????? ??? ????????? ???????????? ??????(From)??? ??????????????? ????????? ??????????????? from??? ?????? ?????? ???????????????.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun registerParcelTestCase6() {
            // given
            val userId = 1L
            val carrier = Carrier.CJ_LOGISTICS
            val alias = ""
            val waybillNum = "test_num"
            val from = From("sopo", "", "")

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
                parcelService.registerParcel(ParcelCommand.RegisterParcel(userId, carrier.CODE, waybillNum, alias))

            // then
            Assertions.assertEquals("????????? ??? (${from.name})", parcelInfo.alias)
        }

        @Test
        @DisplayName("????????? ????????? ???, ??????????????? ??? ?????? ????????? ???????????? ??????(From)??? ??????????????? ????????? ??????????????? ????????? ????????? ??????????????? ???????????????.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun registerParcelTestCase7() {
            // given
            val userId = 1L
            val carrier = Carrier.CJ_LOGISTICS
            val alias = "test_alias"
            val waybillNum = "test_num"
            val from = From("sopo", "", "")

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
                parcelService.registerParcel(ParcelCommand.RegisterParcel(userId, carrier.CODE, waybillNum, alias))

            // then
            Assertions.assertEquals(alias, parcelInfo.alias)
        }

        @Test
        @DisplayName("????????? ????????? ???, ???????????? ????????? ?????????(null) ??????????????? null??? ???????????????.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun registerParcelTestCase8() {
            // given
            val userId = 1L
            val carrier = Carrier.CJ_LOGISTICS
            val alias = "test_alias"
            val waybillNum = "test_num"
            val from = From("sopo", "", "")

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
                parcelService.registerParcel(ParcelCommand.RegisterParcel(userId, carrier.CODE, waybillNum, alias))

            // then
            Assertions.assertEquals(null, parcelInfo.arrivalDte)
        }

    }

    @Nested
    @DisplayName("?????? ?????? ???????????? ?????????")
    inner class SingleRefreshTest {
        @Test
        @DisplayName("????????? ???????????? ???, ?????? ????????? ???????????????, ????????? ???????????? ???????????????.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun singleRefreshTestCase1() {
            // given
            val mockParcelReader: ParcelReader = mockk()
            val mockSearchProc: SearchProcessor = mockk()

            val userId = 1L
            val parcelId = 1L
            val originalParcel =
                Parcel(null, userId, "test_waybillNum", Carrier.CJ_LOGISTICS.CODE, "test_parcel").apply {
                    id = parcelId
                    inquiryHash = "1111111"
                }
            val updatedTracingInfo =
                TrackingInfo(
                    From(null, null, null),
                    To("name", null),
                    State("IN_TRANSIT", "text"),
                    null,
                    arrayListOf(),
                    null
                )

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
        @DisplayName("2?????? ????????? ?????? ????????? NOT_REGISTERED?????? ?????? ?????? ??????????????? ???????????? ??? ???????????? null?????? ?????? ????????? ORPHANED?????? ?????????????????????.")
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun singleRefreshTestCase2() {

            // given
            val userId = 1L
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

        @Test
        @DisplayName("?????? ???????????? ??????, ?????? Reporting(reported => true)??? ????????? deliveryStatus??? ????????? ????????? reported??? false??? ?????????????????????.")
        @DatabaseSetup(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.CLEAN_INSERT)
        @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
        fun singleRefreshTestCase3() {
            // given
            val mockSearchProc: SearchProcessor = mockk()

            val userId = 1L
            val parcelId = 7L

            val updatedTracingInfo =
                TrackingInfo(
                    From(null, null, null),
                    To("name", null),
                    State("DELIVERED", "text"),
                    null,
                    arrayListOf(),
                    null
                )

//            every { mockParcelReader.getParcel(any(), any()) } returns originalParcel
            every { mockSearchProc.search(any()) } returns updatedTracingInfo

            val mockedService =
                ParcelServiceImpl(parcelReader, mockSearchProc, updateProcessor, registerProcessor, mapper)

            // when
            val singleRefresh =
                mockedService.singleRefresh(refreshCommand = ParcelCommand.SingleRefresh(userId, parcelId))

            // then
            Assertions.assertFalse(singleRefresh.parcel.reported!!)
        }
    }

    @Nested
    @DisplayName("?????? ?????? ???????????? ?????????")
    @DatabaseTearDown(value = ["classpath:/dbunit/Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
    inner class EntireRefreshTest {
        @Test
        @DisplayName("5?????? ?????? ?????? ????????? ????????????, 3?????? ???????????? ???????????? Exception??? ?????????????????? 4??????, 5?????? ???????????? ???????????? ????????? ???????????????, ??????????????? ????????? 4???????????????.")
        @DatabaseSetup(
            value = ["classpath:/dbunit/EntireRefresh_Test_DataSet.xml"],
            type = DatabaseOperation.CLEAN_INSERT
        )
        fun entireRefreshTestCase1() {
            val userId = 1L
            val mockSearchProc: SearchProcessor = mockk()

            for (id in 1L..5L) {
                val parcel = parcelReader.getParcel(id, userId)
                if (id == 3L) {
                    // 3?????? ????????? ???????????? ???, ????????? ????????????.
                    every {
                        mockSearchProc.search(
                            ParcelCommand.SearchRequest(
                                parcel.userId,
                                Carrier.getCarrierByCode(parcel.carrier).CODE,
                                parcel.waybillNum
                            )
                        )
                    } throws ParcelNotFoundException()
                } else {
                    every {
                        mockSearchProc.search(
                            ParcelCommand.SearchRequest(
                                parcel.userId,
                                Carrier.getCarrierByCode(parcel.carrier).CODE,
                                parcel.waybillNum
                            )
                        )
                    } returns null
                }
            }
            val mockedService =
                ParcelServiceImpl(parcelReader, mockSearchProc, updateProcessor, registerProcessor, mapper)

            // when
            val refreshedParcelIds =
                mockedService.entireRefresh(ParcelCommand.EntireRefresh(userId)).sorted().toTypedArray()

            // then
            Assertions.assertEquals(4, refreshedParcelIds.size)
            Assertions.assertArrayEquals(arrayOf(1L, 2L, 4L, 5L), refreshedParcelIds, "")
        }
    }

}