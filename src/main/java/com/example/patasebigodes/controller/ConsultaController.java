
package com.example.patasebigodes.controller;

import com.example.patasebigodes.model.Animal;
import com.example.patasebigodes.model.Consulta;
import com.example.patasebigodes.repository.AnimalRepository;
import com.example.patasebigodes.repository.ConsultaRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaRepository consultaRepository;
    private final AnimalRepository animalRepository;

    public ConsultaController(
            ConsultaRepository consultaRepository,
            AnimalRepository animalRepository) {

        this.consultaRepository = consultaRepository;
        this.animalRepository = animalRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("consultas",
                consultaRepository.findAll());

        return "consultas/listar";
    }

    @GetMapping("/agendar")
    public String agendar(Model model) {
        model.addAttribute("consulta", new Consulta());
        model.addAttribute("animais",
                animalRepository.findAll());

        return "consultas/agendar";
    }

    @PostMapping("/salvar")
    public String salvar(
            @ModelAttribute Consulta consulta,
            @RequestParam("animalId") Long animalId,
            Model model) {

        if (consultaRepository.existsByDataAndHorario(
                consulta.getData(), consulta.getHorario())) {

            model.addAttribute("erro",
                    "Já existe uma consulta nesse dia e horário.");

            model.addAttribute("consulta", consulta);
            model.addAttribute("animais",
                    animalRepository.findAll());

            return "consultas/agendar";
        }

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Animal não encontrado"));

        consulta.setAnimal(animal);

        consultaRepository.save(consulta);

        return "redirect:/consultas";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        consultaRepository.deleteById(id);

        return "redirect:/consultas";
    }
}