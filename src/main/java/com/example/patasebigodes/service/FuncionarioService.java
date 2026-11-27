
package com.example.patasebigodes.service;

import com.example.patasebigodes.model.Especialidade;
import com.example.patasebigodes.model.Funcionario;
import com.example.patasebigodes.repository.EspecialidadeRepository;
import com.example.patasebigodes.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final EspecialidadeRepository especialidadeRepository;

    public FuncionarioService(
            FuncionarioRepository funcionarioRepository,
            EspecialidadeRepository especialidadeRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.especialidadeRepository = especialidadeRepository;
    }

    public List<Funcionario> listarTodos() {
        return funcionarioRepository.findAll();
    }

    public Funcionario buscarPorId(Long id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Funcionário não encontrado."));
    }

    @Transactional
    public Funcionario salvar(
            Funcionario funcionario,
            List<Long> especialidadesIds) {

        String cpf = funcionario.getCpf().trim();
        String email = funcionario.getEmail().trim().toLowerCase();

        if (funcionario.getId() == null) {
            if (funcionarioRepository.existsByCpf(cpf)) {
                throw new IllegalArgumentException(
                        "Já existe um funcionário cadastrado com este CPF."
                );
            }

            if (funcionarioRepository.existsByEmail(email)) {
                throw new IllegalArgumentException(
                        "Já existe um funcionário cadastrado com este e-mail."
                );
            }
        } else {
            if (funcionarioRepository.existsByCpfAndIdNot(
                    cpf, funcionario.getId())) {
                throw new IllegalArgumentException(
                        "Este CPF já está cadastrado em outro funcionário."
                );
            }

            if (funcionarioRepository.existsByEmailAndIdNot(
                    email, funcionario.getId())) {
                throw new IllegalArgumentException(
                        "Este e-mail já está cadastrado em outro funcionário."
                );
            }
        }

        funcionario.setCpf(cpf);
        funcionario.setEmail(email);

        Set<Especialidade> especialidades = new HashSet<>();

        if (especialidadesIds != null) {
            for (Long especialidadeId : especialidadesIds) {
                Especialidade especialidade = especialidadeRepository
                        .findById(especialidadeId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Especialidade não encontrada."
                                ));

                if (!especialidade.isAtiva()) {
                    throw new IllegalArgumentException(
                            "Não é possível vincular uma especialidade inativa."
                    );
                }

                especialidades.add(especialidade);
            }
        }

        if (especialidades.isEmpty()) {
            throw new IllegalArgumentException(
                    "Selecione pelo menos uma especialidade."
            );
        }

        funcionario.setEspecialidades(especialidades);

        return funcionarioRepository.save(funcionario);
    }

    @Transactional
    public void alterarStatus(Long id) {
        Funcionario funcionario = buscarPorId(id);
        funcionario.setAtivo(!funcionario.isAtivo());
        funcionarioRepository.save(funcionario);
    }
}
