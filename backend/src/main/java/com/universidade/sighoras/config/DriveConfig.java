package com.universidade.sighoras.config;

import IntegrandoDrive.persistence.DrivePersistence;
import IntegrandoDrive.service.FileService;
import IntegrandoDrive.controller.FileController;
import com.google.api.services.drive.Drive;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DriveConfig {

    @Bean
    public Drive driveClient() throws Exception {
        return DrivePersistence.initDriveService();
    }

    @Bean
    public FileService fileService(Drive driveClient) {
        return new FileService(new DrivePersistence(driveClient));
    }

    @Bean
    public FileController fileController(FileService fileService) {
        return new FileController(fileService);
    }
}