
package com.example.patasebigodes.service;

import com.example.patasebigodes.model.Animal;
import com.example.patasebigodes.model.Consulta;
import com.example.patasebigodes.model.Especialidade;
import com.example.patasebigodes.model.Funcionario;

import com.example.patasebigodes.repository.ConsultaRepository;
import com.example.patasebigodes.repository.EspecialidadeRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class AgendamentoService {

    private final ConsultaRepository consultaRepository;
    private final EspecialidadeRepository especialidadeRepository;
    private final FuncionarioDisponibilidadeService disponibilidadeService;

    public AgendamentoService(
            ConsultaRepository consultaRepository,
            EspecialidadeRepository especialidadeRepository,
            FuncionarioDisponibilidadeService disponibilidadeService) {

        this.consultaRepository = consultaRepository;
        this.especialidadeRepository = especialidadeRepository;
        this.disponibilidadeService = disponibilidadeService;
    }

    @Transactional
    public Consulta agendar(
            Animal animal,
            Long especialidadeId,
            String tipoConsulta,
            LocalDate data,
            LocalTime horario,
            String informacoes) {

        if (animal == null) {
            throw new IllegalArgumentException("Selecione um animal.");
        }

        if (data == null || horario == null) {
            throw new IllegalArgumentException(
                    "Informe a data e o horário da consulta."
            );
        }

        if (data.isBefore(LocalDate.now())
                || (data.equals(LocalDate.now())
                && !horario.isAfter(LocalTime.now()))) {

            throw new IllegalArgumentException(
                    "Não é possível agendar uma consulta em um horário passado."
            );
        }

        if (tipoConsulta == null || tipoConsulta.isBlank()) {
            throw new IllegalArgumentException(
                    "Selecione o tipo de consulta."
            );
        }

        Especialidade especialidade = especialidadeRepository
                .findById(especialidadeId)
                .filter(Especialidade::isAtiva)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Especialidade não encontrada ou inativa."
                ));

        Funcionario funcionario = disponibilidadeService
                .encontrarFuncionarioDisponivel(
                        especialidadeId,
                        data,
                        horario
                )
                .orElseThrow(() -> new IllegalArgumentException(
                        "Não há funcionários disponíveis para essa especialidade e horário."
                ));

        Consulta consulta = new Consulta();

        consulta.setAnimal(animal);
        consulta.setFuncionario(funcionario);
        consulta.setEspecialidade(especialidade);
        consulta.setConsulta(tipoConsulta);
        consulta.setData(data);
        consulta.setHorario(horario);
        consulta.setInformacoes(informacoes);

        return consultaRepository.save(consulta);
    }
}
