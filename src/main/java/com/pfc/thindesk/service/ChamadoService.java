package com.pfc.thindesk.service;

import com.pfc.thindesk.entity.Chamado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.pfc.thindesk.repository.ChamadoRepository;

@Service
public class ChamadoService {

    @Autowired
    private ChamadoRepository chamadoRepository;

    public Chamado criarChamado(Chamado chamado) {
        return chamadoRepository.save(chamado);
    }

    public List<Chamado> listarChamados() {
        return chamadoRepository.findAll();
    }

    public Optional<Chamado> buscarPorId(String id) {
        return chamadoRepository.findById(id);
    }

    public Chamado salvar(Chamado chamado) {
        return chamadoRepository.save(chamado);
    }

    public void excluir(String id) {
        chamadoRepository.deleteById(id);
    }

    public long contar() {
        return chamadoRepository.count();
    }

    /** Quantidade de chamados por status (chaves normalizadas: ABERTO, EM_ANDAMENTO, CONCLUIDO...). */
    public Map<String, Long> contarPorStatus() {
        return chamadoRepository.findAll().stream()
                .map(c -> StatusChamado.normalizar(c.getStatus()))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    /** Ultimos chamados criados (o ObjectId do MongoDB cresce com o tempo de criacao). */
    public List<Chamado> recentes(int quantidade) {
        return chamadoRepository.findAll(PageRequest.of(0, quantidade, Sort.by(Sort.Direction.DESC, "id")))
                .getContent();
    }

    public Chamado atualizarChamado(String id, Chamado chamadoAtualizado) {
        return chamadoRepository.findById(id)
                .map(chamado -> {
                    chamado.setDescricao(chamadoAtualizado.getDescricao());
                    chamado.setStatus(chamadoAtualizado.getStatus());
                    chamado.setTipo(chamadoAtualizado.getTipo());
                    chamado.setTecnico(chamadoAtualizado.getTecnico());
                    chamado.setUsuario(chamadoAtualizado.getUsuario());
                    return chamadoRepository.save(chamado);
                })
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado com id: " + id));
    }

}
