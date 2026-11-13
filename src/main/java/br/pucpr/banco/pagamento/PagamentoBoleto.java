package br.pucpr.banco.pagamento;

import br.pucpr.banco.integracaobc.OperacaoBc;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("BOLETO")
public class PagamentoBoleto extends Pagamento {

    @Column(name = "codigo_barras", length = 48)
    private String codigoBarras;

    @Column(name = "vencimento")
    private LocalDate vencimento;

    protected PagamentoBoleto() {
    }

    public PagamentoBoleto(Long contaOrigemId, BigDecimal valor, String codigoBarras, LocalDate vencimento) {
        super(contaOrigemId, valor);
        this.codigoBarras = codigoBarras;
        this.vencimento = vencimento;
    }

    @Override
    public OperacaoBc operacaoBc() {
        return OperacaoBc.BOLETO;
    }

    @Override
    public boolean usaReserva() {
        return true;
    }

    @Override
    public String descricao() {
        return "codigoBarras=" + codigoBarras;
    }

    public String getCodigoBarras() { return codigoBarras; }
    public LocalDate getVencimento() { return vencimento; }
}
