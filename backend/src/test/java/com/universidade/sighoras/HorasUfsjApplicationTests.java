package com.universidade.sighoras;

import IntegrandoDrive.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HorasUfsjApplicationTests {

    @MockitoBean
    private FileService fileService;

    @Test
    void contextLoads() {
    }
}
