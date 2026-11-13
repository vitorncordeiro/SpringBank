package br.pucpr.banco.pagamento;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    public record PixRequest(@NotNull Long contaOrigemId, @NotNull @Positive BigDecimal valor,
                             @NotBlank String codigoQr) {
    }

    public record BoletoRequest(@NotNull Long contaOrigemId, @NotBlank String codigoBarras) {
    }

    public record SaldoRequest(@NotNull Long contaOrigemId, @NotNull @Positive BigDecimal valor,
                               @NotBlank String descricao) {
    }

    public record PagamentoResponse(Long id, String tipo, Long contaOrigemId, BigDecimal valor,
                                    StatusPagamento status, String comprovante, String motivoFalha,
                                    LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        static PagamentoResponse de(Pagamento p) {
            return new PagamentoResponse(p.getId(), p.getTipo(), p.getContaOrigemId(), p.getValor(),
                    p.getStatus(), p.getProtocoloBc(), p.getMotivoFalha(), p.getCriadoEm(), p.getAtualizadoEm());
        }
    }

    private final PagamentoService service;

    public PagamentoController(PagamentoService service) {
        this.service = service;
    }

    @PostMapping("/pix")
    @ResponseStatus(HttpStatus.CREATED)
    public PagamentoResponse pix(@Valid @RequestBody PixRequest r) {
        return PagamentoResponse.de(service.pagarPix(r.contaOrigemId(), r.valor(), r.codigoQr()));
    }

    @PostMapping("/boleto")
    @ResponseStatus(HttpStatus.CREATED)
    public PagamentoResponse boleto(@Valid @RequestBody BoletoRequest r) {
        return PagamentoResponse.de(service.pagarBoleto(r.contaOrigemId(), r.codigoBarras()));
    }

    @PostMapping("/saldo")
    @ResponseStatus(HttpStatus.CREATED)
    public PagamentoResponse saldo(@Valid @RequestBody SaldoRequest r) {
        return PagamentoResponse.de(service.pagarComSaldo(r.contaOrigemId(), r.valor(), r.descricao()));
    }

    @GetMapping("/{id}")
    public PagamentoResponse buscar(@PathVariable Long id) {
        return PagamentoResponse.de(service.buscar(id));
    }

    @GetMapping("/conta/{contaId}")
    public List<PagamentoResponse> porConta(@PathVariable Long contaId) {
        return service.listarPorConta(contaId).stream().map(PagamentoResponse::de).toList();
    }
}
