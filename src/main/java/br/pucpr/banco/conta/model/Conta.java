package br.pucpr.banco.conta.model;

import br.pucpr.banco.compartilhado.Dinheiro;
import br.pucpr.banco.compartilhado.SaldoInsuficienteException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "contas",
        uniqueConstraints = @UniqueConstraint(name = "uk_conta_agencia_numero", columnNames = {"agencia", "numero"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 10)
    private String agencia;

    @Column(nullable = false, length = 20)
    private String numero;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;

    @Column(name = "saldo_reservado", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoReservado = BigDecimal.ZERO;

    @Version
    private Long versao;

    @Column(name = "aberta_em", nullable = false, updatable = false)
    private LocalDateTime abertaEm;

    public Conta(Long usuarioId, String agencia, String numero) {
        this.usuarioId = usuarioId;
        this.agencia = agencia;
        this.numero = numero;
    }

    @PrePersist
    void antesDeInserir() {
        this.abertaEm = LocalDateTime.now();
    }

    public BigDecimal getSaldoDisponivel() {
        return saldo.subtract(saldoReservado);
    }

    public void creditar(BigDecimal valor) {
        saldo = saldo.add(Dinheiro.positivo(valor));
    }

    public void reservar(BigDecimal valor) {
        BigDecimal v = Dinheiro.positivo(valor);
        exigirDisponivel(v);
        saldoReservado = saldoReservado.add(v);
    }

    public void liberarReserva(BigDecimal valor) {
        BigDecimal v = Dinheiro.positivo(valor);
        if (saldoReservado.compareTo(v) < 0) {
            throw new IllegalStateException("Valor a liberar maior que o reservado");
        }
        saldoReservado = saldoReservado.subtract(v);
    }

    public void debitar(BigDecimal valor, boolean consumirReserva) {
        BigDecimal v = Dinheiro.positivo(valor);
        if (consumirReserva) {
            liberarReserva(v);
        } else {
            exigirDisponivel(v);
        }
        saldo = saldo.subtract(v);
    }

    private void exigirDisponivel(BigDecimal v) {
        if (getSaldoDisponivel().compareTo(v) < 0) {
            throw new SaldoInsuficienteException(
                    "Saldo disponível R$ " + getSaldoDisponivel() + " é menor que o valor R$ " + v);
        }
    }

    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public String getAgencia() { return agencia; }
    public String getNumero() { return numero; }
    public BigDecimal getSaldo() { return saldo; }
    public BigDecimal getSaldoReservado() { return saldoReservado; }
    public Long getVersao() { return versao; }
    public LocalDateTime getAbertaEm() { return abertaEm; }
}
