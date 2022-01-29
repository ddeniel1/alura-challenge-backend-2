package application.jpa.repository;

import application.jpa.entities.Despesa;
import application.jpa.entities.Receita;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReceitaRepository extends CrudRepository<Receita, Integer> {

    @Override
    List<Receita> findAll();

    @Query(value = "SELECT * from receitas " +
            "where descricao=?1 " +
            "and EXTRACT(MONTH FROM data)=?2",
    nativeQuery = true)
    List<Receita> findAllByDescricaoAndData_Month(String descricao, int month);

    @Modifying
    @Query(value = "UPDATE receitas " +
            "SET id=?1, descricao=?2, valor=?3, data=?4, categoria=?5 " +
            "WHERE id = ?1",
            nativeQuery = true)
    void update(Integer id, String descricao, Double valor, LocalDate data, Integer idCategoria);

    List<Receita> findAllByDescricao(String descricao);

    @Query(value = "SELECT * from receitas " +
            "where EXTRACT(YEAR FROM data)=?1 " +
            "and EXTRACT(MONTH FROM data)=?2",
            nativeQuery = true)
    List<Despesa> findAllByData_YearAndData_Month(int year, short month);
}