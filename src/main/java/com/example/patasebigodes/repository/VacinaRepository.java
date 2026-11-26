
package com.example.patasebigodes.repository;

import com.example.patasebigodes.model.Vacina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VacinaRepository extends JpaRepository<Vacina, Long> {

    List<Vacina> findByAnimal_IdOrderByDataAplicacaoDesc(Long animalId);
}
