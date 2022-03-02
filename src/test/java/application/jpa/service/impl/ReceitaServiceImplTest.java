package application.jpa.service.impl;

import application.jpa.entities.Categoria;
import application.jpa.entities.Receita;
import application.jpa.repository.ReceitaRepository;
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
class ReceitaServiceImplTest {

    @InjectMocks
    ReceitaServiceImpl service;

    @Mock
    ReceitaRepository repository;

    @Mock
    CategoriaServiceImpl categoriaService;

    @Test
    void updateFromReceita() {

        Receita receita = getReceita();

        repository.save(receita);

        receita.setValor(new Random().nextDouble() + 10);

        service.updateFromReceita(receita);

        verify(repository, times(1)).update(receita.getId(), receita.getDescricao(), receita.getValor(), receita.getData(), receita.getCategoria().getId());

    }

    @Test
    void validateReceitaInvalid() {
        Receita receita = getReceita();

        Receita receita2 = new Receita();

        Categoria categoria2 = new Categoria();
        categoria2.setDescricao("Alimentação");
        categoria2.setId(7);

        receita2.setId(2);
        receita2.setCategoria(categoria2);
        receita2.setDescricao("desc - 1");
        receita2.setData(LocalDate.parse("2022-01-25"));
        receita2.setValor(new Random().nextDouble() + 10);

        repository.save(receita);


        when(repository.findAllByDescricaoAndData_YearAndData_Month(
                        receita2.getDescricao(), receita2.getData().getYear(), receita2.getData().getMonth().getValue()
                )
        ).thenReturn(List.of(receita));

        assertEquals(ResponseEntity.unprocessableEntity().body(receita2), service.validateReceita(receita2));
    }

    @Test
    void validateReceitaValid() {
        Receita receita = getReceita();

        Receita receita2 = new Receita();

        Categoria categoria2 = new Categoria();
        categoria2.setDescricao("Alimentação");
        categoria2.setId(7);

        receita2.setId(1);
        receita2.setCategoria(categoria2);
        receita2.setDescricao("desc - 1");
        receita2.setData(LocalDate.parse("2022-02-22"));
        receita2.setValor(new Random().nextDouble() + 10);

        repository.save(receita);


        when(repository.findAllByDescricaoAndData_YearAndData_Month(
                        receita2.getDescricao(), receita2.getData().getYear(), receita2.getData().getMonth().getValue()
                )
        ).thenReturn(Collections.emptyList());

        assertNull(service.validateReceita(receita2));

        when(repository.findAllByDescricaoAndData_YearAndData_Month(
                        receita2.getDescricao(), receita2.getData().getYear(), receita2.getData().getMonth().getValue()
                )
        ).thenReturn(Collections.singletonList(receita2));

        assertNull(service.validateReceita(receita2));

    }

    @Test
    void findByIdOrDefault() {

        Receita receita = getReceita();

        Receita receita2 = new Receita();

        Categoria categoria2 = new Categoria();
        categoria2.setDescricao("Alimentação");
        categoria2.setId(7);

        receita2.setId(1);
        receita2.setCategoria(categoria2);
        receita2.setDescricao("desc - 1");
        receita2.setData(LocalDate.parse("2022-02-22"));
        receita2.setValor(new Random().nextDouble() + 10);

        when(repository.findById(receita.getId())).thenReturn(Optional.of(receita));
        assertEquals(receita, service.findByIdOrDefault(receita.getId(), receita2));

        when(repository.findById(receita.getId())).thenReturn(Optional.of(receita2));
        assertEquals(receita2, service.findByIdOrDefault(receita.getId(), receita2));

    }

    @Test
    void deleteReceitaResponse() {
        Receita receita = getReceita();

        repository.save(receita);

        when(repository.existsById(receita.getId())).thenReturn(true);
        ResponseEntity<String> actualResponse = service.deleteReceitaResponse(receita.getId());
        ResponseEntity<String> expectedResponse = ResponseEntity.ok(String.format("Receita de ID %d deletada com sucesso!", receita.getId()));
        assertEquals(expectedResponse, actualResponse);


        when(repository.existsById(receita.getId())).thenReturn(false);
        actualResponse = service.deleteReceitaResponse(receita.getId());
        expectedResponse = ResponseEntity.notFound().build();
        assertEquals(expectedResponse, actualResponse);


    }

