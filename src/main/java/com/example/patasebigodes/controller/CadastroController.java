
package com.example.patasebigodes.controller;

import com.example.patasebigodes.model.Animal;
import com.example.patasebigodes.model.Tutor;
import com.example.patasebigodes.repository.TutorRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class CadastroController {

    private final TutorRepository tutorRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public CadastroController(TutorRepository tutorRepository) {
        this.tutorRepository = tutorRepository;
    }

    @PostMapping("/cadastro")
    public String cadastrar(

            @RequestParam String nomeAnimal,
            @RequestParam String sexo,
            @RequestParam(required = false) Integer idade,
            @RequestParam String especie,
            @RequestParam(required = false) String raca,

            @RequestParam String nomeProprietario,
            @RequestParam String cpf,
            @RequestParam String telefone,
            @RequestParam String email,
            @RequestParam String senha,
            @RequestParam String cidade,
            @RequestParam String bairro,

            RedirectAttributes redirectAttributes) {

        cpf = cpf.replaceAll("\\D", "");
        email = email.trim().toLowerCase();

        if (tutorRepository.existsByCpf(cpf)) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Este CPF já está cadastrado."
            );

            return "redirect:/cadastro";
        }

        if (tutorRepository.existsByEmail(email)) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Este e-mail já está cadastrado."
            );

            return "redirect:/cadastro";
        }

        Tutor tutor = new Tutor();

        tutor.setNome(nomeProprietario);
        tutor.setCpf(cpf);
        tutor.setTelefone(telefone);
        tutor.setEmail(email);
        tutor.setSenha(passwordEncoder.encode(senha));
        tutor.setCidade(cidade);
        tutor.setBairro(bairro);

        Animal animal = new Animal();

        animal.setNome(nomeAnimal);
        animal.setSexo(sexo);
        animal.setEspecie(especie);
        animal.setRaca(raca);

        if (idade != null && idade >= 0) {
            animal.setDataNascimento(
                    LocalDate.now().minusYears(idade)
            );
        }

        animal.setTutor(tutor);

        tutor.getAnimais().add(animal);

        try {

            tutorRepository.save(tutor);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Cadastro realizado com sucesso!"
            );

            return "redirect:/";

        } catch (DataIntegrityViolationException e) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    "CPF ou e-mail já cadastrado."
            );

            return "redirect:/cadastro";
        }
    }
}
