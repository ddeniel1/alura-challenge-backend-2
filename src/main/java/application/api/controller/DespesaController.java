package application.api.controller;

import application.entities.Despesa;
import application.repository.DespesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@RestController("despesa")
public class DespesaController {

    @Autowired
    private DespesaRepository repository;

    @PostMapping("/despesas")
    public ResponseEntity<Despesa> createDespesa(@RequestBody Despesa despesa) {
        ResponseEntity<Despesa> validateDespesa = validateDespesa(despesa);
        if (validateDespesa != null) return validateDespesa;

        return ResponseEntity.ok(repository.saveAndFlush(despesa));
    }

    private ResponseEntity<Despesa> validateDespesa(Despesa despesa) {
        Month month = despesa.getData().getMonth();
        String descricao = despesa.getDescricao();

        List<Despesa> despesaList = repository.findAllByDescricaoAndData_Month(descricao, month.getValue());
        if (despesaList.size() > 0)
            return ResponseEntity.unprocessableEntity().body(despesa);
        return null;
    }

    @GetMapping("/despesas/dummy")
    public ResponseEntity<List<String>> createDummies() {

        List<String> responses = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Despesa novaDespesa = new Despesa();
            novaDespesa.setId(i);
            novaDespesa.setDescricao("desc - " + i);
            novaDespesa.setValor(Math.pow(i, 3));
            novaDespesa.setData(LocalDate.now());
            if (createDespesa(novaDespesa).getStatusCode().is2xxSuccessful()) {
                responses.add(novaDespesa.getDescricao() + " adicionado com sucesso");
            } else responses.add(novaDespesa.getDescricao() + " não foi adicionado");
        }

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/despesas")
    public ResponseEntity<List<Despesa>> getDespesas() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/despesas/{id}")
    public ResponseEntity<Despesa> getDespesa(@PathVariable Integer id) {
        try {
            Despesa despesa = repository.findById(id).orElseThrow(RuntimeException::new);
            return ResponseEntity.ok(despesa);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/despesas/{id}")
    public ResponseEntity<Despesa> updateDespesa(@PathVariable Integer id, @RequestBody Despesa newDespesa) {
        try {
            ResponseEntity<Despesa> validateDespesa = validateDespesa(newDespesa);
            if (validateDespesa != null) return validateDespesa;

            Despesa responseDespesa = repository.findById(id).map(despesa -> {
                despesa.setValor(newDespesa.getValor());
                despesa.setDescricao(newDespesa.getDescricao());
                despesa.setData(newDespesa.getData());
                return repository.saveAndFlush(despesa);
            }).orElseGet(() -> {
                        newDespesa.setId(id);
                        return repository.save(newDespesa);
                    }
            );
            return ResponseEntity.ok(responseDespesa);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/despesas/{id}")
    public ResponseEntity<String> deleteDespesa(@PathVariable Integer id) {
        try {
            boolean exists = repository.existsById(id);
            if (exists) {
                repository.deleteById(id);
                return ResponseEntity.ok(String.format("Despesa de ID %d deletada com sucesso!", id));
            } else
                throw new RuntimeException();

        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

}
