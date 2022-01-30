package application.api.controller;

import application.jpa.entities.Despesa;
import application.jpa.service.CategoriaService;
import application.jpa.service.DespesaService;
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

@RestController("despesa")
public class DespesaController {

    static private final Logger LOGGER = LoggerFactory.getLogger(DespesaController.class);
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private DespesaService despesaService;
    @Autowired
    private CategoriaService categoriaService;

    @PostMapping("/despesas")
    public ResponseEntity<?> createDespesa(@RequestBody String despesaString) {
        try {
            Map<String, String> despesaMap = objectMapper.readerForMapOf(String.class)
                    .readValue(despesaString);
            Despesa despesa = createDespesaFromMap(despesaMap);
            return despesaService.createDespesa(despesa);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            return getProblemResponseEntity(despesaString, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }

    private ResponseEntity<Problem> getProblemResponseEntity(String despesaString, Exception e) {
        return ResponseEntity //
                .status(HttpStatus.INTERNAL_SERVER_ERROR) //
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE) //
                .body(Problem.create() //
                        .withTitle("Internal server error") //
                        .withDetail(String.format("Failed to fetch message %s Error message:  %s", JsonFormatter.prettyPrint(despesaString), e.getMessage())));
    }

    private Despesa createDespesaFromMap(Map<String, String> despesaMap) {
        Despesa novaDespesa = new Despesa();
        novaDespesa.setValor(Double.valueOf(despesaMap.get("valor")));
        novaDespesa.setId(Integer.valueOf(despesaMap.get("id")));
        novaDespesa.setData(LocalDate.parse(despesaMap.get("data")));
        novaDespesa.setDescricao(despesaMap.get("descricao"));
        novaDespesa.setCategoria(
                categoriaService.getOnMap(despesaMap)
        );
        return novaDespesa;
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
            novaDespesa.setCategoria(categoriaService.findById(new Random().nextInt(6) + 1));
            if (despesaService.createDespesa(novaDespesa).getStatusCode().is2xxSuccessful()) {
                responses.add(novaDespesa.getDescricao() + " adicionado com sucesso");
            } else responses.add(novaDespesa.getDescricao() + " não foi adicionado");

        }

        return ResponseEntity.ok(responses);
    }


    @GetMapping("/despesas")
    public ResponseEntity<List<Despesa>> getDespesas(@RequestParam(defaultValue = "", required = false) String descricao) {
        return ResponseEntity.ok(despesaService.findAllByDescricao(descricao));
    }

    @GetMapping("/despesas/{year}/{month}")
    public ResponseEntity<List<Despesa>> getDespesas(@PathVariable int year, @PathVariable short month) {
        return ResponseEntity.ok(despesaService.findAllByYearAndMonth(year, month));
    }

    @GetMapping("/despesas/{id}")
    public ResponseEntity<Despesa> getDespesa(@PathVariable Integer id) {
        return despesaService.getDespesaResponseEntity(id);
    }


    @PutMapping("/despesas/{id}")
    @Transactional
    public ResponseEntity<?> updateDespesa(@PathVariable Integer id, @RequestBody String newDespesaString) {
        try {
            LOGGER.info("Start update despesa with id {}", id);
            Map<String, String> despesaMap = objectMapper.readerForMapOf(String.class)
                    .readValue(newDespesaString);
            Despesa newDespesa = createDespesaFromMap(despesaMap);
            ResponseEntity<Despesa> validateDespesa = despesaService.validateDespesa(newDespesa);
            if (validateDespesa != null) return validateDespesa;
            Despesa responseDespesa = despesaService.findByIdOrDefault(id, newDespesa);
            responseDespesa.setId(id);
            responseDespesa.setValor(newDespesa.getValor());
            responseDespesa.setDescricao(newDespesa.getDescricao());
            responseDespesa.setData(newDespesa.getData());
            responseDespesa.setCategoria(categoriaService.getOnMap(despesaMap));

            despesaService.updateFromDespesa(responseDespesa);

            LOGGER.info("Saved and flushed despesa with id {}", id);
            return ResponseEntity.ok(responseDespesa);
        } catch (RuntimeException | JsonProcessingException ex) {
            LOGGER.error("Failed to process despesa with id {}", id, ex);
            return getProblemResponseEntity(newDespesaString, ex);
        }
    }

    @DeleteMapping("/despesas/{id}")
    public ResponseEntity<String> deleteDespesa(@PathVariable Integer id) {
        return despesaService.deleteDespesaResponse(id);
    }
}
