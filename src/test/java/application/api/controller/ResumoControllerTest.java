package application.api.controller;

import application.jpa.entities.Categoria;
import application.jpa.entities.Despesa;
import application.jpa.entities.Receita;
import application.jpa.repository.CategoriaRepository;
import application.jpa.repository.DespesaRepository;
import application.jpa.repository.ReceitaRepository;
import application.jpa.service.impl.CategoriaServiceImpl;
import application.jpa.service.impl.DespesaServiceImpl;
import application.jpa.service.impl.ReceitaServiceImpl;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ResumoController.class)
class ResumoControllerTest {

    @MockBean
    ReceitaServiceImpl receitaService;

    @MockBean
    ReceitaRepository receitaRepository;

    @MockBean
    DespesaServiceImpl despesaService;

    @MockBean
    DespesaRepository despesaRepository;

    @MockBean
    CategoriaServiceImpl categoriaService;

    @MockBean
    CategoriaRepository categoriaRepository;

    @Autowired
    MockMvc mockMvc;

    @Test
    void getResumo() throws Exception {

        List<Categoria> categoriaList = createCategorias();
        List<Receita> receitaList = createReceitas(categoriaList);
        List<Despesa> despesaList = createDespesas(categoriaList);

        when(receitaService.findAllByYearAndMonth(2022, (short) 12)).thenReturn(receitaList);
        when(despesaService.findAllByYearAndMonth(2022, (short) 12)).thenReturn(despesaList);



        mockMvc.perform(get(String.format("/resumo/%d/%d", 2022, 12)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.aMapWithSize(4)))
                .andExpect(jsonPath("$.totalReceitas", Matchers.greaterThan(0D)))
                .andExpect(jsonPath("$.totalDespesas", Matchers.greaterThan(0D)))
                .andExpect(jsonPath("$.saldoMensal", Matchers.any(Double.class)))
                .andExpect(jsonPath("$.despesasByCategoria", Matchers.aMapWithSize(Matchers.greaterThan(0))))
        ;
    }

    private List<Despesa> createDespesas(List<Categoria> categorias) {

      List<Despesa> despesas = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Despesa novaDespesa = new Despesa();
            novaDespesa.setId(i);
            novaDespesa.setDescricao("desc - " + i);
            novaDespesa.setValor(Math.pow(i, new Random().nextInt(4)));
            novaDespesa.setData(LocalDate.now());
            novaDespesa.setCategoria(categorias.get(new Random().nextInt(6)));
            despesas.add(novaDespesa);
        }
        return despesas;
    }

    private List<Categoria> createCategorias() {
        List<Categoria> categorias = new ArrayList<>();

        categorias.add(Categoria.builder().addDescricao("Alimentação").addId(1));
        categorias.add(Categoria.builder().addDescricao("Saúde").addId(2));
        categorias.add(Categoria.builder().addDescricao("Moradia").addId(3));
        categorias.add(Categoria.builder().addDescricao("Transporte").addId(4));
        categorias.add(Categoria.builder().addDescricao("Lazer").addId(5));
        categorias.add(Categoria.builder().addDescricao("Imprevistos").addId(6));
        categorias.add(Categoria.builder().addDescricao("Outras").addId(7));
        return categorias;
    }

    private List<Receita> createReceitas(List<Categoria> categorias) {
        List<Receita> receitas = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Receita novaReceita = new Receita();
            novaReceita.setId(i);
            novaReceita.setDescricao("desc - " + i);
            novaReceita.setValor(Math.pow(i, new Random().nextInt(7)));
            novaReceita.setData(LocalDate.now());
            novaReceita.setCategoria(categorias.get(new Random().nextInt(6)));
            receitas.add(novaReceita);
        }
        return receitas;
    }
}