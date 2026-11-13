package br.pucpr.banco.transferencia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
public abstract class TransferenciaBancaria extends Transferencia {

    @Column(name = "banco_destino", length = 10)
    private String bancoDestino;

    @Column(name = "agencia_destino", length = 10)
    private String agenciaDestino;

    @Column(name = "conta_destino", length = 20)
    private String contaDestino;

    @Column(name = "favorecido", length = 120)
    private String favorecido;

    protected TransferenciaBancaria() {
    }

    protected TransferenciaBancaria(Long contaOrigemId, BigDecimal valor, String bancoDestino,
                                    String agenciaDestino, String contaDestino, String favorecido) {
        super(contaOrigemId, valor);
        this.bancoDestino = bancoDestino;
        this.agenciaDestino = agenciaDestino;
        this.contaDestino = contaDestino;
        this.favorecido = favorecido;
    }

    @Override
    public String descricaoDestino() {
        return "banco=" + bancoDestino + ";agencia=" + agenciaDestino + ";conta=" + contaDestino
                + ";favorecido=" + favorecido;
    }

    public String getBancoDestino() { return bancoDestino; }
    public String getAgenciaDestino() { return agenciaDestino; }
    public String getContaDestino() { return contaDestino; }
    public String getFavorecido() { return favorecido; }
}
