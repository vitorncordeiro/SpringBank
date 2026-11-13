package br.pucpr.banco.pagamento.model;

import br.pucpr.banco.integracaobc.enums.OperacaoBc;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("PIX")
public class PagamentoPix extends Pagamento {

    @Column(name = "codigo_qr", length = 512)
    private String codigoQr;

    protected PagamentoPix() {
    }

    public PagamentoPix(Long contaOrigemId, BigDecimal valor, String codigoQr) {
        super(contaOrigemId, valor);
        this.codigoQr = codigoQr;
    }

    @Override
    public OperacaoBc operacaoBc() {
        return OperacaoBc.PIX;
    }

    @Override
    public boolean usaReserva() {
        return true;
    }

    @Override
    public String descricao() {
        return "qr=" + codigoQr;
    }

    public String getCodigoQr() { return codigoQr; }
}
