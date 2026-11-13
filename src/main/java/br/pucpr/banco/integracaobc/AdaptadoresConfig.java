package br.pucpr.banco.integracaobc;

import br.pucpr.banco.integracaobc.dto.AdaptadorSimulado;
import br.pucpr.banco.integracaobc.enums.OperacaoBc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Configuration
public class AdaptadoresConfig {

    private static String protocolo(String prefixo) {
        return prefixo + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Bean
    AdaptadorBancoCentral adaptadorPix() {
        return new AdaptadorSimulado(OperacaoBc.PIX, req -> protocolo("PIX"));
    }

    @Bean
    AdaptadorBancoCentral adaptadorTed() {
        return new AdaptadorSimulado(OperacaoBc.TED, req -> protocolo("TED"));
    }

    @Bean
    AdaptadorBancoCentral adaptadorDoc() {
        return new AdaptadorSimulado(OperacaoBc.DOC, req -> protocolo("DOC"));
    }

    @Bean
    AdaptadorBancoCentral adaptadorSaldoConta() {
        return new AdaptadorSimulado(OperacaoBc.SALDO_CONTA, req -> protocolo("SC"));
    }

    @Bean
    AdaptadorBancoCentral adaptadorBoleto() {
        return new AdaptadorSimulado(OperacaoBc.BOLETO, req -> {
            if (req.startsWith("CONSULTA|")) {
                String codigo = req.substring("CONSULTA|".length());
                if (!codigo.matches("\\d{44}")) {
                    throw new IllegalArgumentException("Código de barras deve ter 44 dígitos");
                }
                BigDecimal valor = new BigDecimal(codigo.substring(9, 19)).movePointLeft(2);
                return "valor=" + valor + ";vencimento=" + LocalDate.now().plusDays(10);
            }
            return protocolo("BOL");
        });
    }
}
