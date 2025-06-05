package com.universidade.sighoras.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import IntegrandoDrive.controller.FileController;

@Configuration
public class DriveConfig {
    
    /**
     * Cria e configura um bean do FileController para ser usado pelo Spring
     */
    @Bean
    public FileController fileController() {
        // Instancia manualmente a classe da biblioteca externa
        FileController controller = new FileController(null);
        
        // Configure quaisquer propriedades necessárias
        // controller.setProperty("value");
        
        return controller;
    }
}