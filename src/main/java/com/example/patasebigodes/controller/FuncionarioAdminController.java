
package com.example.patasebigodes.controller;

import com.example.patasebigodes.model.Funcionario;
import com.example.patasebigodes.repository.EspecialidadeRepository;
import com.example.patasebigodes.service.FuncionarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/funcionarios")
public class FuncionarioAdminController {

    private final FuncionarioService funcionarioService;
    private final EspecialidadeRepository especialidadeRepository;

    public FuncionarioAdminController(
            FuncionarioService funcionarioService,
            EspecialidadeRepository especialidadeRepository) {
        this.funcionarioService = funcionarioService;
        this.especialidadeRepository = especialidadeRepository;
    }

    @GetMapping
    public String listar(HttpSession session, Model model) {
        if (!administradorLogado(session)) {
            return "redirect:/admin/login";
        }

        model.addAttribute("funcionarios", funcionarioService.listarTodos());

        return "admin/funcionarios/listar";
    }

    @GetMapping("/novo")
    public String novo(HttpSession session, Model model) {
        if (!administradorLogado(session)) {
            return "redirect:/admin/login";
        }

        model.addAttribute("funcionario", new Funcionario());
        model.addAttribute(
                "especialidades",
                especialidadeRepository.findByAtivaTrue()
        );

        return "admin/funcionarios/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        if (!administradorLogado(session)) {
            return "redirect:/admin/login";
        }

        model.addAttribute("funcionario", funcionarioService.buscarPorId(id));
        model.addAttribute(
                "especialidades",
                especialidadeRepository.findByAtivaTrue()
        );

        return "admin/funcionarios/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(
            @ModelAttribute Funcionario funcionario,
            @RequestParam(required = false) List<Long> especialidadesIds,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!administradorLogado(session)) {
            return "redirect:/admin/login";
        }

        try {
            funcionarioService.salvar(funcionario, especialidadesIds);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Funcionário salvo com sucesso."
            );

            return "redirect:/admin/funcionarios";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());

            if (funcionario.getId() == null) {
                return "redirect:/admin/funcionarios/novo";
            }

            return "redirect:/admin/funcionarios/"
                    + funcionario.getId() + "/editar";
        }
    }

    @PostMapping("/{id}/status")
    public String alterarStatus(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!administradorLogado(session)) {
            return "redirect:/admin/login";
        }

        funcionarioService.alterarStatus(id);

        redirectAttributes.addFlashAttribute(
                "sucesso",
                "Status do funcionário atualizado."
        );

        return "redirect:/admin/funcionarios";
    }

    private boolean administradorLogado(HttpSession session) {
        return session.getAttribute("administradorId") != null;
    }
}
