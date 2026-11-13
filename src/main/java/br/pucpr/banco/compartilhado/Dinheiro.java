package br.pucpr.banco.compartilhado;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Valida e normaliza valores monetários (sempre positivos e com 2 casas decimais). */
public final class Dinheiro {
    private Dinheiro() {
    }

    public static BigDecimal positivo(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero");
        }
        return valor.setScale(2, RoundingMode.HALF_UP);
    }
}
