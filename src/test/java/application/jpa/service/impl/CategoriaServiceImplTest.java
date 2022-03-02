package application.jpa.service.impl;

import application.jpa.entities.Categoria;
import application.jpa.repository.CategoriaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class CategoriaServiceImplTest {

    @InjectMocks
    CategoriaServiceImpl service;

    @Mock
    CategoriaRepository repository;

    private final List<Categoria> categorias = new ArrayList<>();

    @BeforeEach
    void setUp() {
        Map<Integer,String> nomes = new HashMap<>();

        nomes.put(1,"Alimentação");
        nomes.put(2,"Saúde");
        nomes.put(3,"Moradia");
        nomes.put(4,"Transporte");
        nomes.put(5,"Lazer");
        nomes.put(6,"Imprevistos");
        nomes.put(7,"Outras");



        for (int i = 0; i < 7; i++) {
            Categoria newCategoria = new Categoria();
            newCategoria.setId(i+1);
            newCategoria.setDescricao(nomes.get(i+1));
            categorias.add(newCategoria);
        }

    }

    @AfterEach
    void tearDown() {
        categorias.clear();
        repository.deleteAll();
    }

    @Test
    void getCategoriaFromDescricao() {
        Map<Integer,String> expectedDescricao = new HashMap<>();
        expectedDescricao.put(1,"Alimentação");
        expectedDescricao.put(2,"Saúde");
        expectedDescricao.put(3,"Moradia");
        expectedDescricao.put(4,"Transporte");
        expectedDescricao.put(5,"Lazer");
        expectedDescricao.put(6,"Imprevistos");
        expectedDescricao.put(7,"Outras");

        expectedDescricao.forEach((id, descricao) -> {
            when(repository.findByDescricao(descricao)).thenReturn(Optional.ofNullable(categorias.get(id-1)));
            Categoria actual = service.getCategoriaFromDescricao(descricao);
            assertEquals(descricao, actual.getDescricao());
            assertEquals(id, actual.getId());
            verify(repository,times(1)).findByDescricao(descricao);
        });

    }

    @Test
    void getOnMap() {
        Map<String,String> expectedDescricao = new HashMap<>();
        expectedDescricao.put("categoria","Alimentação");
        when(repository.findByDescricao(expectedDescricao.get("categoria")))
                .thenReturn(Optional.ofNullable(categorias.get(0)));
        assertEquals(service.getOnMap(expectedDescricao),categorias.get(0));

        expectedDescricao.replace("categoria","Saúde");
        when(repository.findByDescricao(expectedDescricao.get("categoria")))
                .thenReturn(Optional.ofNullable(categorias.get(1)));
        assertEquals(service.getOnMap(expectedDescricao),categorias.get(1));

        expectedDescricao.replace("categoria","Moradia");
        when(repository.findByDescricao(expectedDescricao.get("categoria")))
                .thenReturn(Optional.ofNullable(categorias.get(2)));
        assertEquals(service.getOnMap(expectedDescricao),categorias.get(2));

        expectedDescricao.replace("categoria","Transporte");
        when(repository.findByDescricao(expectedDescricao.get("categoria")))
                .thenReturn(Optional.ofNullable(categorias.get(3)));
        assertEquals(service.getOnMap(expectedDescricao),categorias.get(3));

        expectedDescricao.replace("categoria","Lazer");
        when(repository.findByDescricao(expectedDescricao.get("categoria")))
                .thenReturn(Optional.ofNullable(categorias.get(4)));
        assertEquals(service.getOnMap(expectedDescricao),categorias.get(4));

        expectedDescricao.replace("categoria","Imprevistos");
        when(repository.findByDescricao(expectedDescricao.get("categoria")))
                .thenReturn(Optional.ofNullable(categorias.get(5)));
        assertEquals(service.getOnMap(expectedDescricao),categorias.get(5));

        expectedDescricao.replace("categoria","Outras");
        when(repository.findByDescricao(expectedDescricao.get("categoria")))
                .thenReturn(Optional.ofNullable(categorias.get(6)));
        assertEquals(service.getOnMap(expectedDescricao),categorias.get(6));

        verify(repository, times(7)).findByDescricao(any());
    }

    @Test
    void getOutras() {
        when(repository.findByDescricao("Outras"))
                .thenReturn(Optional.ofNullable(categorias.get(6)));
        Categoria actual = service.getOutras();
        assertEquals(7,actual.getId());
        assertEquals("Outras",actual.getDescricao());
        verify(repository, times(1)).findByDescricao("Outras");
    }

    @Test
    void findById() {
        Map<Integer,String> expectedDescricao = new HashMap<>();
        expectedDescricao.put(1,"Alimentação");
        expectedDescricao.put(2,"Saúde");
        expectedDescricao.put(3,"Moradia");
        expectedDescricao.put(4,"Transporte");
        expectedDescricao.put(5,"Lazer");
        expectedDescricao.put(6,"Imprevistos");
        expectedDescricao.put(7,"Outras");


        expectedDescricao.forEach((id, descricao) -> {
            when(repository.findById(id)).thenReturn(Optional.ofNullable(categorias.get(id-1)));
            Categoria actual = service.findById(id);
            assertEquals(id, actual.getId());
            assertEquals(descricao, actual.getDescricao());
            verify(repository, times(1)).findById(id);
        });
    }
}