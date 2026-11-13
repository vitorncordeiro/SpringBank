package br.pucpr.banco.integracaobc.model;

import br.pucpr.banco.integracaobc.enums.OperacaoBc;
import br.pucpr.banco.integracaobc.enums.StatusMensagemBc;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "mensagens_banco_central")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusMensagemBc status = StatusMensagemBc.PENDENTE;

    @Column(name = "criada_em", nullable = false, updatable = false)
    private LocalDateTime criadaEm;

    @Column(name = "atualizada_em", nullable = false)
    private LocalDateTime atualizadaEm;

    @PrePersist
    void antesDeInserir() {
        if (status == null) {
            status = StatusMensagemBc.PENDENTE;
        }
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
