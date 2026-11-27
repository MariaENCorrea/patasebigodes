
package com.example.patasebigodes.service;

import com.example.patasebigodes.model.Consulta;
import com.example.patasebigodes.model.DisponibilidadeFuncionario;
import com.example.patasebigodes.model.Funcionario;
import com.example.patasebigodes.repository.ConsultaRepository;
import com.example.patasebigodes.repository.DisponibilidadeFuncionarioRepository;
import com.example.patasebigodes.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

@Service
public class FuncionarioDisponibilidadeService {

    private static final long DURACAO_CONSULTA = 45;
    private static final long INTERVALO = 25;
    private static final long TEMPO_RESERVADO =
            DURACAO_CONSULTA + INTERVALO;

    private final FuncionarioRepository funcionarioRepository;
    private final DisponibilidadeFuncionarioRepository disponibilidadeRepository;
    private final ConsultaRepository consultaRepository;

    public FuncionarioDisponibilidadeService(
            FuncionarioRepository funcionarioRepository,
            DisponibilidadeFuncionarioRepository disponibilidadeRepository,
            ConsultaRepository consultaRepository) {

        this.funcionarioRepository = funcionarioRepository;
        this.disponibilidadeRepository = disponibilidadeRepository;
        this.consultaRepository = consultaRepository;
    }

    public Optional<Funcionario> encontrarFuncionarioDisponivel(
            Long especialidadeId,
            LocalDate data,
            LocalTime horario) {

        if (especialidadeId == null || data == null || horario == null) {
            return Optional.empty();
        }

        if (data.isBefore(LocalDate.now())) {
            return Optional.empty();
        }

        if (data.equals(LocalDate.now())
                && !horario.isAfter(LocalTime.now())) {
            return Optional.empty();
        }

        DayOfWeek diaSemana = data.getDayOfWeek();

        List<Funcionario> funcionarios =
                funcionarioRepository
                        .findDistinctByEspecialidades_IdAndAtivoTrue(
                                especialidadeId
                        );

        for (Funcionario funcionario : funcionarios) {

            List<DisponibilidadeFuncionario> disponibilidades =
                    disponibilidadeRepository
                            .findByFuncionario_IdAndDiaSemanaAndAtivoTrue(
                                    funcionario.getId(),
                                    diaSemana
                            );

            boolean dentroDoExpediente = disponibilidades.stream()
                    .anyMatch(disponibilidade -> {

                        LocalTime inicio = disponibilidade.getHorarioInicio();
                        LocalTime fim = disponibilidade.getHorarioFim();

                        return !horario.isBefore(inicio)
                                && !horario.plusMinutes(DURACAO_CONSULTA)
                                .isAfter(fim);
                    });

            if (!dentroDoExpediente) {
                continue;
            }

            List<Consulta> consultas =
                    consultaRepository
                            .findByFuncionario_IdAndDataOrderByHorarioAsc(
                                    funcionario.getId(),
                                    data
                            );

            boolean horarioOcupado = consultas.stream()
                    .filter(consulta -> consulta.getHorario() != null)
                    .anyMatch(consulta -> {

                        LocalTime inicioExistente = consulta.getHorario();

                        LocalTime fimExistente =
                                inicioExistente.plusMinutes(TEMPO_RESERVADO);

                        LocalTime fimNovo =
                                horario.plusMinutes(TEMPO_RESERVADO);

                        return horario.isBefore(fimExistente)
                                && fimNovo.isAfter(inicioExistente);
                    });

            if (!horarioOcupado) {
                return Optional.of(funcionario);
            }
        }

        return Optional.empty();
    }

    public List<LocalTime> listarHorariosDisponiveis(
            Long especialidadeId,
            LocalDate data) {

        Set<LocalTime> horariosDisponiveis = new TreeSet<>();

        if (especialidadeId == null || data == null
                || data.isBefore(LocalDate.now())) {
            return new ArrayList<>();
        }

        DayOfWeek diaSemana = data.getDayOfWeek();

        List<Funcionario> funcionarios =
                funcionarioRepository
                        .findDistinctByEspecialidades_IdAndAtivoTrue(
                                especialidadeId
                        );

        for (Funcionario funcionario : funcionarios) {

            List<DisponibilidadeFuncionario> disponibilidades =
                    disponibilidadeRepository
                            .findByFuncionario_IdAndDiaSemanaAndAtivoTrue(
                                    funcionario.getId(),
                                    diaSemana
                            );

            for (DisponibilidadeFuncionario disponibilidade : disponibilidades) {

                LocalTime horario = disponibilidade.getHorarioInicio();
                LocalTime fim = disponibilidade.getHorarioFim();

                while (!horario.plusMinutes(DURACAO_CONSULTA).isAfter(fim)) {

                    if (encontrarFuncionarioDisponivel(
                            especialidadeId,
                            data,
                            horario
                    ).isPresent()) {

                        horariosDisponiveis.add(horario);
                    }

                    horario = horario.plusMinutes(TEMPO_RESERVADO);
                }
            }
        }

        return new ArrayList<>(horariosDisponiveis);
    }
}
