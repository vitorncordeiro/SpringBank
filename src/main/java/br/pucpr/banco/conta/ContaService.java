package br.pucpr.banco.conta;

import br.pucpr.banco.compartilhado.Dinheiro;
import br.pucpr.banco.compartilhado.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Cada método público é UMA transação: ou o saldo e a movimentação são gravados juntos, ou nada é gravado.
 * Dentro da transação, a Conta carregada fica "gerenciada": basta alterar o objeto que o Hibernate
 * detecta a mudança (dirty checking) e gera o UPDATE sozinho no commit.
 */
@Service
public class ContaService {

    private final ContaRepository contas;
    private final MovimentacaoRepository movimentacoes;

    public ContaService(ContaRepository contas, MovimentacaoRepository movimentacoes) {
        this.contas = contas;
        this.movimentacoes = movimentacoes;
    }

    @Transactional
    public Conta abrir(Long usuarioId, String agencia, String numero) {
        // Em produção, o usuário seria validado chamando a API do microsserviço de Autenticação.
        if (contas.existsByAgenciaAndNumero(agencia, numero)) {
            throw new IllegalArgumentException("Já existe a conta " + agencia + "/" + numero);
        }
        return contas.save(new Conta(usuarioId, agencia, numero));
    }

    public Conta buscar(Long contaId) {
        return contas.findById(contaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta " + contaId + " não encontrada"));
    }

    /** consultarSaldo() das sequências. */
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

    /** reservarValor(valor): separa o dinheiro enquanto o Banco Central ainda não confirmou. */
    @Transactional
    public SaldoConta reservarValor(Long contaId, BigDecimal valor, String referencia) {
        Conta conta = buscar(contaId);
        conta.reservar(valor);
        registrar(conta, TipoMovimentacao.RESERVA, valor, referencia);
        return SaldoConta.de(conta);
    }

    /** Compensação: devolve a reserva quando a operação falha depois de reservar. */
    @Transactional
    public SaldoConta liberarReserva(Long contaId, BigDecimal valor, String referencia) {
        Conta conta = buscar(contaId);
        conta.liberarReserva(valor);
        registrar(conta, TipoMovimentacao.LIBERACAO_RESERVA, valor, referencia);
        return SaldoConta.de(conta);
    }

    /** debitar(valor): efetiva a saída do dinheiro (consumindo a reserva, quando houver). */
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
        movimentacoes.save(new Movimentacao(conta, tipo, Dinheiro.positivo(valor), referencia));
    }
}
