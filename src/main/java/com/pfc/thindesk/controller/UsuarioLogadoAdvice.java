package com.pfc.thindesk.controller;

import java.security.Principal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Disponibiliza o nome do usuario logado para todas as paginas (ex.: menu lateral). */
@ControllerAdvice
public class UsuarioLogadoAdvice {

    @ModelAttribute("usuarioLogado")
    public String usuarioLogado(Principal principal) {
        return principal == null ? null : principal.getName();
    }
}
