package application.repository;

import application.entities.Despesa;
import application.entities.Receita;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DespesaRepository extends JpaRepository<Despesa, Integer> {

    @Override
    List<Despesa> findAll();

    @Override
    <S extends Despesa> boolean exists(Example<S> example);

    @Override
    <S extends Despesa> S saveAndFlush(S entity);

    @Override
    <S extends Despesa> List<S> saveAllAndFlush(Iterable<S> entities);

    @Query(value = "SELECT * from despesas " +
            "where descricao=?1 " +
            "and EXTRACT(MONTH FROM data)=?2",
            nativeQuery = true)
    List<Despesa> findAllByDescricaoAndData_Month(String descricao, int month);
}