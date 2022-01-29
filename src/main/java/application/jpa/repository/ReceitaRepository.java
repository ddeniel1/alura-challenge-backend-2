package application.jpa.repository;

import application.jpa.entities.Receita;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;

import java.time.LocalDate;
import java.util.List;

public interface ReceitaRepository extends CrudRepository<Receita, Integer> {

    @Override
    @NonNull
    List<Receita> findAll();

    @Query(value = "SELECT * from receitas " +
            "WHERE descricao=?1 " +
            "AND EXTRACT(YEAR FROM data)=?2 " +
            "AND EXTRACT(MONTH FROM data)=?3",
            nativeQuery = true)
    List<Receita> findAllByDescricaoAndData_YearAndData_Month(String descricao, int year, int month);

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
    List<Receita> findAllByData_YearAndData_Month(int year, short month);
}