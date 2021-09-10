package team.sopo.common.config.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream
import java.io.IOException

@Configuration
class FirebaseConfiguration {

    @Value("\${firebase.admin.database.url}")
    private val databaseUrl: String? = null

    @Value("\${firebase.admin.config.path}")
    private val configPath: String? = null

    @Bean
    @Throws(IOException::class)
    fun initializeFireBaseApp(): FirebaseApp? {

        val serviceAccount = FileInputStream(configPath)
        val options = FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl(databaseUrl)
                .build()
        return FirebaseApp.initializeApp(options)
    }
}