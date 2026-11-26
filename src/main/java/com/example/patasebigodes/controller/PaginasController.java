
package com.example.patasebigodes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaginasController {
    
    @GetMapping("/horarios")
    public String horarios() {
        return "horarios";
    }

    @GetMapping("/apresentacao")
    public String apresentacao() {
        return "apresentacao";
    }


}
