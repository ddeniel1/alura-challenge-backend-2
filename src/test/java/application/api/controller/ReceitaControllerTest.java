package application.api.controller;

import application.jpa.entities.Categoria;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ReceitaController.class)
class ReceitaControllerTest {

    @MockBean
    ReceitaServiceImpl receitaService;

    @MockBean
    ReceitaRepository receitaRepository;

    @MockBean
    DespesaRepository despesaRepository;

    @MockBean
    CategoriaServiceImpl categoriaService;

    @MockBean
    CategoriaRepository categoriaRepository;

    @Autowired
    MockMvc mockMvc;


    @Test
    void createReceita() throws Exception {

        String receitaString = getReceitaString(202, "2022-02-01", "desc - 1");
        Receita receita = getReceitaFromDate("2022-02-01", 202);
        Categoria categoria = receita.getCategoria();

        when(receitaService.createReceita(any())).thenReturn(ResponseEntity.ok(receita));

        mockMvc.perform(post("/receitas").contentType(MediaType.APPLICATION_JSON).content(receitaString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.aMapWithSize(5)))
                .andExpect(jsonPath("$.categoria.id", Matchers.is(categoria.getId())))
                .andExpect(jsonPath("$.categoria.descricao", Matchers.is(categoria.getDescricao())))
                .andExpect(jsonPath("$.valor", Matchers.is(receita.getValor())))
                .andExpect(jsonPath("$.id", Matchers.is(receita.getId())))
                .andExpect(jsonPath("$.descricao", Matchers.is("desc - 1")))
                .andExpect(jsonPath("$.data", Matchers.is("2022-02-01")));

    }

    @Test
    void createDummies() throws Exception {
        Receita receita = getReceitaFromDate("2022-02-01", 202);
        Categoria categoria = receita.getCategoria();


        when(categoriaService.findById(anyInt())).thenReturn(categoria);
        when(receitaService.createReceita(any())).thenReturn(ResponseEntity.ok(receita));

        mockMvc.perform(get("/receitas/dummy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",Matchers.hasSize(10)));
    }

