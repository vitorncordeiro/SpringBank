package br.pucpr.banco.integracaobc;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mensagens_banco_central")
public class MensagemBancoCentral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chave_idempotencia", nullable = false, unique = true, length = 80)
    private String chaveIdempotencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OperacaoBc operacao;

    @Column(nullable = false, length = 2000)
    private String requisicao;

    @Column(length = 2000)
    private String resposta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusMensagemBc status = StatusMensagemBc.PENDENTE;

    @Column(name = "criada_em", nullable = false, updatable = false)
    private LocalDateTime criadaEm;

    @Column(name = "atualizada_em", nullable = false)
    private LocalDateTime atualizadaEm;

    protected MensagemBancoCentral() {
    }

    public MensagemBancoCentral(String chaveIdempotencia, OperacaoBc operacao, String requisicao) {
        this.chaveIdempotencia = chaveIdempotencia;
        this.operacao = operacao;
        this.requisicao = requisicao;
    }

    @PrePersist
    void antesDeInserir() {
        criadaEm = LocalDateTime.now();
        atualizadaEm = criadaEm;
    }

    @PreUpdate
    void antesDeAtualizar() {
        atualizadaEm = LocalDateTime.now();
    }

    public void confirmar(String resposta) {
        this.resposta = resposta;
        this.status = StatusMensagemBc.CONFIRMADA;
    }

    public void falhar(String motivo) {
        this.resposta = motivo;
        this.status = StatusMensagemBc.FALHA;
    }

    public Long getId() { return id; }
    public String getChaveIdempotencia() { return chaveIdempotencia; }
    public OperacaoBc getOperacao() { return operacao; }
    public String getRequisicao() { return requisicao; }
    public String getResposta() { return resposta; }
    public StatusMensagemBc getStatus() { return status; }
    public LocalDateTime getCriadaEm() { return criadaEm; }
    public LocalDateTime getAtualizadaEm() { return atualizadaEm; }
}
