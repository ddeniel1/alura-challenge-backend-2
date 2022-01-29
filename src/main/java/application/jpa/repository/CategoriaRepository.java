package application.jpa.repository;

import application.jpa.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    @Query(
            value = "SELECT * FROM categoria WHERE descricao=?1",
            nativeQuery = true
    )
    Optional<Categoria> findByDescricao(String descricao);

    @Query(
            value = "SELECT MAX(id) FROM categoria",
            nativeQuery = true
    )
    Integer findMaxId();
}