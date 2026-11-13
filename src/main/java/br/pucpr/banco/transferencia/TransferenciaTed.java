package br.pucpr.banco.transferencia;

import br.pucpr.banco.integracaobc.OperacaoBc;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("TED")
public class TransferenciaTed extends TransferenciaBancaria {

    protected TransferenciaTed() {
    }

    public TransferenciaTed(Long contaOrigemId, BigDecimal valor, String banco, String agencia,
                            String conta, String favorecido) {
        super(contaOrigemId, valor, banco, agencia, conta, favorecido);
    }

    @Override
    public OperacaoBc operacaoBc() {
        return OperacaoBc.TED;
    }
}
