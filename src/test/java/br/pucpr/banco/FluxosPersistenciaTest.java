package br.pucpr.banco;

import br.pucpr.banco.autenticacao.Usuario;
import br.pucpr.banco.autenticacao.UsuarioRepository;
import br.pucpr.banco.autenticacao.UsuarioService;
import br.pucpr.banco.compartilhado.FalhaIntegracaoException;
import br.pucpr.banco.compartilhado.SaldoInsuficienteException;
import br.pucpr.banco.conta.Conta;
import br.pucpr.banco.conta.ContaService;
import br.pucpr.banco.conta.MovimentacaoResponse;
import br.pucpr.banco.conta.SaldoConta;
import br.pucpr.banco.conta.TipoMovimentacao;
import br.pucpr.banco.integracaobc.IntegracaoBancoCentralService;
import br.pucpr.banco.integracaobc.MensagemBancoCentralRepository;
import br.pucpr.banco.integracaobc.OperacaoBc;
import br.pucpr.banco.integracaobc.StatusMensagemBc;
import br.pucpr.banco.pagamento.Pagamento;
import br.pucpr.banco.pagamento.PagamentoBoleto;
import br.pucpr.banco.pagamento.PagamentoService;
import br.pucpr.banco.pagamento.StatusPagamento;
import br.pucpr.banco.transferencia.StatusTransferencia;
import br.pucpr.banco.transferencia.Transferencia;
import br.pucpr.banco.transferencia.TransferenciaPix;
import br.pucpr.banco.transferencia.TransferenciaRepository;
import br.pucpr.banco.transferencia.TransferenciaService;
import br.pucpr.banco.transferencia.TransferenciaTed;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes de integração: sobem o Spring de verdade, com um SQLite em memória, e verificam o que
 * foi realmente gravado no banco em cada fluxo dos diagramas de sequência.
 * (Um contexto só: cada teste usa CPFs e contas próprios para não interferir nos outros.)
 */
