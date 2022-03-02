package application.jpa.service;

import application.jpa.entities.Receita;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ReceitaService {
    void updateFromReceita(Receita deceita);

    ResponseEntity<Receita> validateReceita(Receita novaReceita);

    Receita findByIdOrDefault(Integer id, Receita newReceita);

    ResponseEntity<String> deleteReceitaResponse(Integer id);

    ResponseEntity<Receita> getReceitaResponseEntity(Integer id);

    List<Receita> findAll();

    ResponseEntity<Receita> createReceita(Receita novaReceita);

    List<Receita> findAllByDescricao(String descricao);

    List<Receita> findAllByYearAndMonth(int year, short month);
}
