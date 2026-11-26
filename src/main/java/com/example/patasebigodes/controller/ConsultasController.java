
package com.example.patasebigodes.controller;

import com.example.patasebigodes.model.Consulta;
import com.example.patasebigodes.repository.ConsultaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class ConsultasController {

    private final ConsultaRepository consultaRepository;

    public ConsultasController(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
    }

    @GetMapping("/consultas")
    public String consultas() {
        return "consultas";
    }

    @PostMapping("/consultas")
    public String agendarConsulta(
            @RequestParam String animal,
            @RequestParam String nomePet,
            @RequestParam(required = false) String raca,
            @RequestParam Integer idade,
            @RequestParam String nomeTutor,
            @RequestParam String celular,
            @RequestParam String email,
            @RequestParam(required = false) String informacoes,
            @RequestParam String consulta,
            @RequestParam String especialidade,
            @RequestParam LocalDate data,
            @RequestParam LocalTime horario,
            RedirectAttributes redirectAttributes) {

        LocalDate hoje = LocalDate.now();
        LocalTime agora = LocalTime.now();

        if (data.isBefore(hoje)) {
            redirectAttributes.addFlashAttribute(
                    "erro", "Não é possível agendar uma consulta em uma data passada.");

            return "redirect:/consultas";
        }

        if (idade < 0) {
            redirectAttributes.addFlashAttribute(
                    "erro", "Informe uma idade válida para o pet.");

            return "redirect:/consultas";
        }

        int diaSemana = data.getDayOfWeek().getValue();

        if (diaSemana == 7) {
            redirectAttributes.addFlashAttribute(
                    "erro", "A clínica não atende aos domingos.");

            return "redirect:/consultas";
        }

        LocalTime abertura = LocalTime.of(9, 0);
        LocalTime fechamento;

        if (diaSemana == 6) {
            fechamento = LocalTime.of(13, 0);
        } else {
            fechamento = LocalTime.of(20, 0);
        }

        if (horario.isBefore(abertura) || !horario.isBefore(fechamento)) {
            redirectAttributes.addFlashAttribute(
                    "erro", "O horário selecionado está fora do expediente.");

            return "redirect:/consultas";
        }

        if (data.equals(hoje) && !horario.isAfter(agora)) {
            redirectAttributes.addFlashAttribute(
                    "erro", "Escolha um horário que ainda não passou.");

            return "redirect:/consultas";
        }

        if (consultaRepository.existsByDataAndHorario(data, horario)) {
            redirectAttributes.addFlashAttribute(
                    "erro", "Esse horário já está reservado. Escolha outro.");

            return "redirect:/consultas";
        }

        Consulta novaConsulta = new Consulta();

        novaConsulta.setAnimal(animal);
        novaConsulta.setNomePet(nomePet.trim());
        novaConsulta.setRaca(raca);
        novaConsulta.setIdade(idade);
        novaConsulta.setNomeTutor(nomeTutor.trim());
        novaConsulta.setCelular(celular.trim());
        novaConsulta.setEmail(email.trim().toLowerCase());
        novaConsulta.setInformacoes(informacoes);
        novaConsulta.setConsulta(consulta);
        novaConsulta.setEspecialidade(especialidade);
        novaConsulta.setData(data);
        novaConsulta.setHorario(horario);

        consultaRepository.save(novaConsulta);

        redirectAttributes.addFlashAttribute(
                "mensagem", "Consulta agendada com sucesso!");

        redirectAttributes.addFlashAttribute(
                "consultaConfirmada", true);

        return "redirect:/consultas";
    }
}
