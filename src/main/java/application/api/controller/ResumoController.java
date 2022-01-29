package application.api.controller;

import application.jpa.entities.Despesa;
import application.jpa.entities.Receita;
import application.jpa.service.DespesaService;
import application.jpa.service.ReceitaService;
import com.jayway.jsonpath.internal.JsonFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;

@RestController("resumo")
public class ResumoController {

    @Autowired
    ReceitaService receitaService;

    @Autowired
    DespesaService despesaService;

    @GetMapping("/resumo/{year}/{month}")
    public ResponseEntity<Object> getResumo(@PathVariable Integer year, @PathVariable Integer month) {

        List<Receita> receitas = receitaService.findAllByYearAndMonth(year, month.shortValue());
        List<Despesa> despesas = despesaService.findAllByYearAndMonth(year, month.shortValue());

        return ResponseEntity.ok(JsonFormatter.prettyPrint(Objects.toString(new Object() {

            final Double totalReceitas = receitas.stream().mapToDouble(Receita::getValor).sum();
            final Double totalDespesas = despesas.stream().mapToDouble(Despesa::getValor).sum();

            final Double saldoMensal = totalReceitas - totalDespesas;

            final Map<Integer, Double> despesasByCategoria = despesas.stream().collect(
                    groupingBy(
                            despesa -> despesa.getCategoria().getId(),
                            HashMap::new,
                            summingDouble(Despesa::getValor)
                    )
            );

            @Override
            public String toString() {
                return "{" +
                        "totalReceitas:" + totalReceitas +
                        ", totalDespesas:" + totalDespesas +
                        ", saldoMensal:" + saldoMensal +
                        ", despesasByCategoria:" + despesasByCategoria.toString().replaceAll("=", ":") +
                        "}";
            }
        })));
    }


}
