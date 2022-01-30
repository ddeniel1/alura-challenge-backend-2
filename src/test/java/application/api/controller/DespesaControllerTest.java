package application.api.controller;

import application.jpa.service.DespesaService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(DespesaController.class)
class DespesaControllerTest {

    @MockBean
    DespesaService despesaService;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createDespesa() {

    }

    @Test
    void createDummies() {
    }

    @Test
    void getDespesas() {
    }

    @Test
    void testGetDespesas() {
    }

    @Test
    void getDespesa() {
    }

    @Test
    void updateDespesa() {
    }

    @Test
    void deleteDespesa() {
    }
}