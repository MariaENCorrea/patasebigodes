
package com.example.patasebigodes.controller;

import com.example.patasebigodes.model.Tutor;
import com.example.patasebigodes.repository.TutorRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class LoginController {

    private final TutorRepository tutorRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginController(TutorRepository tutorRepository) {
        this.tutorRepository = tutorRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String autenticar(
            @RequestParam String email,
            @RequestParam String senha,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        email = email.trim().toLowerCase();

        Optional<Tutor> resultado =
                tutorRepository.findByEmail(email);

        if (resultado.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "E-mail ou senha incorretos."
            );

            return "redirect:/login";
        }

        Tutor tutor = resultado.get();

        if (tutor.getSenha() == null ||
                !passwordEncoder.matches(senha, tutor.getSenha())) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    "E-mail ou senha incorretos."
            );

            return "redirect:/login";
        }

        session.setAttribute("tutorId", tutor.getId());
        session.setAttribute("tutorNome", tutor.getNome());

        return "redirect:/horarios";
    }

    @GetMapping("/logout")
    public String logout(
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        session.invalidate();

        redirectAttributes.addFlashAttribute(
                "mensagem",
                "Você saiu da sua conta."
        );

        return "redirect:/login";
    }
}