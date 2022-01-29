package application.jpa.service.impl;

import application.jpa.entities.Categoria;
import application.jpa.entities.Despesa;
import application.jpa.repository.DespesaRepository;
import application.jpa.service.CategoriaService;
import application.jpa.service.DespesaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DespesaServiceImpl implements DespesaService {
    @Autowired
    private DespesaRepository repository;

    @Autowired
    private CategoriaService categoriaService;

    @Override
    public void updateFromDespesa(Despesa despesa) {
        repository.update(
                despesa.getId(),
                despesa.getDescricao(),
                despesa.getValor(),
                despesa.getData(),
                despesa.getCategoria().getId()
        );
    }

    public ResponseEntity<Despesa> validateDespesa(Despesa despesa) {
        LocalDate localDate = despesa.getData();
        String descricao = despesa.getDescricao();

        List<Despesa> despesaList = repository.findAllByDescricaoAndData_YearAndData_Month(descricao, localDate.getYear(), localDate.getMonth().getValue());
        if (despesaList.size() > 0) {
            if (despesaList.size() == 1 && despesa.getId().equals(despesaList.get(0).getId()))
                return null;
            return ResponseEntity.unprocessableEntity().body(despesa);
        }
        return null;
    }

    @Override
    public Despesa findByIdOrDefault(Integer id, Despesa newDespesa) {
        return repository.findById(id).orElse(newDespesa);
    }

    @Override
    public ResponseEntity<String> deleteDespesaResponse(Integer id) {
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

    @Override
    public ResponseEntity<Despesa> getDespesaResponseEntity(Integer id) {
        try {
            Despesa despesa = repository.findById(id).orElseThrow(RuntimeException::new);
            return ResponseEntity.ok(despesa);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public List<Despesa> findAll() {
        return repository.findAll();
    }

    @Override
    public ResponseEntity<Despesa> createDespesaFromString(Despesa novaDespesa) {
        ResponseEntity<Despesa> validateDespesa = validateDespesa(novaDespesa);
        if (validateDespesa != null) return validateDespesa;
        novaDespesa.setCategoria(validateCategory(novaDespesa.getCategoria()));
        return ResponseEntity.ok(repository.save(novaDespesa));
    }

    @Override
    public List<Despesa> findAllByDescricao(String descricao) {
        if (descricao.equals("")) return findAll();
        return repository.findAllByDescricao(descricao);
    }

    @Override
    public List<Despesa> findAllByYearAndMonth(int year, short month) {
        return repository.findAllByData_YearAndData_Month(year, month);
    }

    private Categoria validateCategory(Categoria categoria) {
        return Optional.ofNullable(categoria).orElse(categoriaService.getOutras());
    }
}
