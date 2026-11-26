
package com.example.patasebigodes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaginasController {

    @GetMapping("/consultas")
    public String consultas() {
        return "consultas";
    }

    @GetMapping("/horarios")
    public String horarios() {
        return "horarios";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String cadastro() {
        return "cadastro";
    }

    @GetMapping("/apresentacao")
    public String apresentacao() {
        return "apresentacao";
    }


}
