
package com.example.patasebigodes.controller;

import com.example.patasebigodes.model.Administrador;
import com.example.patasebigodes.service.AdministradorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdministradorController {

    private final AdministradorService administradorService;

    public AdministradorController(
            AdministradorService administradorService) {
        this.administradorService = administradorService;
    }

    @GetMapping("/primeiro-acesso")
    public String primeiroAcesso() {
        if (administradorService.existeAdministrador()) {
            return "redirect:/admin/login";
        }

        return "admin/primeiro-acesso";
    }

    @PostMapping("/primeiro-acesso")
    public String cadastrarPrimeiroAdministrador(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String senha,
            @RequestParam String confirmarSenha,
            RedirectAttributes redirectAttributes) {

        if (administradorService.existeAdministrador()) {
            return "redirect:/admin/login";
        }

        if (senha.length() < 8) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "A senha deve ter pelo menos 8 caracteres."
            );
            return "redirect:/admin/primeiro-acesso";
        }

        if (!senha.equals(confirmarSenha)) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "As senhas não coincidem."
            );
            return "redirect:/admin/primeiro-acesso";
        }

        try {
            administradorService.criarPrimeiroAdministrador(
                    nome, email, senha
            );
        } catch (IllegalStateException e) {
            return "redirect:/admin/login";
        }

        redirectAttributes.addFlashAttribute(
                "sucesso",
                "Administrador cadastrado! Faça seu login."
        );

        return "redirect:/admin/login";
    }

    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String autenticar(
            @RequestParam String email,
            @RequestParam String senha,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        var administrador = administradorService.autenticar(
                email, senha
        );

        if (administrador.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "E-mail ou senha inválidos."
            );
            return "redirect:/admin/login";
        }

        Administrador admin = administrador.get();

        HttpSession session = request.getSession();
        session.invalidate();

        session = request.getSession(true);

        session.setAttribute("administradorId", admin.getId());
        session.setAttribute("administradorNome", admin.getNome());

        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(
            HttpSession session,
            Model model) {

        if (session.getAttribute("administradorId") == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute(
                "administradorNome",
                session.getAttribute("administradorNome")
        );

        return "admin/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();

        return "redirect:/admin/login";
    }
}
