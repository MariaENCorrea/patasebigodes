
package com.example.patasebigodes.controller;

import com.example.patasebigodes.model.Tutor;
import com.example.patasebigodes.repository.TutorRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CadastroController {

    private final TutorRepository tutorRepository;
    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public CadastroController(TutorRepository tutorRepository) {
        this.tutorRepository = tutorRepository;
    }

    @GetMapping("/cadastro")
    public String cadastro() {
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(
            @RequestParam String nome,
            @RequestParam String cpf,
            @RequestParam String telefone,
            @RequestParam String email,
            @RequestParam String senha,
            @RequestParam String cep,
            @RequestParam String rua,
            @RequestParam String numero,
            @RequestParam(required = false) String complemento,
            @RequestParam String bairro,
            @RequestParam String cidade,
            @RequestParam String estado,
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

        tutor.setNome(nome.trim());
        tutor.setCpf(cpf);
        tutor.setTelefone(telefone);
        tutor.setEmail(email);
        tutor.setSenha(passwordEncoder.encode(senha));

        tutor.setCep(cep);
        tutor.setRua(rua);
        tutor.setNumero(numero);
        tutor.setComplemento(complemento);
        tutor.setBairro(bairro);
        tutor.setCidade(cidade);
        tutor.setEstado(estado);

        try {
            tutorRepository.save(tutor);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Cadastro realizado com sucesso! Faça login para continuar."
            );

            return "redirect:/login";

        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "CPF ou e-mail já cadastrado."
            );

            return "redirect:/cadastro";
        }
    }
}
