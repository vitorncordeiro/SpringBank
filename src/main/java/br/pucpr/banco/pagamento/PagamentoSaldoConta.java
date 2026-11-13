package br.pucpr.banco.pagamento;

import br.pucpr.banco.integracaobc.OperacaoBc;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("SALDO_CONTA")
public class PagamentoSaldoConta extends Pagamento {

    @Column(name = "descricao", length = 120)
    private String descricaoPagamento;

    protected PagamentoSaldoConta() {
    }

    public PagamentoSaldoConta(Long contaOrigemId, BigDecimal valor, String descricaoPagamento) {
        super(contaOrigemId, valor);
        this.descricaoPagamento = descricaoPagamento;
    }

    @Override
    public OperacaoBc operacaoBc() {
        return OperacaoBc.SALDO_CONTA;
    }

    @Override
    public boolean usaReserva() {
        return false;
    }

    @Override
    public String descricao() {
        return "descricao=" + descricaoPagamento;
    }

    public String getDescricaoPagamento() { return descricaoPagamento; }
}
