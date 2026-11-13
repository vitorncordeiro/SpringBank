package br.pucpr.banco.transferencia.model;

import br.pucpr.banco.integracaobc.enums.OperacaoBc;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("DOC")
public class TransferenciaDoc extends TransferenciaBancaria {

    protected TransferenciaDoc() {
    }

    public TransferenciaDoc(Long contaOrigemId, BigDecimal valor, String banco, String agencia,
                            String conta, String favorecido) {
        super(contaOrigemId, valor, banco, agencia, conta, favorecido);
    }

    @Override
    public OperacaoBc operacaoBc() {
        return OperacaoBc.DOC;
    }
}
