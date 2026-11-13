package br.pucpr.banco.conta.service;

import br.pucpr.banco.compartilhado.Dinheiro;
import br.pucpr.banco.compartilhado.RecursoNaoEncontradoException;
import br.pucpr.banco.conta.model.Conta;
import br.pucpr.banco.conta.model.Movimentacao;
import br.pucpr.banco.conta.dto.MovimentacaoResponse;
import br.pucpr.banco.conta.dto.SaldoConta;
import br.pucpr.banco.conta.enums.TipoMovimentacao;
import br.pucpr.banco.conta.repository.ContaRepository;
import br.pucpr.banco.conta.repository.MovimentacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContaService {

    private final ContaRepository contas;
    private final MovimentacaoRepository movimentacoes;

    @Transactional
    public Conta abrir(Long usuarioId, String agencia, String numero) {
        if (contas.existsByAgenciaAndNumero(agencia, numero)) {
            throw new IllegalArgumentException("Já existe a conta " + agencia + "/" + numero);
        }
        return contas.save(Conta.builder()
                .usuarioId(usuarioId)
                .agencia(agencia)
                .numero(numero)
                .build());
    }

    public Conta buscar(Long contaId) {
        return contas.findById(contaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta " + contaId + " não encontrada"));
    }

    public SaldoConta consultarSaldo(Long contaId) {
        return SaldoConta.de(buscar(contaId));
    }

    @Transactional
    public SaldoConta creditar(Long contaId, BigDecimal valor, String referencia) {
        Conta conta = buscar(contaId);
        conta.creditar(valor);
        registrar(conta, TipoMovimentacao.CREDITO, valor, referencia);
        return SaldoConta.de(conta);
    }

    @Transactional
    public SaldoConta reservarValor(Long contaId, BigDecimal valor, String referencia) {
        Conta conta = buscar(contaId);
        conta.reservar(valor);
        registrar(conta, TipoMovimentacao.RESERVA, valor, referencia);
        return SaldoConta.de(conta);
    }

    @Transactional
    public SaldoConta liberarReserva(Long contaId, BigDecimal valor, String referencia) {
        Conta conta = buscar(contaId);
        conta.liberarReserva(valor);
        registrar(conta, TipoMovimentacao.LIBERACAO_RESERVA, valor, referencia);
        return SaldoConta.de(conta);
    }

    @Transactional
    public SaldoConta debitar(Long contaId, BigDecimal valor, String referencia, boolean consumirReserva) {
        Conta conta = buscar(contaId);
        conta.debitar(valor, consumirReserva);
        registrar(conta, TipoMovimentacao.DEBITO, valor, referencia);
        return SaldoConta.de(conta);
    }

    public List<MovimentacaoResponse> extrato(Long contaId) {
        buscar(contaId);
        return movimentacoes.findByContaIdOrderByIdAsc(contaId).stream().map(MovimentacaoResponse::de).toList();
    }

    private void registrar(Conta conta, TipoMovimentacao tipo, BigDecimal valor, String referencia) {
        movimentacoes.save(Movimentacao.builder()
                .conta(conta)
                .tipo(tipo)
                .valor(Dinheiro.positivo(valor))
                .saldoApos(conta.getSaldo())
                .referencia(referencia)
                .build());
    }
}
