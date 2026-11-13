package br.pucpr.banco.pagamento;

import br.pucpr.banco.compartilhado.RecursoNaoEncontradoException;
import br.pucpr.banco.compartilhado.SaldoInsuficienteException;
import br.pucpr.banco.conta.ContaService;
import br.pucpr.banco.conta.SaldoConta;
import br.pucpr.banco.integracaobc.IntegracaoBancoCentralService;
import br.pucpr.banco.integracaobc.TituloBoleto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PagamentoService {

    private final PagamentoRepository repository;
    private final ContaService contaService;
    private final IntegracaoBancoCentralService integracao;

    public PagamentoService(PagamentoRepository repository, ContaService contaService,
                            IntegracaoBancoCentralService integracao) {
        this.repository = repository;
        this.contaService = contaService;
        this.integracao = integracao;
    }

    public Pagamento pagarPix(Long contaOrigemId, BigDecimal valor, String codigoQr) {
        if (codigoQr == null || codigoQr.isBlank()) {
            throw new IllegalArgumentException("QR Code inválido");
        }
        return executar(new PagamentoPix(contaOrigemId, valor, codigoQr));
    }

    public Pagamento pagarBoleto(Long contaOrigemId, String codigoBarras) {
        if (codigoBarras == null || !codigoBarras.matches("\\d{44}")) {
            throw new IllegalArgumentException("Código de barras deve ter 44 dígitos");
        }
        TituloBoleto titulo = integracao.consultarBoleto(codigoBarras);
        return executar(new PagamentoBoleto(contaOrigemId, titulo.valor(), codigoBarras, titulo.vencimento()));
    }

    public Pagamento pagarComSaldo(Long contaOrigemId, BigDecimal valor, String descricao) {
        return executar(new PagamentoSaldoConta(contaOrigemId, valor, descricao));
    }

    public Pagamento buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pagamento " + id + " não encontrado"));
    }

    public List<Pagamento> listarPorConta(Long contaOrigemId) {
        return repository.findByContaOrigemIdOrderByIdDesc(contaOrigemId);
    }

    private Pagamento executar(Pagamento pedido) {
        Pagamento p = repository.save(pedido);
        try {
            if (p.usaReserva()) {
                processarComReserva(p);
            } else {
                processarDebitoDireto(p);
            }
        } catch (SaldoInsuficienteException e) {
            finalizar(p, StatusPagamento.RECUSADO_SALDO_INSUFICIENTE, e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            if (p.getStatus() != StatusPagamento.CONFIRMADO_BC) {
                finalizar(p, StatusPagamento.FALHA, e.getMessage());
            }
            throw e;
        }
        return buscar(p.getId());
    }

    private void processarComReserva(Pagamento p) {
        String referencia = "PAGAMENTO-" + p.getId();
        exigirSaldo(p);

        contaService.reservarValor(p.getContaOrigemId(), p.getValor(), referencia);
        atualizar(p, StatusPagamento.VALOR_RESERVADO);

        String protocolo;
        try {
            protocolo = integracao.comunicar(p.operacaoBc(), referencia, requisicao(p));
        } catch (RuntimeException e) {
            contaService.liberarReserva(p.getContaOrigemId(), p.getValor(), referencia); // compensação
            throw e;
        }
        p.setProtocoloBc(protocolo);
        atualizar(p, StatusPagamento.CONFIRMADO_BC);

        contaService.debitar(p.getContaOrigemId(), p.getValor(), referencia, true);
        atualizar(p, StatusPagamento.PAGO);
    }

    private void processarDebitoDireto(Pagamento p) {
        String referencia = "PAGAMENTO-" + p.getId();
        exigirSaldo(p);

        contaService.debitar(p.getContaOrigemId(), p.getValor(), referencia, false);
        String protocolo;
        try {
            protocolo = integracao.comunicar(p.operacaoBc(), referencia, requisicao(p));
        } catch (RuntimeException e) {
            contaService.creditar(p.getContaOrigemId(), p.getValor(), "ESTORNO-" + referencia); // compensação
            throw e;
        }
        p.setProtocoloBc(protocolo);
        atualizar(p, StatusPagamento.CONFIRMADO_BC);
        atualizar(p, StatusPagamento.PAGO);
    }

    private void exigirSaldo(Pagamento p) {
        SaldoConta saldo = contaService.consultarSaldo(p.getContaOrigemId());
        if (saldo.disponivel().compareTo(p.getValor()) < 0) {
            throw new SaldoInsuficienteException(
                    "Saldo disponível R$ " + saldo.disponivel() + " é menor que o valor R$ " + p.getValor());
        }
    }

    private String requisicao(Pagamento p) {
        return "tipo=" + p.getTipo() + ";origem=" + p.getContaOrigemId() + ";valor=" + p.getValor()
                + ";" + p.descricao();
    }

    private void atualizar(Pagamento p, StatusPagamento novoStatus) {
        p.setStatus(novoStatus);
        repository.save(p);
    }

    private void finalizar(Pagamento p, StatusPagamento status, String motivo) {
        p.setMotivoFalha(motivo != null && motivo.length() > 255 ? motivo.substring(0, 255) : motivo);
        atualizar(p, status);
    }
}