    @Test
    void getReceitas() throws Exception {
        String receitaString = getReceitaString(1, "2022-02-01", "desc - 1");
        Receita receita = getReceitaFromDate("2022-02-01", 1);
        Categoria categoria = receita.getCategoria();

        List<Receita> receitaList = Collections.singletonList(receita);

        when(receitaService.findAll()).thenReturn(receitaList);
        when(receitaService.findAllByDescricao(any())).thenReturn(receitaList);

        //Teste do get com parâmetros

        mockMvc.perform(get("/receitas").content(receitaString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].categoria.id", Matchers.is(categoria.getId())))
                .andExpect(jsonPath("$[0].categoria.descricao", Matchers.is(categoria.getDescricao())))
                .andExpect(jsonPath("$[0].valor", Matchers.is(245D)))
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[0].descricao", Matchers.is("desc - 1")))
                .andExpect(jsonPath("$[0].data", Matchers.is("2022-02-01")));

        //Teste do get sem parâmetros

        mockMvc.perform(get("/receitas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].categoria.id", Matchers.is(categoria.getId())))
                .andExpect(jsonPath("$[0].categoria.descricao", Matchers.is(categoria.getDescricao())))
                .andExpect(jsonPath("$[0].valor", Matchers.is(245D)))
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[0].descricao", Matchers.is("desc - 1")))
                .andExpect(jsonPath("$[0].data", Matchers.is("2022-02-01")));
    }

    @Test
    void updateReceita() throws Exception {
        String receitaString = getReceitaString(402, "2022-02-01","desc - 402");
        Receita receita = getReceitaFromDate("2022-02-01", 402);
        Categoria categoria = receita.getCategoria();

        List<Receita> receitaList = Collections.singletonList(receita);

        when(receitaService.findAll()).thenReturn(receitaList);
        when(receitaService.findByIdOrDefault(anyInt(), any())).thenReturn(receita);
        when(receitaService.validateReceita(receita)).thenReturn(null);
        when(receitaRepository.findAllByDescricaoAndData_YearAndData_Month(anyString(), anyInt(), anyInt())).thenReturn(receitaList);
        when(categoriaService.getOnMap(any())).thenReturn(categoria);

        mockMvc.perform(put(String.format("/receitas/%d", 402)).contentType(MediaType.APPLICATION_JSON).content(receitaString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.aMapWithSize(5)))
                .andExpect(jsonPath("$.categoria.id", Matchers.is(categoria.getId())))
                .andExpect(jsonPath("$.categoria.descricao", Matchers.is(categoria.getDescricao())))
                .andExpect(jsonPath("$.valor", Matchers.is(245D)))
                .andExpect(jsonPath("$.id", Matchers.is(402)))
                .andExpect(jsonPath("$.descricao", Matchers.is("desc - 402")))
                .andExpect(jsonPath("$.data", Matchers.is("2022-02-01")));

        verify(receitaService, times(1)).updateFromReceita(any());

    }

    @Test
    void deleteReceita() throws Exception {
        Receita receita = getReceitaFromDate("2022-02-01", 205);

        receitaService.createReceita(receita);

        when(receitaRepository.existsById(receita.getId())).thenReturn(true);


        mockMvc.perform(delete(String.format("/receitas/%d", 205)))
                .andExpect(status().isOk());

        verify(receitaService,times(1)).deleteReceitaResponse(anyInt());

    }

    @Test
    void getReceitasByYearAndMonth() throws Exception {

        Receita receita = getReceitaFromDate("2022-12-01", 1);
        List<Receita> receitaList = Collections.singletonList(receita);
        Categoria categoria = receita.getCategoria();

        when(receitaService.findAllByYearAndMonth(2022, (short) 12)).thenReturn(receitaList);


        mockMvc.perform(get(String.format("/receitas/%d/%d", 2022, 12)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].categoria.id", Matchers.is(categoria.getId())))
                .andExpect(jsonPath("$[0].categoria.descricao", Matchers.is(categoria.getDescricao())))
                .andExpect(jsonPath("$[0].valor", Matchers.is(245D)))
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[0].descricao", Matchers.is("desc - 1")))
                .andExpect(jsonPath("$[0].data", Matchers.is("2022-12-01")));

    }

    @Test
    void getReceitaById() throws Exception {
        Receita receita = getReceitaFromDate("2022-02-01", 101);
        Categoria categoria = receita.getCategoria();

        when(receitaService.getReceitaResponseEntity(101)).thenReturn(ResponseEntity.ok(receita));
        when(receitaRepository.findById(101)).thenReturn(Optional.of(receita));


        mockMvc.perform(get(String.format("/receitas/%d", 101)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.aMapWithSize(5)))
                .andExpect(jsonPath("$.categoria.id", Matchers.is(categoria.getId())))
                .andExpect(jsonPath("$.categoria.descricao", Matchers.is(categoria.getDescricao())))
                .andExpect(jsonPath("$.valor", Matchers.is(245D)))
                .andExpect(jsonPath("$.id", Matchers.is(101)))
                .andExpect(jsonPath("$.descricao", Matchers.is("desc - 1")))
                .andExpect(jsonPath("$.data", Matchers.is("2022-02-01")));
    }

    private Receita getReceitaFromDate(String date, Integer id) {
        Receita receita = new Receita();
        receita.setDescricao("desc - 1");
        receita.setData(LocalDate.parse(date));
        receita.setId(id);
        receita.setValor(245D);
        Categoria categoria = new Categoria();
        categoria.setDescricao("Outras");
        categoria.setId(7);
        receita.setCategoria(categoria);
        return receita;
    }

    private String getReceitaString(int id, String date, String descricao) {
        return "{\n" +
                "    \"categoria\": \"Outras\",\n" +
                "    \"data\": \"" + date + "\",\n" +
                "    \"descricao\": \"" + descricao + "\",\n" +
                "    \"id\": " + id + ",\n" +
                "    \"valor\": 245\n" +
                "  }";
    }
}