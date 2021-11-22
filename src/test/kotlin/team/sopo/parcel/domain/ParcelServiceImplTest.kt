package team.sopo.parcel.domain

import com.github.springtestdbunit.DbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseOperation
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseTearDown
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import team.sopo.parcel.TestConfig
import javax.transaction.Transactional

@Transactional
@Import(TestConfig::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestExecutionListeners(DbUnitTestExecutionListener::class, DependencyInjectionTestExecutionListener::class)
@DbUnitConfiguration(databaseConnection = ["dbUnitDatabaseConnection"])
@DatabaseSetup(value = ["Parcel.xml"] , type = DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(value = ["Parcel.xml"], type = DatabaseOperation.DELETE_ALL)
class ParcelServiceImplTest{

   /*
    *   TEST DB 구축 방법
    *   1. resources-test/ 아래에서 docker-compose -p parcel-db up -d
    *   2. 프로젝트 폴더로 이동 후, ./gradlew flywayClean flywayMigrate
    */

    @Autowired
    lateinit var parcelService: ParcelService

    @Test
    fun getParcelTest(){
        val parcel = parcelService.retrieveParcel(ParcelCommand.GetParcel("asle1221@naver.com", 8))
        println("parcel : $parcel")
    }
}