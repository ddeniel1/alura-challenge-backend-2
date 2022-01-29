package application.jpa.service;

import application.jpa.entities.Despesa;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface DespesaService {
    void updateFromDespesa(Despesa despesa);

    ResponseEntity<Despesa> validateDespesa(Despesa novaDespesa);

    Despesa findByIdOrDefault(Integer id, Despesa newDespesa);

    ResponseEntity<String> deleteDespesaResponse(Integer id);

    ResponseEntity<Despesa> getDespesaResponseEntity(Integer id);

    List<Despesa> findAll();

    ResponseEntity<Despesa> createDespesaFromString(Despesa novaDespesa);

    List<Despesa> findAllByDescricao(String descricao);

    List<Despesa> findAllByYearAndMonth(int year, short month);
}
