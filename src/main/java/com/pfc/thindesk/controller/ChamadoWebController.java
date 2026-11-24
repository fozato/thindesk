package com.pfc.thindesk.controller;

import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.service.ChamadoService;
import com.pfc.thindesk.service.StatusChamado;
import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Paginas web de chamados: listar, criar, editar e excluir. */
@Controller
@RequestMapping("/chamados")
public class ChamadoWebController {

    private final ChamadoService chamadoService;

    public ChamadoWebController(ChamadoService chamadoService) {
        this.chamadoService = chamadoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("chamados", chamadoService.listarChamados());
        return "chamados";
    }

    @GetMapping("/novo")
    public String novo(Model model, Principal principal) {
        Chamado chamado = new Chamado();
        chamado.setStatus(StatusChamado.ABERTO);
        if (principal != null) {
            chamado.setUsuario(principal.getName());
        }
        model.addAttribute("chamado", chamado);
        return "chamados/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable String id, Model model) {
        Chamado chamado = buscar(id);
        chamado.setStatus(StatusChamado.normalizar(chamado.getStatus()));
        model.addAttribute("chamado", chamado);
        return "chamados/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("chamado") Chamado chamado, Model model, RedirectAttributes redirect) {
        chamado.setId(FormUtils.limpar(chamado.getId()));
        chamado.setDescricao(FormUtils.limpar(chamado.getDescricao()));
        chamado.setTipo(FormUtils.limpar(chamado.getTipo()));
        chamado.setTecnico(FormUtils.limpar(chamado.getTecnico()));
        chamado.setUsuario(FormUtils.limpar(chamado.getUsuario()));

        String status = StatusChamado.normalizar(chamado.getStatus());
        chamado.setStatus(StatusChamado.VALORES.contains(status) ? status : StatusChamado.ABERTO);

        if (chamado.getDescricao() == null) {
            model.addAttribute("erro", "Informe a descrição do chamado.");
            return "chamados/form";
        }
        if (chamado.getId() != null) {
            buscar(chamado.getId()); // garante que esta editando um chamado existente
        }
        chamadoService.salvar(chamado);
        redirect.addFlashAttribute("sucesso", "Chamado salvo com sucesso.");
        return "redirect:/chamados";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable String id, RedirectAttributes redirect) {
        buscar(id);
        chamadoService.excluir(id);
        redirect.addFlashAttribute("sucesso", "Chamado excluído.");
        return "redirect:/chamados";
    }

    private Chamado buscar(String id) {
        return chamadoService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chamado não encontrado"));
    }
}
