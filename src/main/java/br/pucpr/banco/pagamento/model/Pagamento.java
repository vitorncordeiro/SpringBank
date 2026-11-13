package br.pucpr.banco.pagamento.model;

import br.pucpr.banco.integracaobc.enums.OperacaoBc;
import br.pucpr.banco.pagamento.enums.StatusPagamento;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING, length = 15)
public abstract class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conta_origem_id", nullable = false)
    private Long contaOrigemId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private StatusPagamento status = StatusPagamento.INICIADO;

    @Column(name = "protocolo_bc", length = 60)
    private String protocoloBc;

    @Column(name = "motivo_falha", length = 255)
    private String motivoFalha;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    protected Pagamento() {
    }

    protected Pagamento(Long contaOrigemId, BigDecimal valor) {
        this.contaOrigemId = contaOrigemId;
        this.valor = valor;
    }

    public abstract OperacaoBc operacaoBc();

    public abstract boolean usaReserva();

    public abstract String descricao();

    public String getTipo() {
        return operacaoBc().name();
    }

    @PrePersist
    void antesDeInserir() {
        criadoEm = LocalDateTime.now();
        atualizadoEm = criadoEm;
    }

    @PreUpdate
    void antesDeAtualizar() {
        atualizadoEm = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getContaOrigemId() { return contaOrigemId; }
    public BigDecimal getValor() { return valor; }
    public StatusPagamento getStatus() { return status; }
    public String getProtocoloBc() { return protocoloBc; }
    public String getMotivoFalha() { return motivoFalha; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }

    public void setStatus(StatusPagamento status) { this.status = status; }
    public void setProtocoloBc(String protocoloBc) { this.protocoloBc = protocoloBc; }
    public void setMotivoFalha(String motivoFalha) { this.motivoFalha = motivoFalha; }
}
