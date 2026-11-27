
package com.example.patasebigodes.repository;

import com.example.patasebigodes.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    Optional<Funcionario> findByCpf(String cpf);

    Optional<Funcionario> findByEmail(String email);

    List<Funcionario> findByAtivoTrue();

    List<Funcionario> findByEspecialidades_IdAndAtivoTrue(Long especialidadeId);

}
