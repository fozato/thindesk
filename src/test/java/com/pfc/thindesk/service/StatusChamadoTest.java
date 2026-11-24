package com.pfc.thindesk.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Nomes, cores e normalizacao dos status de chamado. */
class StatusChamadoTest {

    private final StatusChamado status = new StatusChamado();

    @ParameterizedTest(name = "\"{0}\" -> {1}")
    @CsvSource({
        "Aberto, ABERTO",
        "em andamento, EM_ANDAMENTO",
        "Em-Andamento, EM_ANDAMENTO",
        "Concluído, CONCLUIDO",
        "'  concluido  ', CONCLUIDO"
    })
    @DisplayName("Variações de escrita viram o valor padrão")
    void normalizar(String entrada, String esperado) {
        assertThat(StatusChamado.normalizar(entrada)).isEqualTo(esperado);
    }

    @Test
    @DisplayName("Status nulo vira texto vazio")
    void normalizarNulo() {
        assertThat(StatusChamado.normalizar(null)).isEmpty();
    }

    @ParameterizedTest(name = "{0} -> \"{1}\" / {2}")
    @CsvSource({
        "ABERTO, Aberto, st-aberto",
        "EM_ANDAMENTO, Em andamento, st-andamento",
        "CONCLUIDO, Concluído, st-concluido"
    })
    @DisplayName("Cada status tem seu nome e sua cor")
    void labelEBadge(String valor, String label, String badge) {
        assertThat(status.label(valor)).isEqualTo(label);
        assertThat(status.badge(valor)).isEqualTo(badge);
    }

    @Test
    @DisplayName("Status desconhecido ou vazio usa o estilo neutro")
    void statusDesconhecido() {
        assertThat(status.label("Pausado")).isEqualTo("Pausado");
        assertThat(status.label(null)).isEqualTo("—");
        assertThat(status.label("  ")).isEqualTo("—");
        assertThat(status.badge("Pausado")).isEqualTo("st-outro");
    }

    @Test
    @DisplayName("Lista de status oferecida nos formulários")
    void valores() {
        assertThat(status.getValores())
                .containsExactly(StatusChamado.ABERTO, StatusChamado.EM_ANDAMENTO, StatusChamado.CONCLUIDO);
    }
}
