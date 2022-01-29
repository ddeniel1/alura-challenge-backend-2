package application.jpa.service.impl;

import application.jpa.entities.Categoria;
import application.jpa.repository.CategoriaRepository;
import application.jpa.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public Categoria getCategoriaFromDescricao(String descricao) {
        return categoriaRepository.findByDescricao(
                Optional.ofNullable(descricao).orElse(("Outras"))
        ).orElseThrow(RuntimeException::new);
    }

    @Override
    public Categoria getOnMap(Map<String, String> despesaMap) {
        return categoriaRepository.findByDescricao(despesaMap.getOrDefault("categoria", "Outras"))
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public Categoria getOutras() {
        return categoriaRepository.findByDescricao("Outras").get();
    }

    @Override
    public Categoria findById(int i) {
        return categoriaRepository.findById(new Random().nextInt(6) + 1).get();
    }

}
