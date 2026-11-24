package com.pfc.thindesk.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Limpeza dos textos recebidos dos formularios. */
class FormUtilsTest {

    @Test
    @DisplayName("Remove espaços das pontas")
    void removeEspacos() {
        assertThat(FormUtils.limpar("  Suporte TI  ")).isEqualTo("Suporte TI");
    }

    @Test
    @DisplayName("Texto vazio ou só com espaços vira null")
    void vazioViraNulo() {
        assertThat(FormUtils.limpar("")).isNull();
        assertThat(FormUtils.limpar("   ")).isNull();
        assertThat(FormUtils.limpar(null)).isNull();
    }
}
