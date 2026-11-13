package br.pucpr.banco.transferencia;

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
@RequestMapping("/transferencias")
public class TransferenciaController {

    public record PixRequest(@NotNull Long contaOrigemId, @NotNull @Positive BigDecimal valor,
                             @NotBlank String chavePix) {
    }

    public record BancariaRequest(@NotNull Long contaOrigemId, @NotNull @Positive BigDecimal valor,
                                  @NotBlank String banco, @NotBlank String agencia, @NotBlank String conta,
                                  @NotBlank String favorecido) {
    }

    public record TransferenciaResponse(Long id, String tipo, Long contaOrigemId, BigDecimal valor,
                                        StatusTransferencia status, String comprovante, String motivoFalha,
                                        LocalDateTime criadaEm, LocalDateTime atualizadaEm) {
        static TransferenciaResponse de(Transferencia t) {
            return new TransferenciaResponse(t.getId(), t.getTipo(), t.getContaOrigemId(), t.getValor(),
                    t.getStatus(), t.getProtocoloBc(), t.getMotivoFalha(), t.getCriadaEm(), t.getAtualizadaEm());
        }
    }

    private final TransferenciaService service;

    public TransferenciaController(TransferenciaService service) {
        this.service = service;
    }

    @PostMapping("/pix")
    @ResponseStatus(HttpStatus.CREATED)
    public TransferenciaResponse pix(@Valid @RequestBody PixRequest r) {
        return TransferenciaResponse.de(service.transferirPix(r.contaOrigemId(), r.valor(), r.chavePix()));
    }

    @PostMapping("/ted")
    @ResponseStatus(HttpStatus.CREATED)
    public TransferenciaResponse ted(@Valid @RequestBody BancariaRequest r) {
        return TransferenciaResponse.de(service.transferirTed(r.contaOrigemId(), r.valor(), r.banco(),
                r.agencia(), r.conta(), r.favorecido()));
    }

    @PostMapping("/doc")
    @ResponseStatus(HttpStatus.CREATED)
    public TransferenciaResponse doc(@Valid @RequestBody BancariaRequest r) {
        return TransferenciaResponse.de(service.transferirDoc(r.contaOrigemId(), r.valor(), r.banco(),
                r.agencia(), r.conta(), r.favorecido()));
    }

    @GetMapping("/{id}")
    public TransferenciaResponse buscar(@PathVariable Long id) {
        return TransferenciaResponse.de(service.buscar(id));
    }

    @GetMapping("/conta/{contaId}")
    public List<TransferenciaResponse> porConta(@PathVariable Long contaId) {
        return service.listarPorConta(contaId).stream().map(TransferenciaResponse::de).toList();
    }
}
