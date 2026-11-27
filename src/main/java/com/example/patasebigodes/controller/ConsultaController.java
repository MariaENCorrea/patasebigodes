
package com.example.patasebigodes.controller;

import com.example.patasebigodes.model.Animal;
import com.example.patasebigodes.model.Consulta;
import com.example.patasebigodes.repository.AnimalRepository;
import com.example.patasebigodes.repository.ConsultaRepository;
import com.example.patasebigodes.repository.EspecialidadeRepository;
import com.example.patasebigodes.service.AgendamentoService;
import com.example.patasebigodes.service.ConsultaPrazoService;
import com.example.patasebigodes.service.FuncionarioDisponibilidadeService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaRepository consultaRepository;
    private final AnimalRepository animalRepository;
    private final EspecialidadeRepository especialidadeRepository;
    private final AgendamentoService agendamentoService;
    private final ConsultaPrazoService prazoService;
    private final FuncionarioDisponibilidadeService disponibilidadeService;

    public ConsultaController(
            ConsultaRepository consultaRepository,
            AnimalRepository animalRepository,
            EspecialidadeRepository especialidadeRepository,
            AgendamentoService agendamentoService,
            ConsultaPrazoService prazoService,
            FuncionarioDisponibilidadeService disponibilidadeService) {

        this.consultaRepository = consultaRepository;
        this.animalRepository = animalRepository;
        this.especialidadeRepository = especialidadeRepository;
        this.agendamentoService = agendamentoService;
        this.prazoService = prazoService;
        this.disponibilidadeService = disponibilidadeService;
    }

    @GetMapping
    public String listar(HttpSession session, Model model) {

        Long tutorId = (Long) session.getAttribute("tutorId");

        if (tutorId == null) {
            return "redirect:/login";
        }

        List<Animal> animais =
                animalRepository.findByTutor_IdOrderByIdAsc(tutorId);

        List<Consulta> consultas = new ArrayList<>();

        for (Animal animal : animais) {
            consultas.addAll(
                    consultaRepository
                            .findByAnimal_IdOrderByDataAscHorarioAsc(animal.getId())
            );
        }

        consultas.sort(
                Comparator.comparing(
                        Consulta::getData,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ).thenComparing(
                        Consulta::getHorario,
                        Comparator.nullsLast(Comparator.naturalOrder())
                )
        );

        model.addAttribute("consultas", consultas);

        return "consultas/listar";
    }

    @GetMapping("/agendar")
    public String agendar(HttpSession session, Model model) {

        Long tutorId = (Long) session.getAttribute("tutorId");

        if (tutorId == null) {
            return "redirect:/login";
        }

        List<Animal> animais =
                animalRepository.findByTutor_IdOrderByIdAsc(tutorId);

        if (animais.isEmpty()) {
            return "redirect:/carteirinha";
        }

        prepararFormulario(model, animais, new Consulta());

        return "consultas/agendar";
    }

    @GetMapping("/horarios-disponiveis")
    @ResponseBody
    public ResponseEntity<List<LocalTime>> horariosDisponiveis(
            @RequestParam Long especialidadeId,
            @RequestParam LocalDate data,
            HttpSession session) {

        Long tutorId = (Long) session.getAttribute("tutorId");

        if (tutorId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<LocalTime> horarios =
                disponibilidadeService.listarHorariosDisponiveis(
                        especialidadeId,
                        data
                );

        return ResponseEntity.ok(horarios);
    }

    @PostMapping("/salvar")
    public String salvar(
            @RequestParam Long animalId,
            @RequestParam Long especialidadeId,
            @RequestParam String tipoConsulta,
            @RequestParam LocalDate data,
            @RequestParam LocalTime horario,
            @RequestParam(required = false) String informacoes,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        Long tutorId = (Long) session.getAttribute("tutorId");

        if (tutorId == null) {
            return "redirect:/login";
        }

        List<Animal> animais =
                animalRepository.findByTutor_IdOrderByIdAsc(tutorId);

        Animal animal = animalRepository
                .findByIdAndTutor_Id(animalId, tutorId)
                .orElse(null);

        if (animal == null) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Pet não encontrado ou não pertence à sua conta."
            );

            return "redirect:/consultas/agendar";
        }

        try {
            agendamentoService.agendar(
                    animal,
                    especialidadeId,
                    tipoConsulta,
                    data,
                    horario,
                    informacoes
            );

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Consulta agendada com sucesso!"
            );

            return "redirect:/carteirinha/" + animalId;

        } catch (IllegalArgumentException e) {

            Consulta agendamento = new Consulta();
            agendamento.setAnimal(animal);
            agendamento.setData(data);
            agendamento.setHorario(horario);
            agendamento.setConsulta(tipoConsulta);
            agendamento.setInformacoes(informacoes);

            prepararFormulario(model, animais, agendamento);

            model.addAttribute("erro", e.getMessage());

            return "consultas/agendar";
        }
    }

    @PostMapping("/excluir/{id}")
    public String excluir(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long tutorId = (Long) session.getAttribute("tutorId");

        if (tutorId == null) {
            return "redirect:/login";
        }

        Consulta consulta = consultaRepository.findById(id).orElse(null);

        if (consulta == null
                || consulta.getAnimal() == null
                || consulta.getAnimal().getTutor() == null
                || !consulta.getAnimal().getTutor().getId().equals(tutorId)) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Consulta não encontrada."
            );

            return "redirect:/consultas";
        }

        try {
            prazoService.validarCancelamento(consulta);

            Long animalId = consulta.getAnimal().getId();

            consultaRepository.delete(consulta);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Consulta cancelada com sucesso!"
            );

            return "redirect:/carteirinha/" + animalId;

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute("erro", e.getMessage());

            return "redirect:/consultas";
        }
    }

    private void prepararFormulario(
            Model model,
            List<Animal> animais,
            Consulta agendamento) {

        model.addAttribute("agendamento", agendamento);
        model.addAttribute("animais", animais);
        model.addAttribute(
                "especialidades",
                especialidadeRepository.findByAtivaTrue()
        );
        model.addAttribute("dataMinima", LocalDate.now());
    }
}
