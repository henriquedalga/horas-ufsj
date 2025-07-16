package IntegrandoDrive.config;

import com.google.api.services.drive.Drive;
import IntegrandoDrive.persistence.DrivePersistence;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntegrandoDriveConfig {
    @Value("${drive.parentId.complementary}")
    private String parentComplementary;

    @Value("${drive.parentId.extension}")
    private String parentExtension;

    @Bean
    public Drive drive() throws Exception {
        return DrivePersistence.initDriveService("/credentials.json");
    }


    @Bean(name = "persistenceComplementary")
    public DrivePersistence persistenceComplementary(Drive drive) {
        return new DrivePersistence(drive, parentComplementary);
    }

    @Bean(name = "persistenceExtension")
    public DrivePersistence persistenceExtension(Drive drive) {
        return new DrivePersistence(drive, parentExtension);
    }
} 

