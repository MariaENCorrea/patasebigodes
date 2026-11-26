
package com.example.patasebigodes.repository;

import com.example.patasebigodes.model.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    boolean existsByDataAndHorario(LocalDate data, LocalTime horario);
}
