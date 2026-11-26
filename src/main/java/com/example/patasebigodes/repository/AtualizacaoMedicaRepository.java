
package com.example.patasebigodes.repository;

import com.example.patasebigodes.model.AtualizacaoMedica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AtualizacaoMedicaRepository
        extends JpaRepository<AtualizacaoMedica, Long> {

    List<AtualizacaoMedica> findByAnimal_IdOrderByDataAtendimentoDesc(
            Long animalId
    );
}
