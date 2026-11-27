
package com.example.patasebigodes.repository;

import com.example.patasebigodes.model.Especialidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EspecialidadeRepository extends JpaRepository<Especialidade, Long> {

    Optional<Especialidade> findByNomeIgnoreCase(String nome);

    List<Especialidade> findByAtivaTrue();

}
