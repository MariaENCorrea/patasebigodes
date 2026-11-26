package com.example.patasebigodes.repository;

import com.example.patasebigodes.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TutorRepository extends JpaRepository<Tutor, Long> {

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);

    Optional<Tutor> findByEmail(String email);


    boolean existsByCpfAndIdNot(String cpf, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

}