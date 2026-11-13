package br.pucpr.banco.integracaobc.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TituloBoleto(BigDecimal valor, LocalDate vencimento) {
}
