
package com.example.patasebigodes.repository;

import com.example.patasebigodes.model.Funcionario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    Optional<Funcionario> findByCpf(String cpf);

    @EntityGraph(attributePaths = "especialidades")
    Optional<Funcionario> findByEmail(String email);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);

    boolean existsByCpfAndIdNot(String cpf, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    @EntityGraph(attributePaths = "especialidades")
    List<Funcionario> findByAtivoTrue();

    @EntityGraph(attributePaths = "especialidades")
    List<Funcionario> findDistinctByEspecialidades_IdAndAtivoTrue(
            Long especialidadeId
    );

    @Override
    @EntityGraph(attributePaths = "especialidades")
    List<Funcionario> findAll();
}
