package application.jpa.service.impl;

import application.jpa.entities.Categoria;
import application.jpa.entities.Receita;
import application.jpa.repository.ReceitaRepository;
import application.jpa.service.CategoriaService;
import application.jpa.service.ReceitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReceitaServiceImpl implements ReceitaService {
    @Autowired
    private ReceitaRepository repository;

    @Autowired
    private CategoriaService categoriaService;

    @Override
    public void updateFromReceita(Receita receita) {
        repository.update(
                receita.getId(),
                receita.getDescricao(),
                receita.getValor(),
                receita.getData(),
                receita.getCategoria().getId()
        );
    }

    public ResponseEntity<Receita> validateReceita(Receita receita) {
        LocalDate localDate = receita.getData();
        String descricao = receita.getDescricao();

        List<Receita> receitaList = repository.findAllByDescricaoAndData_YearAndData_Month(descricao, localDate.getYear(), localDate.getMonth().getValue());
        if (receitaList.size() > 0) {
            if (receitaList.size() == 1 && receita.getId().equals(receitaList.get(0).getId()))
                return null;
            return ResponseEntity.unprocessableEntity().body(receita);
        }
        return null;
    }

    @Override
    public Receita findByIdOrDefault(Integer id, Receita newReceita) {
        return repository.findById(id).orElse(newReceita);
    }

    @Override
    public ResponseEntity<String> deleteReceitaResponse(Integer id) {
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

    @Override
    public ResponseEntity<Receita> getReceitaResponseEntity(Integer id) {
        try {
            Receita receita = repository.findById(id).orElseThrow(RuntimeException::new);
            return ResponseEntity.ok(receita);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public List<Receita> findAll() {
        return repository.findAll();
    }

    @Override
    public ResponseEntity<Receita> createReceita(Receita novaReceita) {
        ResponseEntity<Receita> validateReceita = validateReceita(novaReceita);
        if (validateReceita != null) return validateReceita;
        novaReceita.setCategoria(validateCategory(novaReceita.getCategoria()));
        return ResponseEntity.ok(repository.save(novaReceita));
    }

    @Override
    public List<Receita> findAllByDescricao(String descricao) {
        if (descricao.equals("")) return findAll();
        return repository.findAllByDescricao(descricao);
    }

    @Override
    public List<Receita> findAllByYearAndMonth(int year, short month) {
        return repository.findAllByData_YearAndData_Month(year, month);
    }

    private Categoria validateCategory(Categoria categoria) {
        return Optional.ofNullable(categoria).orElse(categoriaService.getOutras());
    }
}
