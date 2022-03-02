package application.api.controller;

import application.jpa.entities.Categoria;
import application.jpa.entities.Despesa;
import application.jpa.repository.CategoriaRepository;
import application.jpa.repository.DespesaRepository;
import application.jpa.repository.ReceitaRepository;
import application.jpa.service.impl.CategoriaServiceImpl;
import application.jpa.service.impl.DespesaServiceImpl;
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
@WebMvcTest(DespesaController.class)
class DespesaControllerTest {

    @MockBean
    DespesaServiceImpl despesaService;

    @MockBean
    CategoriaServiceImpl categoriaService;

    @MockBean
    CategoriaRepository categoriaRepository;

    @MockBean
    ReceitaRepository receitaRepository;

    @MockBean
    DespesaRepository despesaRepository;

    @Autowired
    MockMvc mockMvc;


    @Test
    void createDespesa() throws Exception {

        String despesaString = getDespesaString(202, "2022-02-01", "desc - 1");
        Despesa despesa = getDespesaFromDate("2022-02-01", 202);
        Categoria categoria = despesa.getCategoria();

        when(despesaService.createDespesa(any())).thenReturn(ResponseEntity.ok(despesa));

        mockMvc.perform(post("/despesas").contentType(MediaType.APPLICATION_JSON).content(despesaString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.aMapWithSize(5)))
                .andExpect(jsonPath("$.categoria.id", Matchers.is(categoria.getId())))
                .andExpect(jsonPath("$.categoria.descricao", Matchers.is(categoria.getDescricao())))
                .andExpect(jsonPath("$.valor", Matchers.is(despesa.getValor())))
                .andExpect(jsonPath("$.id", Matchers.is(despesa.getId())))
                .andExpect(jsonPath("$.descricao", Matchers.is("desc - 1")))
                .andExpect(jsonPath("$.data", Matchers.is("2022-02-01")));

    }

    @Test
    void createDummies() throws Exception {
        Despesa despesa = getDespesaFromDate("2022-02-01", 202);
        Categoria categoria = despesa.getCategoria();


        when(categoriaService.findById(anyInt())).thenReturn(categoria);
        when(despesaService.createDespesa(any())).thenReturn(ResponseEntity.ok(despesa));

        mockMvc.perform(get("/despesas/dummy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",Matchers.hasSize(10)));
    }

    @Test
    void getDespesas() throws Exception {
        String despesaString = getDespesaString(1, "2022-02-01", "desc - 1");
        Despesa despesa = getDespesaFromDate("2022-02-01", 1);
        Categoria categoria = despesa.getCategoria();

        List<Despesa> despesaList = Collections.singletonList(despesa);

        when(despesaService.findAll()).thenReturn(despesaList);
        when(despesaService.findAllByDescricao(any())).thenReturn(despesaList);

        //Teste do get com parâmetros

        mockMvc.perform(get("/despesas").content(despesaString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].categoria.id", Matchers.is(categoria.getId())))
                .andExpect(jsonPath("$[0].categoria.descricao", Matchers.is(categoria.getDescricao())))
                .andExpect(jsonPath("$[0].valor", Matchers.is(245D)))
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[0].descricao", Matchers.is("desc - 1")))
                .andExpect(jsonPath("$[0].data", Matchers.is("2022-02-01")));

        //Teste do get sem parâmetros

        mockMvc.perform(get("/despesas"))
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
    void updateDespesa() throws Exception {
        String despesaString = getDespesaString(402, "2022-02-01","desc - 402");
        Despesa despesa = getDespesaFromDate("2022-02-01", 402);
        Categoria categoria = despesa.getCategoria();

        List<Despesa> despesaList = Collections.singletonList(despesa);

        when(despesaService.findAll()).thenReturn(despesaList);
        when(despesaService.findByIdOrDefault(anyInt(), any())).thenReturn(despesa);
        when(despesaService.validateDespesa(despesa)).thenReturn(null);
        when(despesaRepository.findAllByDescricaoAndData_YearAndData_Month(anyString(), anyInt(), anyInt())).thenReturn(despesaList);
        when(categoriaService.getOnMap(any())).thenReturn(categoria);

        mockMvc.perform(put(String.format("/despesas/%d", 402)).contentType(MediaType.APPLICATION_JSON).content(despesaString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.aMapWithSize(5)))
                .andExpect(jsonPath("$.categoria.id", Matchers.is(categoria.getId())))
                .andExpect(jsonPath("$.categoria.descricao", Matchers.is(categoria.getDescricao())))
                .andExpect(jsonPath("$.valor", Matchers.is(245D)))
                .andExpect(jsonPath("$.id", Matchers.is(402)))
                .andExpect(jsonPath("$.descricao", Matchers.is("desc - 402")))
                .andExpect(jsonPath("$.data", Matchers.is("2022-02-01")));

        verify(despesaService, times(1)).updateFromDespesa(any());

    }

    @Test
    void deleteDespesa() throws Exception {
        Despesa despesa = getDespesaFromDate("2022-02-01", 205);

        despesaService.createDespesa(despesa);

        when(despesaRepository.existsById(despesa.getId())).thenReturn(true);


        mockMvc.perform(delete(String.format("/despesas/%d", 205)))
                .andExpect(status().isOk());

        verify(despesaService,times(1)).deleteDespesaResponse(anyInt());

    }

    @Test
    void getDespesasByYearAndMonth() throws Exception {

        Despesa despesa = getDespesaFromDate("2022-12-01", 1);
        List<Despesa> despesaList = Collections.singletonList(despesa);
        Categoria categoria = despesa.getCategoria();

        when(despesaService.findAllByYearAndMonth(2022, (short) 12)).thenReturn(despesaList);


        mockMvc.perform(get(String.format("/despesas/%d/%d", 2022, 12)))
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
    void getDespesaById() throws Exception {
        Despesa despesa = getDespesaFromDate("2022-02-01", 101);
        Categoria categoria = despesa.getCategoria();

        when(despesaService.getDespesaResponseEntity(101)).thenReturn(ResponseEntity.ok(despesa));
        when(despesaRepository.findById(101)).thenReturn(Optional.of(despesa));


        mockMvc.perform(get(String.format("/despesas/%d", 101)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.aMapWithSize(5)))
                .andExpect(jsonPath("$.categoria.id", Matchers.is(categoria.getId())))
                .andExpect(jsonPath("$.categoria.descricao", Matchers.is(categoria.getDescricao())))
                .andExpect(jsonPath("$.valor", Matchers.is(245D)))
                .andExpect(jsonPath("$.id", Matchers.is(101)))
                .andExpect(jsonPath("$.descricao", Matchers.is("desc - 1")))
                .andExpect(jsonPath("$.data", Matchers.is("2022-02-01")));
    }

    private Despesa getDespesaFromDate(String date, Integer id) {
        Despesa despesa = new Despesa();
        despesa.setDescricao("desc - 1");
        despesa.setData(LocalDate.parse(date));
        despesa.setId(id);
        despesa.setValor(245D);
        Categoria categoria = new Categoria();
        categoria.setDescricao("Outras");
        categoria.setId(7);
        despesa.setCategoria(categoria);
        return despesa;
    }

    private String getDespesaString(int id, String date, String descricao) {
        return "{\n" +
                "    \"categoria\": \"Outras\",\n" +
                "    \"data\": \"" + date + "\",\n" +
                "    \"descricao\": \"" + descricao + "\",\n" +
                "    \"id\": " + id + ",\n" +
                "    \"valor\": 245\n" +
                "  }";
    }
}