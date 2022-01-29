package application.jpa.repository;

import application.jpa.entities.Despesa;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public interface DespesaRepository extends CrudRepository<Despesa, Integer> {

    @Override
    @NonNull
    List<Despesa> findAll();

    @Query(value = "SELECT * from despesas " +
            "WHERE descricao=?1 " +
            "AND EXTRACT(YEAR FROM data)=?2 " +
            "AND EXTRACT(MONTH FROM data)=?3",
            nativeQuery = true)
    List<Despesa> findAllByDescricaoAndData_YearAndData_Month(String descricao, int year, int month);

    @Modifying
    @Query(value = "UPDATE despesas " +
            "SET id=?1, descricao=?2, valor=?3, data=?4, categoria=?5 " +
            "WHERE id = ?1",
            nativeQuery = true)
    void update(Integer id, String descricao, Double valor, LocalDate data, Integer categoria);

    List<Despesa> findAllByDescricao(String descricao);

    @Query(value = "SELECT * from despesas " +
            "where EXTRACT(YEAR FROM data)=?1 " +
            "and EXTRACT(MONTH FROM data)=?2",
            nativeQuery = true)
    List<Despesa> findAllByData_YearAndData_Month(int year, short month);
}