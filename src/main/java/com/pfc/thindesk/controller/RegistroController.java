package com.pfc.thindesk.controller;

import com.pfc.thindesk.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistroController {

    private final UsuarioService usuarioService;

    public RegistroController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/registro")
    public String formulario(Model model) {
        model.addAttribute("senhaMin", UsuarioService.SENHA_MIN);
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@RequestParam String username,
                            @RequestParam String password,
                            @RequestParam String confirmPassword,
                            Model model) {
        try {
            usuarioService.cadastrar(username, password, confirmPassword);
            return "redirect:/login?registrado";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("senhaMin", UsuarioService.SENHA_MIN);
            return "registro";
        }
    }
}
