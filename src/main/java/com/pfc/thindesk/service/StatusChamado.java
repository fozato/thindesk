package com.pfc.thindesk.service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

/**
 * Status possiveis de um chamado. Tambem usado nos templates como @statusChamado
 * para exibir o nome e a cor de cada status.
 */
@Component("statusChamado")
public class StatusChamado {

    public static final String ABERTO = "ABERTO";
    public static final String EM_ANDAMENTO = "EM_ANDAMENTO";
    public static final String CONCLUIDO = "CONCLUIDO";
    public static final List<String> VALORES = List.of(ABERTO, EM_ANDAMENTO, CONCLUIDO);

    /** Converte variacoes como "Aberto", "em andamento" ou "Concluído" para o valor padrao. */
    public static String normalizar(String status) {
        if (status == null) {
            return "";
        }
        String semAcento = Normalizer.normalize(status.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semAcento.toUpperCase(Locale.ROOT).replaceAll("[\\s-]+", "_");
    }

    public List<String> getValores() {
        return VALORES;
    }

    public String label(String status) {
        return switch (normalizar(status)) {
            case ABERTO -> "Aberto";
            case EM_ANDAMENTO -> "Em andamento";
            case CONCLUIDO -> "Concluído";
            default -> (status == null || status.isBlank()) ? "—" : status;
        };
    }

    public String badge(String status) {
        return switch (normalizar(status)) {
            case ABERTO -> "st-aberto";
            case EM_ANDAMENTO -> "st-andamento";
            case CONCLUIDO -> "st-concluido";
            default -> "st-outro";
        };
    }
}
