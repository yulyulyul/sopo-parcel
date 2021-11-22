package team.sopo.parcel

import com.github.springtestdbunit.bean.DatabaseConfigBean
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean
import org.dbunit.ext.mysql.MySqlDataTypeFactory
import org.dbunit.ext.mysql.MySqlMetadataHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class TestConfig {

    @Bean
    fun dbUnitDatabaseConfig(): DatabaseConfigBean{
        val config = DatabaseConfigBean()
        config.allowEmptyFields = true
        config.datatypeFactory = MySqlDataTypeFactory()
        config.metadataHandler = MySqlMetadataHandler()
        return config
    }

    @Bean
    fun dbUnitDatabaseConnection(dataSource: DataSource): DatabaseDataSourceConnectionFactoryBean {
        val dbUnitDatabaseConnection = DatabaseDataSourceConnectionFactoryBean()
        dbUnitDatabaseConnection.setDataSource(dataSource)
        dbUnitDatabaseConnection.setDatabaseConfig(dbUnitDatabaseConfig())
        dbUnitDatabaseConnection.setSchema("parcel")
        return dbUnitDatabaseConnection
    }
}