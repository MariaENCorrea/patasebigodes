
package com.example.patasebigodes.config;

import com.example.patasebigodes.model.DisponibilidadeFuncionario;
import com.example.patasebigodes.model.Especialidade;
import com.example.patasebigodes.model.Funcionario;
import com.example.patasebigodes.repository.DisponibilidadeFuncionarioRepository;
import com.example.patasebigodes.repository.EspecialidadeRepository;
import com.example.patasebigodes.repository.FuncionarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Configuration
public class InicializadorDados {

    @Bean
    public CommandLineRunner inicializarDados(
            EspecialidadeRepository especialidadeRepository,
            FuncionarioRepository funcionarioRepository,
            DisponibilidadeFuncionarioRepository disponibilidadeRepository) {

        return args -> {

            cadastrarEspecialidade(especialidadeRepository, "Clínica Geral",
                    "Consultas de rotina e avaliação geral.");

            cadastrarEspecialidade(especialidadeRepository, "Dermatologia",
                    "Cuidados com a pele e pelos.");

            cadastrarEspecialidade(especialidadeRepository, "Cardiologia",
                    "Avaliação e cuidados cardíacos.");

            cadastrarEspecialidade(especialidadeRepository, "Oftalmologia",
                    "Cuidados com a saúde ocular.");

            cadastrarEspecialidade(especialidadeRepository, "Odontologia Veterinária",
                    "Cuidados com a saúde bucal.");

            cadastrarEspecialidade(especialidadeRepository, "Ortopedia",
                    "Avaliação de ossos e articulações.");

            cadastrarEspecialidade(especialidadeRepository, "Nutrição",
                    "Avaliação e orientação nutricional.");

            cadastrarFuncionario(funcionarioRepository, disponibilidadeRepository,
                    especialidadeRepository, "Dr. Veterinário - Clínica Geral",
                    "DEMO-001", "11900000001",
                    "clinica.geral@patasebigodes.test", "Clínica Geral");

            cadastrarFuncionario(funcionarioRepository, disponibilidadeRepository,
                    especialidadeRepository, "Dr. Veterinário - Dermatologia",
                    "DEMO-002", "11900000002",
                    "dermatologia@patasebigodes.test", "Dermatologia");

            cadastrarFuncionario(funcionarioRepository, disponibilidadeRepository,
                    especialidadeRepository, "Dr. Veterinário - Cardiologia",
                    "DEMO-003", "11900000003",
                    "cardiologia@patasebigodes.test", "Cardiologia");

            cadastrarFuncionario(funcionarioRepository, disponibilidadeRepository,
                    especialidadeRepository, "Dr. Veterinário - Oftalmologia",
                    "DEMO-004", "11900000004",
                    "oftalmologia@patasebigodes.test", "Oftalmologia");

            cadastrarFuncionario(funcionarioRepository, disponibilidadeRepository,
                    especialidadeRepository, "Dr. Veterinário - Odontologia",
                    "DEMO-005", "11900000005",
                    "odontologia@patasebigodes.test", "Odontologia Veterinária");

            cadastrarFuncionario(funcionarioRepository, disponibilidadeRepository,
                    especialidadeRepository, "Dr. Veterinário - Ortopedia",
                    "DEMO-006", "11900000006",
                    "ortopedia@patasebigodes.test", "Ortopedia");

            cadastrarFuncionario(funcionarioRepository, disponibilidadeRepository,
                    especialidadeRepository, "Dr. Veterinário - Nutrição",
                    "DEMO-007", "11900000007",
                    "nutricao@patasebigodes.test", "Nutrição");
        };
    }

    private void cadastrarEspecialidade(
            EspecialidadeRepository repository,
            String nome,
            String descricao) {

        if (repository.findByNomeIgnoreCase(nome).isEmpty()) {
            Especialidade especialidade = new Especialidade();
            especialidade.setNome(nome);
            especialidade.setDescricao(descricao);
            especialidade.setAtiva(true);

            repository.save(especialidade);
        }
    }

    private void cadastrarFuncionario(
            FuncionarioRepository funcionarioRepository,
            DisponibilidadeFuncionarioRepository disponibilidadeRepository,
            EspecialidadeRepository especialidadeRepository,
            String nome,
            String cpf,
            String telefone,
            String email,
            String nomeEspecialidade) {

        Funcionario funcionario = funcionarioRepository
                .findByEmail(email)
                .orElseGet(() -> {
                    Funcionario novo = new Funcionario();
                    novo.setNome(nome);
                    novo.setCpf(cpf);
                    novo.setTelefone(telefone);
                    novo.setEmail(email);
                    novo.setAtivo(true);
                    return funcionarioRepository.save(novo);
                });

        Especialidade especialidade = especialidadeRepository
                .findByNomeIgnoreCase(nomeEspecialidade)
                .orElseThrow(() -> new IllegalStateException(
                        "Especialidade não encontrada: " + nomeEspecialidade));

        if (funcionario.getEspecialidades().stream()
                .noneMatch(e -> e.getId().equals(especialidade.getId()))) {

            funcionario.getEspecialidades().add(especialidade);
            funcionarioRepository.save(funcionario);
        }

        if (disponibilidadeRepository
                .findByFuncionario_IdAndAtivoTrue(funcionario.getId()).isEmpty()) {

            for (DayOfWeek dia : List.of(
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY)) {

                cadastrarDisponibilidade(
                        disponibilidadeRepository,
                        funcionario,
                        dia,
                        LocalTime.of(9, 0),
                        LocalTime.of(20, 0)
                );
            }

            cadastrarDisponibilidade(
                    disponibilidadeRepository,
                    funcionario,
                    DayOfWeek.SATURDAY,
                    LocalTime.of(9, 0),
                    LocalTime.of(13, 0)
            );
        }
    }

    private void cadastrarDisponibilidade(
            DisponibilidadeFuncionarioRepository repository,
            Funcionario funcionario,
            DayOfWeek dia,
            LocalTime inicio,
            LocalTime fim) {

        DisponibilidadeFuncionario disponibilidade =
                new DisponibilidadeFuncionario();

        disponibilidade.setFuncionario(funcionario);
        disponibilidade.setDiaSemana(dia);
        disponibilidade.setHorarioInicio(inicio);
        disponibilidade.setHorarioFim(fim);
        disponibilidade.setAtivo(true);

        repository.save(disponibilidade);
    }
}
