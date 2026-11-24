package com.pfc.thindesk.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Disponibiliza dados comuns para todas as paginas (ex.: menu lateral). */
@ControllerAdvice
public class UsuarioLogadoAdvice {

    @ModelAttribute("usuarioLogado")
    public String usuarioLogado(Principal principal) {
        return principal == null ? null : principal.getName();
    }

    /** Caminho da pagina atual, usado para destacar o item ativo do menu. */
    @ModelAttribute("paginaAtual")
    public String paginaAtual(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
