package application.jpa.service;

import application.jpa.entities.Categoria;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public interface CategoriaService {
    Categoria getCategoriaFromDescricao(String descricao);

    Categoria getOnMap(Map<String, String> despesaMap);

    Categoria getOutras();

    Categoria findById(int i);
}
