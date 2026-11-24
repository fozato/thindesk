package com.pfc.thindesk.controller;

import com.pfc.thindesk.entity.HorarioAtendimento;
import com.pfc.thindesk.service.HorarioAtendimentoService;
import java.util.List;
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

/** Pagina de horarios de atendimento por setor: listar, cadastrar e excluir. */
@Controller
@RequestMapping("/ajustes-horarios")
public class HorarioAtendimentoController {

    static final List<String> DIAS = List.of(
            "Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira",
            "Sexta-feira", "Sábado", "Domingo");

    private final HorarioAtendimentoService horarioAtendimentoService;

    public HorarioAtendimentoController(HorarioAtendimentoService horarioAtendimentoService) {
        this.horarioAtendimentoService = horarioAtendimentoService;
    }

    @GetMapping
    public String listarHorarios(Model model) {
        model.addAttribute("horarios", horarioAtendimentoService.listarTodos());
        model.addAttribute("novoHorario", new HorarioAtendimento());
        model.addAttribute("dias", DIAS);
        return "ajustes-horarios";
    }

    @PostMapping("/salvar")
    public String salvarHorario(@ModelAttribute HorarioAtendimento horario, RedirectAttributes redirect) {
        horario.setId(null); // sempre um novo registro
        horario.setSetor(FormUtils.limpar(horario.getSetor()));
        horario.setDiaSemana(FormUtils.limpar(horario.getDiaSemana()));
        horario.setHorarioInicio(FormUtils.limpar(horario.getHorarioInicio()));
        horario.setHorarioFim(FormUtils.limpar(horario.getHorarioFim()));

        String erro = validar(horario);
        if (erro != null) {
            redirect.addFlashAttribute("erro", erro);
            return "redirect:/ajustes-horarios";
        }
        horarioAtendimentoService.salvar(horario);
        redirect.addFlashAttribute("sucesso", "Horário salvo com sucesso.");
        return "redirect:/ajustes-horarios";
    }

    @PostMapping("/excluir/{id}")
    public String deletarHorario(@PathVariable String id, RedirectAttributes redirect) {
        if (!horarioAtendimentoService.existe(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Horário não encontrado");
        }
        horarioAtendimentoService.deletar(id);
        redirect.addFlashAttribute("sucesso", "Horário excluído.");
        return "redirect:/ajustes-horarios";
    }

    private static String validar(HorarioAtendimento h) {
        if (h.getSetor() == null) {
            return "Informe o setor.";
        }
        if (h.getDiaSemana() == null || !DIAS.contains(h.getDiaSemana())) {
            return "Escolha um dia da semana válido.";
        }
        if (h.getHorarioInicio() == null || h.getHorarioFim() == null
                || !h.getHorarioInicio().matches("\\d{2}:\\d{2}") || !h.getHorarioFim().matches("\\d{2}:\\d{2}")) {
            return "Informe os horários de início e fim.";
        }
        if (h.getHorarioFim().compareTo(h.getHorarioInicio()) <= 0) {
            return "O horário de fim deve ser depois do horário de início.";
        }
        return null;
    }
}
