
package com.example.patasebigodes.repository;

import com.example.patasebigodes.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TutorRepository extends JpaRepository<Tutor, Long> {

    Optional<Tutor> findByEmail(String email);

    Optional<Tutor> findByCpf(String cpf);
}
