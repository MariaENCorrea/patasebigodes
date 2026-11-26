
package com.example.patasebigodes.controller;

import com.example.patasebigodes.model.Animal;
import com.example.patasebigodes.model.Tutor;
import com.example.patasebigodes.repository.AnimalRepository;
import com.example.patasebigodes.repository.AtualizacaoMedicaRepository;
import com.example.patasebigodes.repository.TutorRepository;
import com.example.patasebigodes.repository.VacinaRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
public class CarteirinhaController {

    private final AnimalRepository animalRepository;
    private final TutorRepository tutorRepository;
    private final AtualizacaoMedicaRepository atualizacaoRepository;
    private final VacinaRepository vacinaRepository;

    public CarteirinhaController(
            AnimalRepository animalRepository,
            TutorRepository tutorRepository,
            AtualizacaoMedicaRepository atualizacaoRepository,
            VacinaRepository vacinaRepository) {

        this.animalRepository = animalRepository;
        this.tutorRepository = tutorRepository;
        this.atualizacaoRepository = atualizacaoRepository;
        this.vacinaRepository = vacinaRepository;
    }

    @GetMapping("/carteirinha")
    public String carteirinha(HttpSession session,
                              RedirectAttributes redirectAttributes) {

        Long tutorId = obterTutorId(session);

        if (tutorId == null) {
            return "redirect:/login";
        }

        List<Animal> animais =
                animalRepository.findByTutor_IdOrderByIdAsc(tutorId);

        if (animais.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Você ainda não possui um pet cadastrado."
            );

            return "redirect:/cadastro";
        }

        return "redirect:/carteirinha/" + animais.get(0).getId();
    }

    @GetMapping("/carteirinha/{id}")
    public String visualizarCarteirinha(
            @PathVariable Long id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        Long tutorId = obterTutorId(session);

        if (tutorId == null) {
            return "redirect:/login";
        }

        Animal animal = animalRepository
                .findByIdAndTutor_Id(id, tutorId)
                .orElse(null);

        if (animal == null) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Pet não encontrado."
            );

            return "redirect:/carteirinha";
        }

        carregarCarteirinha(animal, model);

        return "carteirinha";
    }

    @PostMapping("/carteirinha/{id}/atualizar")
    public String atualizarCarteirinha(
            @PathVariable Long id,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String dataNascimento,
            @RequestParam(required = false) String sexo,
            @RequestParam(required = false) String raca,
            @RequestParam(required = false) String cor,
            @RequestParam(required = false) String nomeTutor,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String celular,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) String bairro,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long tutorId = obterTutorId(session);

        if (tutorId == null) {
            return "redirect:/login";
        }

        Animal animal = animalRepository
                .findByIdAndTutor_Id(id, tutorId)
                .orElse(null);

        if (animal == null) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Pet não encontrado."
            );

            return "redirect:/carteirinha";
        }

        Tutor tutor = animal.getTutor();

        if (tutor == null) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Não foi possível localizar os dados do tutor."
            );

            return "redirect:/carteirinha/" + id;
        }

        if (nome == null || nome.trim().length() < 2) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Informe o nome do pet."
            );

            return "redirect:/carteirinha/" + id;
        }

        if (nomeTutor == null || nomeTutor.trim().length() < 3) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Informe o nome completo do tutor."
            );

            return "redirect:/carteirinha/" + id;
        }

        if (cpf != null && !cpf.trim().isEmpty()) {

            cpf = cpf.trim();

            if (tutorRepository.existsByCpfAndIdNot(cpf, tutor.getId())) {
                redirectAttributes.addFlashAttribute(
                        "erro",
                        "Este CPF já está cadastrado."
                );

                return "redirect:/carteirinha/" + id;
            }

            tutor.setCpf(cpf);
        }

        if (email != null && !email.trim().isEmpty()) {

            email = email.trim().toLowerCase();

            if (tutorRepository.existsByEmailAndIdNot(
                    email,
                    tutor.getId())) {

                redirectAttributes.addFlashAttribute(
                        "erro",
                        "Este e-mail já está cadastrado."
                );

                return "redirect:/carteirinha/" + id;
            }

            tutor.setEmail(email);
        }

        animal.setNome(nome.trim());
        animal.setSexo(sexo);
        animal.setRaca(raca);
        animal.setCor(cor);

        if (dataNascimento != null && !dataNascimento.isBlank()) {

            try {
                animal.setDataNascimento(
                        LocalDate.parse(dataNascimento)
                );
            } catch (Exception e) {

                redirectAttributes.addFlashAttribute(
                        "erro",
                        "Informe uma data de nascimento válida."
                );

                return "redirect:/carteirinha/" + id;
            }

        } else {
            animal.setDataNascimento(null);
        }

        tutor.setNome(nomeTutor.trim());

        if (celular != null) {
            tutor.setTelefone(celular.trim());
        }

        if (cidade != null) {
            tutor.setCidade(cidade.trim());
        }

        if (bairro != null) {
            tutor.setBairro(bairro.trim());
        }

        tutorRepository.save(tutor);
        animalRepository.save(animal);

        redirectAttributes.addFlashAttribute(
                "mensagem",
                "Cadastro atualizado com sucesso!"
        );

        return "redirect:/carteirinha/" + id;
    }

    private void carregarCarteirinha(Animal animal, Model model) {

        List<?> atualizacoes =
                atualizacaoRepository
                        .findByAnimal_IdOrderByDataAtendimentoDesc(
                                animal.getId()
                        );

        List<?> vacinas =
                vacinaRepository
                        .findByAnimal_IdOrderByDataAplicacaoDesc(
                                animal.getId()
                        );

        model.addAttribute("animal", animal);
        model.addAttribute("atualizacoes", atualizacoes);
        model.addAttribute("vacinas", vacinas);

        model.addAttribute(
                "totalAtualizacoes",
                atualizacoes.size()
        );

        model.addAttribute(
                "totalVacinas",
                vacinas.size()
        );
    }

    private Long obterTutorId(HttpSession session) {

        Object id = session.getAttribute("tutorId");

        if (id == null) {
            return null;
        }

        if (id instanceof Number) {
            return ((Number) id).longValue();
        }

        try {
            return Long.parseLong(id.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
