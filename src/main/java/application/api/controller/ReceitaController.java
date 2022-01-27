package application.api.controller;

import application.entities.Receita;
import application.repository.ReceitaRepository;
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

@RestController("receita")
public class ReceitaController {

    @Autowired
    private ReceitaRepository repository;

    @PostMapping("/receitas")
    public ResponseEntity<Receita> createReceita(@RequestBody Receita receita) {
        ResponseEntity<Receita> validateReceita = validateReceita(receita);
        if (validateReceita != null) return validateReceita;

        return ResponseEntity.ok(repository.saveAndFlush(receita));
    }

    private ResponseEntity<Receita> validateReceita(Receita receita) {
        Month month = receita.getData().getMonth();
        String descricao = receita.getDescricao();

        List<Receita> receitaList = repository.findAllByDescricaoAndData_Month(descricao, month.getValue());
        if (receitaList.size() > 0)
            return ResponseEntity.unprocessableEntity().body(receita);
        return null;
    }

    @GetMapping("/receitas/dummy")
    public ResponseEntity<List<String>> createDummies() {

        List<String> responses = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Receita novaReceita = new Receita();
            novaReceita.setId(i);
            novaReceita.setDescricao("desc - " + i);
            novaReceita.setValor(Math.pow(i, 2));
            novaReceita.setData(LocalDate.now());
            if (createReceita(novaReceita).getStatusCode().is2xxSuccessful()) {
                responses.add(novaReceita.getDescricao() + " adicionado com sucesso");
            } else responses.add(novaReceita.getDescricao() + " não foi adicionado");
        }

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/receitas")
    public ResponseEntity<List<Receita>> getReceitas() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/receitas/{id}")
    public ResponseEntity<Receita> getReceita(@PathVariable Integer id) {
        try {
            Receita receita = repository.findById(id).orElseThrow(RuntimeException::new);
            return ResponseEntity.ok(receita);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/receitas/{id}")
    public ResponseEntity<Receita> getReceita(@PathVariable Integer id, @RequestBody Receita newReceita) {
        try {
            ResponseEntity<Receita> validateReceita = validateReceita(newReceita);
            if (validateReceita != null) return validateReceita;

            Receita responseReceita = repository.findById(id).map(receita -> {
                receita.setValor(newReceita.getValor());
                receita.setDescricao(newReceita.getDescricao());
                receita.setData(newReceita.getData());
                return repository.saveAndFlush(receita);
            }).orElseGet(() -> {
                        newReceita.setId(id);
                        return repository.save(newReceita);
                    }
            );
            return ResponseEntity.ok(responseReceita);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/receitas/{id}")
    public ResponseEntity<String> deleteReceita(@PathVariable Integer id) {
        try {
            boolean exists = repository.existsById(id);
            if (exists) {
                repository.deleteById(id);
                return ResponseEntity.ok(String.format("Receita de ID %d deletada com sucesso!", id));
            } else
                throw new RuntimeException();

        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

}
