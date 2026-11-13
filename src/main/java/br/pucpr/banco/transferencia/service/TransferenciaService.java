package br.pucpr.banco.transferencia.service;

import br.pucpr.banco.compartilhado.RecursoNaoEncontradoException;
import br.pucpr.banco.compartilhado.SaldoInsuficienteException;
import br.pucpr.banco.conta.dto.SaldoConta;
import br.pucpr.banco.conta.service.ContaService;
import br.pucpr.banco.integracaobc.service.IntegracaoBancoCentralService;
import br.pucpr.banco.transferencia.model.Transferencia;
import br.pucpr.banco.transferencia.model.TransferenciaDoc;
import br.pucpr.banco.transferencia.model.TransferenciaPix;
import br.pucpr.banco.transferencia.model.TransferenciaTed;
import br.pucpr.banco.transferencia.enums.StatusTransferencia;
import br.pucpr.banco.transferencia.repository.TransferenciaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransferenciaService {

    private final TransferenciaRepository repository;
    private final ContaService contaService;
    private final IntegracaoBancoCentralService integracao;

    public TransferenciaService(TransferenciaRepository repository, ContaService contaService,
                               IntegracaoBancoCentralService integracao) {
        this.repository = repository;
        this.contaService = contaService;
        this.integracao = integracao;
    }

    public Transferencia transferirPix(Long contaOrigemId, BigDecimal valor, String chavePix) {
        return executar(new TransferenciaPix(contaOrigemId, valor, chavePix));
    }

    public Transferencia transferirTed(Long contaOrigemId, BigDecimal valor, String banco, String agencia,
                                       String conta, String favorecido) {
        return executar(new TransferenciaTed(contaOrigemId, valor, banco, agencia, conta, favorecido));
    }

    public Transferencia transferirDoc(Long contaOrigemId, BigDecimal valor, String banco, String agencia,
                                       String conta, String favorecido) {
        return executar(new TransferenciaDoc(contaOrigemId, valor, banco, agencia, conta, favorecido));
    }

    public Transferencia buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Transferência " + id + " não encontrada"));
    }

    public List<Transferencia> listarPorConta(Long contaOrigemId) {
        return repository.findByContaOrigemIdOrderByIdDesc(contaOrigemId);
    }

    private Transferencia executar(Transferencia pedido) {
        Transferencia t = repository.save(pedido);
        try {
            processar(t);
        } catch (SaldoInsuficienteException e) {
            finalizar(t, StatusTransferencia.RECUSADA_SALDO_INSUFICIENTE, e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            if (t.getStatus() != StatusTransferencia.CONFIRMADA_BC) {
                finalizar(t, StatusTransferencia.FALHA, e.getMessage());
            }
            throw e;
        }
        return buscar(t.getId());
    }

    private void processar(Transferencia t) {
        String referencia = "TRANSFERENCIA-" + t.getId();

        SaldoConta saldo = contaService.consultarSaldo(t.getContaOrigemId());
        if (saldo.disponivel().compareTo(t.getValor()) < 0) {
            throw new SaldoInsuficienteException(
                    "Saldo disponível R$ " + saldo.disponivel() + " é menor que o valor R$ " + t.getValor());
        }

        contaService.reservarValor(t.getContaOrigemId(), t.getValor(), referencia);
        atualizar(t, StatusTransferencia.VALOR_RESERVADO);

        String protocolo;
        try {
            String requisicao = "tipo=" + t.getTipo() + ";origem=" + t.getContaOrigemId()
                    + ";valor=" + t.getValor() + ";" + t.descricaoDestino();
            protocolo = integracao.comunicar(t.operacaoBc(), referencia, requisicao);
        } catch (RuntimeException e) {
            contaService.liberarReserva(t.getContaOrigemId(), t.getValor(), referencia);
            throw e;
        }
        t.setProtocoloBc(protocolo);
        atualizar(t, StatusTransferencia.CONFIRMADA_BC);

        contaService.debitar(t.getContaOrigemId(), t.getValor(), referencia, true);
        atualizar(t, StatusTransferencia.CONCLUIDA);
    }

    private void atualizar(Transferencia t, StatusTransferencia novoStatus) {
        t.setStatus(novoStatus);
        repository.save(t);
    }

    private void finalizar(Transferencia t, StatusTransferencia status, String motivo) {
        t.setMotivoFalha(motivo != null && motivo.length() > 255 ? motivo.substring(0, 255) : motivo);
        atualizar(t, status);
    }
}
