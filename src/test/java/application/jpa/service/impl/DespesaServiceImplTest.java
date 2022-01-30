package application.jpa.service.impl;

import application.jpa.entities.Categoria;
import application.jpa.entities.Despesa;
import application.jpa.repository.DespesaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class DespesaServiceImplTest {

    @InjectMocks
    DespesaServiceImpl service;

    @Mock
    DespesaRepository repository;

    @Mock
    CategoriaServiceImpl categoriaService;

    @Test
    void updateFromDespesa() {

        Despesa despesa = getDespesa();

        repository.save(despesa);

        despesa.setValor(new Random().nextDouble() + 10);

        service.updateFromDespesa(despesa);

        verify(repository, times(1)).update(despesa.getId(), despesa.getDescricao(), despesa.getValor(), despesa.getData(), despesa.getCategoria().getId());

    }

    @Test
    void validateDespesaInvalid() {
        Despesa despesa = getDespesa();

        Despesa despesa2 = new Despesa();

        Categoria categoria2 = new Categoria();
        categoria2.setDescricao("Alimentação");
        categoria2.setId(7);

        despesa2.setId(2);
        despesa2.setCategoria(categoria2);
        despesa2.setDescricao("desc - 1");
        despesa2.setData(LocalDate.parse("2022-01-25"));
        despesa2.setValor(new Random().nextDouble() + 10);

        repository.save(despesa);


        when(repository.findAllByDescricaoAndData_YearAndData_Month(
                        despesa2.getDescricao(), despesa2.getData().getYear(), despesa2.getData().getMonth().getValue()
                )
        ).thenReturn(List.of(despesa));

        assertEquals(ResponseEntity.unprocessableEntity().body(despesa2), service.validateDespesa(despesa2));
    }

    @Test
    void validateDespesaValid() {
        Despesa despesa = getDespesa();

        Despesa despesa2 = new Despesa();

        Categoria categoria2 = new Categoria();
        categoria2.setDescricao("Alimentação");
        categoria2.setId(7);

        despesa2.setId(1);
        despesa2.setCategoria(categoria2);
        despesa2.setDescricao("desc - 1");
        despesa2.setData(LocalDate.parse("2022-02-22"));
        despesa2.setValor(new Random().nextDouble() + 10);

        repository.save(despesa);


        when(repository.findAllByDescricaoAndData_YearAndData_Month(
                        despesa2.getDescricao(), despesa2.getData().getYear(), despesa2.getData().getMonth().getValue()
                )
        ).thenReturn(Collections.emptyList());

        assertNull(service.validateDespesa(despesa2));

        when(repository.findAllByDescricaoAndData_YearAndData_Month(
                        despesa2.getDescricao(), despesa2.getData().getYear(), despesa2.getData().getMonth().getValue()
                )
        ).thenReturn(Collections.singletonList(despesa2));

        assertNull(service.validateDespesa(despesa2));
    }

    @Test
    void findByIdOrDefault() {

        Despesa despesa = getDespesa();

        Despesa despesa2 = new Despesa();

        Categoria categoria2 = new Categoria();
        categoria2.setDescricao("Alimentação");
        categoria2.setId(7);

        despesa2.setId(1);
        despesa2.setCategoria(categoria2);
        despesa2.setDescricao("desc - 1");
        despesa2.setData(LocalDate.parse("2022-02-22"));
        despesa2.setValor(new Random().nextDouble() + 10);

        when(repository.findById(despesa.getId())).thenReturn(Optional.of(despesa));
        assertEquals(despesa, service.findByIdOrDefault(despesa.getId(), despesa2));

        when(repository.findById(despesa.getId())).thenReturn(Optional.of(despesa2));
        assertEquals(despesa2, service.findByIdOrDefault(despesa.getId(), despesa2));

    }

    @Test
    void deleteDespesaResponse() {
        Despesa despesa = getDespesa();

        repository.save(despesa);

        when(repository.existsById(despesa.getId())).thenReturn(true);
        ResponseEntity<String> actualResponse = service.deleteDespesaResponse(despesa.getId());
        ResponseEntity<String> expectedResponse = ResponseEntity.ok(String.format("Despesa de ID %d deletada com sucesso!", despesa.getId()));
        assertEquals(expectedResponse, actualResponse);


        when(repository.existsById(despesa.getId())).thenReturn(false);
        actualResponse = service.deleteDespesaResponse(despesa.getId());
        expectedResponse = ResponseEntity.notFound().build();
        assertEquals(expectedResponse, actualResponse);


    }

    @Test
    void getDespesaResponseEntity() {
        Despesa despesa = getDespesa();
        repository.save(despesa);


        when(repository.findById(despesa.getId())).thenReturn(Optional.of(despesa));

        ResponseEntity<Despesa> actualResponse = service.getDespesaResponseEntity(despesa.getId());
        ResponseEntity<Despesa> expectedResponse = ResponseEntity.ok(despesa);
        assertEquals(expectedResponse, actualResponse);

        when(repository.findById(despesa.getId())).thenReturn(Optional.empty());

        actualResponse = service.getDespesaResponseEntity(despesa.getId());
        expectedResponse = ResponseEntity.notFound().build();
        assertEquals(expectedResponse, actualResponse);


    }

    @Test
    void findAll() {

        List<Despesa> despesas = getRandomDespesas(5);

        when(repository.findAll()).thenReturn(despesas);
        List<Despesa> actual = service.findAll();
        assertEquals(5, actual.size());
        verify(repository, times(1)).findAll();

    }

    @Test
    void createDespesaFromString() {

        Despesa despesa = getDespesa();
        Categoria categoria = despesa.getCategoria();

        when(repository.findAllByDescricaoAndData_YearAndData_Month(
                        despesa.getDescricao(), despesa.getData().getYear(), despesa.getData().getMonth().getValue()
                )
        ).thenReturn(Collections.emptyList());
        when(repository.save(despesa)).thenReturn(despesa);
        when(categoriaService.getOutras()).thenReturn(categoria);

        ResponseEntity<Despesa> actualResponse = service.createDespesa(despesa);
        ResponseEntity<Despesa> expectedResponse = ResponseEntity.ok(despesa);
        assertEquals(expectedResponse, actualResponse);

    }

    @Test
    void findAllByDescricao() {
        Despesa despesa = getDespesa();

        when(repository.findAllByDescricao(despesa.getDescricao())).thenReturn(Collections.singletonList(despesa));

        List<Despesa> allByDescricao = service.findAllByDescricao(despesa.getDescricao());

        assertEquals(1, allByDescricao.size());
        verify(repository, times(1)).findAllByDescricao(despesa.getDescricao());
    }

    @Test
    void findAllByYearAndMonth() {
        Despesa despesa = getDespesa();

        LocalDate date = despesa.getData();

        when(repository.findAllByData_YearAndData_Month(
                date.getYear(), (short) date.getMonth().getValue()
        )).thenReturn(Collections.singletonList(despesa));

        List<Despesa> allByDescricao = service
                .findAllByYearAndMonth(date.getYear(), (short) date.getMonth().getValue());

        assertEquals(1, allByDescricao.size());
        verify(repository, times(1))
                .findAllByData_YearAndData_Month(date.getYear(), (short) date.getMonth().getValue());
    }

    private Despesa getDespesa() {
        Despesa despesa = new Despesa();

        Categoria categoria = new Categoria();
        categoria.setDescricao("Outras");
        categoria.setId(7);

        despesa.setId(1);
        despesa.setCategoria(categoria);
        despesa.setDescricao("desc - 1");
        despesa.setData(LocalDate.parse("2022-01-22"));
        despesa.setValor(new Random().nextDouble() + 10);
        return despesa;
    }

    private List<Despesa> getRandomDespesas(int i) {

        List<Despesa> despesas = new ArrayList<>();

        for (; 0 < i; i--) {
            Despesa despesa = new Despesa();

            Categoria categoria = new Categoria();
            categoria.setDescricao("Outras");
            categoria.setId(7);

            int nextInt = new Random().nextInt();
            String date = String.format("%d-0%d-%d", new Random().nextInt(22) + 2000, new Random().nextInt(8) + 1, new Random().nextInt(18) + 10);
            despesa.setId(nextInt);
            despesa.setCategoria(categoria);
            despesa.setDescricao("desc - " + nextInt);
            despesa.setData(LocalDate.parse(date));
            despesa.setValor(new Random().nextDouble() + 10);

            despesas.add(despesa);
        }

        return despesas;
    }
}