    @Test
    void getReceitaResponseEntity() {
        Receita receita = getReceita();
        repository.save(receita);


        when(repository.findById(receita.getId())).thenReturn(Optional.of(receita));

        ResponseEntity<Receita> actualResponse = service.getReceitaResponseEntity(receita.getId());
        ResponseEntity<Receita> expectedResponse = ResponseEntity.ok(receita);
        assertEquals(expectedResponse, actualResponse);

        when(repository.findById(receita.getId())).thenReturn(Optional.empty());

        actualResponse = service.getReceitaResponseEntity(receita.getId());
        expectedResponse = ResponseEntity.notFound().build();
        assertEquals(expectedResponse, actualResponse);


    }

    @Test
    void findAll() {

        List<Receita> receitas = getRandomReceitas(5);

        when(repository.findAll()).thenReturn(receitas);
        List<Receita> actual = service.findAll();
        assertEquals(5, actual.size());
        verify(repository, times(1)).findAll();

    }

    @Test
    void createReceitaFromString() {

        Receita receita = getReceita();
        Categoria categoria = receita.getCategoria();

        when(repository.findAllByDescricaoAndData_YearAndData_Month(
                        receita.getDescricao(), receita.getData().getYear(), receita.getData().getMonth().getValue()
                )
        ).thenReturn(Collections.emptyList());
        when(repository.save(receita)).thenReturn(receita);
        when(categoriaService.getOutras()).thenReturn(categoria);

        ResponseEntity<Receita> actualResponse = service.createReceita(receita);
        ResponseEntity<Receita> expectedResponse = ResponseEntity.ok(receita);
        assertEquals(expectedResponse, actualResponse);

    }

    @Test
    void findAllByDescricao() {
        Receita receita = getReceita();

        when(repository.findAllByDescricao(receita.getDescricao())).thenReturn(Collections.singletonList(receita));

        List<Receita> allByDescricao = service.findAllByDescricao(receita.getDescricao());

        assertEquals(1, allByDescricao.size());
        verify(repository, times(1)).findAllByDescricao(receita.getDescricao());
    }

    @Test
    void findAllByYearAndMonth() {
        Receita receita = getReceita();

        LocalDate date = receita.getData();

        when(repository.findAllByData_YearAndData_Month(
                date.getYear(), (short) date.getMonth().getValue()
        )).thenReturn(Collections.singletonList(receita));

        List<Receita> allByDescricao = service
                .findAllByYearAndMonth(date.getYear(), (short) date.getMonth().getValue());

        assertEquals(1, allByDescricao.size());
        verify(repository, times(1))
                .findAllByData_YearAndData_Month(date.getYear(), (short) date.getMonth().getValue());
    }

    private Receita getReceita() {
        Receita receita = new Receita();

        Categoria categoria = new Categoria();
        categoria.setDescricao("Outras");
        categoria.setId(7);

        receita.setId(1);
        receita.setCategoria(categoria);
        receita.setDescricao("desc - 1");
        receita.setData(LocalDate.parse("2022-01-22"));
        receita.setValor(new Random().nextDouble() + 10);
        return receita;
    }

    private List<Receita> getRandomReceitas(int i) {

        List<Receita> receitas = new ArrayList<>();

        for (; 0 < i; i--) {
            Receita receita = new Receita();

            Categoria categoria = new Categoria();
            categoria.setDescricao("Outras");
            categoria.setId(7);

            int nextInt = new Random().nextInt();
            String date = String.format("%d-0%d-%d", new Random().nextInt(22) + 2000, new Random().nextInt(8) + 1, new Random().nextInt(18) + 10);
            receita.setId(nextInt);
            receita.setCategoria(categoria);
            receita.setDescricao("desc - " + nextInt);
            receita.setData(LocalDate.parse(date));
            receita.setValor(new Random().nextDouble() + 10);

            receitas.add(receita);
        }

        return receitas;
    }
}