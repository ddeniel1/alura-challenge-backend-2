package application.repository;

import application.entities.Receita;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Month;
import java.util.List;
import java.util.Optional;

public interface ReceitaRepository extends JpaRepository<Receita, Integer> {

    @Override
    List<Receita> findAll();

    @Override
    <S extends Receita> boolean exists(Example<S> example);

    @Override
    <S extends Receita> S saveAndFlush(S entity);

    @Override
    <S extends Receita> List<S> saveAllAndFlush(Iterable<S> entities);

    @Query(value = "SELECT * from receitas " +
            "where descricao=?1 " +
            "and EXTRACT(MONTH FROM data)=?2",
    nativeQuery = true)
    List<Receita> findAllByDescricaoAndData_Month(String descricao, int month);
}