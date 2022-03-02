package application;

import application.api.controller.DespesaController;
import application.api.controller.ReceitaController;
import application.api.controller.ResumoController;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ApplicationStartTest {

    @Autowired
    DespesaController despesaController;

    @Autowired
    ReceitaController receitaController;

    @Autowired
    ResumoController resumoController;

    @Autowired
    ApplicationStart start;

    @Test
    void main() {
        ApplicationStart.main(new String[]{"test"});
        assertThat(despesaController).isNotNull();
        assertThat(receitaController).isNotNull();
        assertThat(resumoController).isNotNull();
        assertThat(start).isNotNull();
    }
}