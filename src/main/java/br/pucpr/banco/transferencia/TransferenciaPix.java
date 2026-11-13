package br.pucpr.banco.transferencia;

import br.pucpr.banco.integracaobc.OperacaoBc;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("PIX")
public class TransferenciaPix extends Transferencia {

    @Column(name = "chave_pix_destino", length = 120)
    private String chavePixDestino;

    protected TransferenciaPix() {
    }

    public TransferenciaPix(Long contaOrigemId, BigDecimal valor, String chavePixDestino) {
        super(contaOrigemId, valor);
        this.chavePixDestino = chavePixDestino;
    }

    @Override
    public OperacaoBc operacaoBc() {
        return OperacaoBc.PIX;
    }

    @Override
    public String descricaoDestino() {
        return "chave=" + chavePixDestino;
    }

    public String getChavePixDestino() { return chavePixDestino; }
}
