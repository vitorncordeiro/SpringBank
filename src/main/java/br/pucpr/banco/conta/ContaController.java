package br.pucpr.banco.conta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/contas")
public class ContaController {

    public record AbrirContaRequest(@NotNull Long usuarioId, @NotBlank String agencia, @NotBlank String numero) {
    }

    public record DepositoRequest(@NotNull @Positive BigDecimal valor) {
    }

    public record ContaResponse(Long id, Long usuarioId, String agencia, String numero,
                                BigDecimal saldo, BigDecimal saldoReservado, BigDecimal saldoDisponivel) {
        static ContaResponse de(Conta c) {
            return new ContaResponse(c.getId(), c.getUsuarioId(), c.getAgencia(), c.getNumero(),
                    c.getSaldo(), c.getSaldoReservado(), c.getSaldoDisponivel());
        }
    }

    private final ContaService service;

    public ContaController(ContaService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContaResponse abrir(@Valid @RequestBody AbrirContaRequest req) {
        return ContaResponse.de(service.abrir(req.usuarioId(), req.agencia(), req.numero()));
    }

    /** Sequência 2: GET /contas/{id}/saldo */
    @GetMapping("/{id}/saldo")
    public SaldoConta saldo(@PathVariable Long id) {
        return service.consultarSaldo(id);
    }

    @PostMapping("/{id}/depositos")
    @ResponseStatus(HttpStatus.CREATED)
    public SaldoConta depositar(@PathVariable Long id, @Valid @RequestBody DepositoRequest req) {
        return service.creditar(id, req.valor(), "DEPOSITO");
    }

    @GetMapping("/{id}/extrato")
    public List<MovimentacaoResponse> extrato(@PathVariable Long id) {
        return service.extrato(id);
    }
}
