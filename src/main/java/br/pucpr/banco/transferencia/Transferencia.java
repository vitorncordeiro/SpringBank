package br.pucpr.banco.transferencia;

import br.pucpr.banco.integracaobc.OperacaoBc;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transferencias")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING, length = 10)
public abstract class Transferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conta_origem_id", nullable = false)
    private Long contaOrigemId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private StatusTransferencia status = StatusTransferencia.INICIADA;

    @Column(name = "protocolo_bc", length = 60)
    private String protocoloBc;

    @Column(name = "motivo_falha", length = 255)
    private String motivoFalha;

    @Column(name = "criada_em", nullable = false, updatable = false)
    private LocalDateTime criadaEm;

    @Column(name = "atualizada_em", nullable = false)
    private LocalDateTime atualizadaEm;

    protected Transferencia() {
    }

    protected Transferencia(Long contaOrigemId, BigDecimal valor) {
        this.contaOrigemId = contaOrigemId;
        this.valor = valor;
    }

    public abstract OperacaoBc operacaoBc();

    public abstract String descricaoDestino();

    public String getTipo() {
        return operacaoBc().name();
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

    public Long getId() { return id; }
    public Long getContaOrigemId() { return contaOrigemId; }
    public BigDecimal getValor() { return valor; }
    public StatusTransferencia getStatus() { return status; }
    public String getProtocoloBc() { return protocoloBc; }
    public String getMotivoFalha() { return motivoFalha; }
    public LocalDateTime getCriadaEm() { return criadaEm; }
    public LocalDateTime getAtualizadaEm() { return atualizadaEm; }

    void setStatus(StatusTransferencia status) { this.status = status; }
    void setProtocoloBc(String protocoloBc) { this.protocoloBc = protocoloBc; }
    void setMotivoFalha(String motivoFalha) { this.motivoFalha = motivoFalha; }
}
