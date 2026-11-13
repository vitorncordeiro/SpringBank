package br.pucpr.banco.conta.model;

import br.pucpr.banco.conta.enums.TipoMovimentacao;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Histórico (extrato) de tudo que mexeu no saldo. Relacionamento N:1 com Conta (mesmo microsserviço). */
@Entity
@Table(name = "movimentacoes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Movimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conta_id", nullable = false)
    private Conta conta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMovimentacao tipo;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Column(name = "saldo_apos", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoApos;

    /** Identifica a operação de origem, ex.: TRANSFERENCIA-12 ou PAGAMENTO-7. */
    @Column(length = 60)
    private String referencia;

    @Column(name = "criada_em", nullable = false, updatable = false)
    private LocalDateTime criadaEm;

    @PrePersist
    void antesDeInserir() {
        this.criadaEm = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Conta getConta() { return conta; }
    public TipoMovimentacao getTipo() { return tipo; }
    public BigDecimal getValor() { return valor; }
    public BigDecimal getSaldoApos() { return saldoApos; }
    public String getReferencia() { return referencia; }
    public LocalDateTime getCriadaEm() { return criadaEm; }
}
