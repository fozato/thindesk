package com.pfc.thindesk.controller;

import com.pfc.thindesk.entity.Cliente;
import com.pfc.thindesk.service.ClienteService;
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

/** Paginas web de clientes: listar, criar, editar e excluir. */
@Controller
@RequestMapping("/clientes")
public class ClienteWebController {

    private final ClienteService clienteService;

    public ClienteWebController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.listarClientes());
        return "clientes";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable String id, Model model) {
        model.addAttribute("cliente", buscar(id));
        return "clientes/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("cliente") Cliente cliente, Model model, RedirectAttributes redirect) {
        cliente.setId(FormUtils.limpar(cliente.getId()));
        cliente.setNome(FormUtils.limpar(cliente.getNome()));
        cliente.setTelefone(FormUtils.limpar(cliente.getTelefone()));
        cliente.setSetor(FormUtils.limpar(cliente.getSetor()));

        if (cliente.getNome() == null) {
            model.addAttribute("erro", "Informe o nome do cliente.");
            return "clientes/form";
        }
        if (cliente.getId() != null) {
            buscar(cliente.getId()); // garante que esta editando um cliente existente
        }
        clienteService.salvar(cliente);
        redirect.addFlashAttribute("sucesso", "Cliente salvo com sucesso.");
        return "redirect:/clientes";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable String id, RedirectAttributes redirect) {
        buscar(id);
        clienteService.excluir(id);
        redirect.addFlashAttribute("sucesso", "Cliente excluído.");
        return "redirect:/clientes";
    }

    private Cliente buscar(String id) {
        return clienteService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }
}
