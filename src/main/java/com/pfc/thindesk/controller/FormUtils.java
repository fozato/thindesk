package com.pfc.thindesk.controller;

/** Utilitarios para tratar os dados recebidos dos formularios. */
final class FormUtils {

    private FormUtils() {
    }

    /** Remove espacos das pontas; texto vazio vira null. */
    static String limpar(String texto) {
        if (texto == null) {
            return null;
        }
        String t = texto.trim();
        return t.isEmpty() ? null : t;
    }
}
