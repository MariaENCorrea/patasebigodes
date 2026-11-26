
package com.example.patasebigodes.repository;

import com.example.patasebigodes.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnimalRepository extends JpaRepository<Animal, Long> {

    List<Animal> findByTutor_IdOrderByIdAsc(Long tutorId);

    Optional<Animal> findByIdAndTutor_Id(Long animalId, Long tutorId);
}