@SpringBootTest
@TestPropertySource(properties = {
        "banco.seed=false",
        "spring.datasource.url=jdbc:sqlite::memory:",
        "spring.datasource.hikari.max-lifetime=0",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class FluxosPersistenciaTest {

    /** Boleto de R$ 150,00: as posições 10 a 19 do código de barras trazem o valor em centavos. */
    private static final String BOLETO_150 = "001900000" + "0000015000" + "0".repeat(25);

    @Autowired UsuarioService usuarioService;
    @Autowired UsuarioRepository usuarioRepository;
    @Autowired ContaService contaService;
    @Autowired TransferenciaService transferenciaService;
    @Autowired TransferenciaRepository transferenciaRepository;
    @Autowired PagamentoService pagamentoService;
    @Autowired IntegracaoBancoCentralService integracao;
    @Autowired MensagemBancoCentralRepository mensagens;

    private Long novaConta(String cpf, String numero, String saldoInicial) {
        Usuario u = usuarioService.cadastrar("Cliente " + cpf, cpf, "senha");
        Conta c = contaService.abrir(u.getId(), "0001", numero);
        contaService.creditar(c.getId(), new BigDecimal(saldoInicial), "DEPOSITO");
        return c.getId();
    }

    @Test
    void usuarioEhGravadoComSenhaCriptografada() {
        Usuario salvo = usuarioService.cadastrar("Maria", "333.333.333-33", "segredo");

        Usuario lido = usuarioRepository.findById(salvo.getId()).orElseThrow();
        assertThat(lido.getCpf()).isEqualTo("33333333333");
        assertThat(lido.getSenhaHash()).isNotEqualTo("segredo").startsWith("$2");
        assertThat(usuarioService.credenciaisValidas("33333333333", "segredo")).isTrue();
        assertThat(usuarioService.credenciaisValidas("33333333333", "errada")).isFalse();
        assertThatThrownBy(() -> usuarioService.cadastrar("Outra", "33333333333", "x"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void transferenciaPixPersisteTudoEAtualizaSaldo() {
        Long origem = novaConta("44444444444", "1001-1", "1000.00");

        Transferencia t = transferenciaService.transferirPix(origem, new BigDecimal("300.00"), "ana@email.com");

        assertThat(t).isInstanceOf(TransferenciaPix.class);
        assertThat(t.getStatus()).isEqualTo(StatusTransferencia.CONCLUIDA);
        assertThat(t.getProtocoloBc()).startsWith("PIX-");

        SaldoConta saldo = contaService.consultarSaldo(origem);
        assertThat(saldo.saldo()).isEqualByComparingTo("700.00");
        assertThat(saldo.saldoReservado()).isEqualByComparingTo("0.00");

        assertThat(contaService.extrato(origem)).extracting(MovimentacaoResponse::tipo)
                .containsExactly(TipoMovimentacao.CREDITO, TipoMovimentacao.RESERVA, TipoMovimentacao.DEBITO);

        assertThat(mensagens.findByChaveIdempotencia("TRANSFERENCIA-" + t.getId()).orElseThrow().getStatus())
                .isEqualTo(StatusMensagemBc.CONFIRMADA);
    }

    @Test
    void tedEhRecuperadaComoSubclasseComOsDadosDoDestino() {
        Long origem = novaConta("55555555555", "1002-2", "500.00");

        Transferencia t = transferenciaService.transferirTed(origem, new BigDecimal("100.00"),
                "341", "1234", "99999-9", "Fulano de Tal");

        Transferencia lida = transferenciaRepository.findById(t.getId()).orElseThrow();
        assertThat(lida).isInstanceOf(TransferenciaTed.class);
        assertThat(((TransferenciaTed) lida).getFavorecido()).isEqualTo("Fulano de Tal");
        assertThat(lida.getTipo()).isEqualTo("TED");
    }

    @Test
    void saldoInsuficienteRecusaMasMantemORegistroDaTentativa() {
        Long origem = novaConta("66666666666", "1003-3", "100.00");

        assertThatThrownBy(() -> transferenciaService.transferirPix(origem, new BigDecimal("500.00"), "x@y.com"))
                .isInstanceOf(SaldoInsuficienteException.class);

        List<Transferencia> tentativas = transferenciaService.listarPorConta(origem);
        assertThat(tentativas).hasSize(1);
        assertThat(tentativas.get(0).getStatus()).isEqualTo(StatusTransferencia.RECUSADA_SALDO_INSUFICIENTE);

        SaldoConta saldo = contaService.consultarSaldo(origem);
        assertThat(saldo.saldo()).isEqualByComparingTo("100.00");
        assertThat(saldo.saldoReservado()).isEqualByComparingTo("0.00");
    }

    @Test
    void falhaNoBancoCentralLiberaAReservaPorCompensacao() {
        Long origem = novaConta("77777777777", "1004-4", "500.00");

        // a chave "FALHAR" faz o Banco Central simulado ficar indisponível
        assertThatThrownBy(() -> transferenciaService.transferirPix(origem, new BigDecimal("200.00"), "FALHAR"))
                .isInstanceOf(FalhaIntegracaoException.class);

        Transferencia t = transferenciaService.listarPorConta(origem).get(0);
        assertThat(t.getStatus()).isEqualTo(StatusTransferencia.FALHA);

        SaldoConta saldo = contaService.consultarSaldo(origem);
        assertThat(saldo.saldo()).isEqualByComparingTo("500.00");
        assertThat(saldo.saldoReservado()).isEqualByComparingTo("0.00");
        assertThat(contaService.extrato(origem)).extracting(MovimentacaoResponse::tipo)
                .containsExactly(TipoMovimentacao.CREDITO, TipoMovimentacao.RESERVA,
                        TipoMovimentacao.LIBERACAO_RESERVA);
        assertThat(mensagens.findByChaveIdempotencia("TRANSFERENCIA-" + t.getId()).orElseThrow().getStatus())
                .isEqualTo(StatusMensagemBc.FALHA);
    }

    @Test
    void pagamentoDeBoletoBuscaValorNoBancoCentralEPersiste() {
        Long origem = novaConta("88888888888", "1005-5", "200.00");

        Pagamento p = pagamentoService.pagarBoleto(origem, BOLETO_150);

        assertThat(p).isInstanceOf(PagamentoBoleto.class);
        assertThat(p.getStatus()).isEqualTo(StatusPagamento.PAGO);
        assertThat(p.getValor()).isEqualByComparingTo("150.00");
        assertThat(((PagamentoBoleto) p).getVencimento()).isNotNull();
        assertThat(contaService.consultarSaldo(origem).saldo()).isEqualByComparingTo("50.00");
    }

    @Test
    void pagamentoComSaldoEmContaDebitaDiretoSemReserva() {
        Long origem = novaConta("99999999999", "1006-6", "80.00");

        Pagamento p = pagamentoService.pagarComSaldo(origem, new BigDecimal("30.00"), "Mensalidade");

        assertThat(p.getStatus()).isEqualTo(StatusPagamento.PAGO);
        assertThat(contaService.consultarSaldo(origem).saldo()).isEqualByComparingTo("50.00");
        assertThat(contaService.extrato(origem)).extracting(MovimentacaoResponse::tipo)
                .containsExactly(TipoMovimentacao.CREDITO, TipoMovimentacao.DEBITO);
    }

    @Test
    void mesmaChaveDeIdempotenciaNaoGeraSegundaMensagem() {
        String r1 = integracao.comunicar(OperacaoBc.PIX, "CHAVE-TESTE-1", "tipo=PIX");
        String r2 = integracao.comunicar(OperacaoBc.PIX, "CHAVE-TESTE-1", "tipo=PIX");

        assertThat(r2).isEqualTo(r1);
        assertThat(mensagens.findAll().stream()
                .filter(m -> m.getChaveIdempotencia().equals("CHAVE-TESTE-1")).count()).isEqualTo(1);
    }
}
