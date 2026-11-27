
package com.example.patasebigodes.repository;

import com.example.patasebigodes.model.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    boolean existsByDataAndHorario(LocalDate data, LocalTime horario);

    boolean existsByFuncionario_IdAndDataAndHorario(
            Long funcionarioId,
            LocalDate data,
            LocalTime horario
    );

    boolean existsByFuncionario_IdAndDataAndHorarioAndIdNot(
            Long funcionarioId,
            LocalDate data,
            LocalTime horario,
            Long consultaId
    );

    boolean existsByAnimal_Id(Long animalId);

    List<Consulta> findByAnimal_IdOrderByDataAscHorarioAsc(Long animalId);

    List<Consulta> findByFuncionario_IdAndDataOrderByHorarioAsc(
            Long funcionarioId,
            LocalDate data
    );

}
