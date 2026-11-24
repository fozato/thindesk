package com.pfc.thindesk.controller;

import com.pfc.thindesk.service.ChamadoService;
import com.pfc.thindesk.service.ClienteService;
import com.pfc.thindesk.service.HorarioAtendimentoService;
import com.pfc.thindesk.service.StatusChamado;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    @Autowired
    private ChamadoService chamadoService;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private HorarioAtendimentoService horarioAtendimentoService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /** Dashboard: resumo dos chamados, clientes e horarios. */
    @GetMapping("/")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView("layout");
        Map<String, Long> porStatus = chamadoService.contarPorStatus();
        modelAndView.addObject("totalChamados", chamadoService.contar());
        modelAndView.addObject("abertos", porStatus.getOrDefault(StatusChamado.ABERTO, 0L));
        modelAndView.addObject("emAndamento", porStatus.getOrDefault(StatusChamado.EM_ANDAMENTO, 0L));
        modelAndView.addObject("concluidos", porStatus.getOrDefault(StatusChamado.CONCLUIDO, 0L));
        modelAndView.addObject("totalClientes", clienteService.contar());
        modelAndView.addObject("totalHorarios", horarioAtendimentoService.contar());
        modelAndView.addObject("ultimosChamados", chamadoService.recentes(5));
        return modelAndView;
    }

}
