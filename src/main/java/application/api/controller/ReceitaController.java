package application.api.controller;

import application.jpa.entities.Receita;
import application.jpa.service.CategoriaService;
import application.jpa.service.ReceitaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.internal.JsonFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController("receita")
public class ReceitaController {

    static private final Logger LOGGER = LoggerFactory.getLogger(ReceitaController.class);
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private ReceitaService receitaService;
    @Autowired
    private CategoriaService categoriaService;

    @PostMapping("/receitas")
    public ResponseEntity<?> createReceita(@RequestBody String receitaString) {
        try {
            Map<String, String> receitaMap = objectMapper.readerForMapOf(String.class)
                    .readValue(receitaString);
            Receita receita = createReceitaFromMap(receitaMap);
            return receitaService.createReceita(receita);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            return getProblemResponseEntity(receitaString, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }

    private ResponseEntity<Problem> getProblemResponseEntity(String receitaString, Exception e) {
        return ResponseEntity //
                .status(HttpStatus.INTERNAL_SERVER_ERROR) //
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE) //
                .body(Problem.create() //
                        .withTitle("Internal server error") //
                        .withDetail(String.format("Failed to fetch message %s Error message:  %s", JsonFormatter.prettyPrint(receitaString), e.getMessage())));
    }

    private Receita createReceitaFromMap(Map<String, String> receitaMap) {
        Receita novaReceita = new Receita();
        novaReceita.setValor(Double.valueOf(receitaMap.get("valor")));
        novaReceita.setId(Integer.valueOf(receitaMap.get("id")));
        novaReceita.setData(LocalDate.parse(receitaMap.get("data")));
        novaReceita.setDescricao(receitaMap.get("descricao"));
        novaReceita.setCategoria(
                categoriaService.getOnMap(receitaMap)
        );
        return novaReceita;
    }


    @GetMapping("/receitas/dummy")
    public ResponseEntity<List<String>> createDummies() {

        List<String> responses = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Receita novaReceita = new Receita();
            novaReceita.setId(i);
            novaReceita.setDescricao("desc - " + i);
            novaReceita.setValor(Math.pow(i, 3));
            novaReceita.setData(LocalDate.now());
            novaReceita.setCategoria(categoriaService.findById(new Random().nextInt(6) + 1));
            if (receitaService.createReceita(novaReceita).getStatusCode().is2xxSuccessful()) {
                responses.add(novaReceita.getDescricao() + " adicionado com sucesso");
            } else responses.add(novaReceita.getDescricao() + " não foi adicionado");

        }

        return ResponseEntity.ok(responses);
    }


    @GetMapping("/receitas")
    public ResponseEntity<List<Receita>> getReceitas(@RequestParam(defaultValue = "", required = false) String descricao) {
        return ResponseEntity.ok(receitaService.findAllByDescricao(descricao));
    }

    @GetMapping("/receitas/{year}/{month}")
    public ResponseEntity<List<Receita>> getReceitas(@PathVariable int year, @PathVariable short month) {
        return ResponseEntity.ok(receitaService.findAllByYearAndMonth(year, month));
    }

    @GetMapping("/receitas/{id}")
    public ResponseEntity<Receita> getReceita(@PathVariable Integer id) {
        return receitaService.getReceitaResponseEntity(id);
    }


    @PutMapping("/receitas/{id}")
    @Transactional
    public ResponseEntity<?> updateReceita(@PathVariable Integer id, @RequestBody String newReceitaString) {
        try {
            LOGGER.info("Start update receita with id {}", id);
            Map<String, String> receitaMap = objectMapper.readerForMapOf(String.class)
                    .readValue(newReceitaString);
            Receita newReceita = createReceitaFromMap(receitaMap);
            ResponseEntity<Receita> validateReceita = receitaService.validateReceita(newReceita);
            if (validateReceita != null) return validateReceita;
            Receita responseReceita = receitaService.findByIdOrDefault(id, newReceita);
            responseReceita.setId(id);
            responseReceita.setValor(newReceita.getValor());
            responseReceita.setDescricao(newReceita.getDescricao());
            responseReceita.setData(newReceita.getData());
            responseReceita.setCategoria(categoriaService.getOnMap(receitaMap));

            receitaService.updateFromReceita(responseReceita);

            LOGGER.info("Saved and flushed receita with id {}", id);
            return ResponseEntity.ok(responseReceita);
        } catch (RuntimeException | JsonProcessingException ex) {
            LOGGER.error("Failed to process receita with id {}", id, ex);
            return getProblemResponseEntity(newReceitaString, ex);
        }
    }

    @DeleteMapping("/receitas/{id}")
    public ResponseEntity<String> deleteReceita(@PathVariable Integer id) {
        return receitaService.deleteReceitaResponse(id);
    }
}
