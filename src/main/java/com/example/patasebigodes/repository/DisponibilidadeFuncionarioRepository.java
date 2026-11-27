
package com.example.patasebigodes.repository;

import com.example.patasebigodes.model.DisponibilidadeFuncionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public interface DisponibilidadeFuncionarioRepository
        extends JpaRepository<DisponibilidadeFuncionario, Long> {

    List<DisponibilidadeFuncionario> findByDiaSemanaAndAtivoTrue(
            DayOfWeek diaSemana
    );

    List<DisponibilidadeFuncionario> findByFuncionario_IdAndAtivoTrue(
            Long funcionarioId
    );

    List<DisponibilidadeFuncionario> findByFuncionario_IdAndDiaSemanaAndAtivoTrue(
            Long funcionarioId,
            DayOfWeek diaSemana
    );

}
