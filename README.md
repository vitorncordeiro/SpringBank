# Projeto Bancário – Persistência com Spring Data JPA + SQLite

Implementação da persistência das classes do projeto bancário (diagramas de pacotes, sequência e implantação das atividades anteriores) usando Spring Boot 3, Spring Data JPA (Hibernate) e SQLite.

## O que é persistido

Cada pacote do diagrama virou um pacote Java com suas próprias entidades:

- **Autenticação:** `Usuario`, com a senha guardada apenas como hash BCrypt.
- **Conta:** `Conta` (saldo, saldo reservado e controle de concorrência com `@Version`) e `Movimentacao` (extrato).
- **Transferência:** `Transferencia`, com as subclasses `TransferenciaPix`, `TransferenciaTed` e `TransferenciaDoc`, todas na mesma tabela (herança `SINGLE_TABLE`).
- **Pagamento:** `Pagamento`, com as subclasses `PagamentoPix`, `PagamentoBoleto` e `PagamentoSaldoConta`, também em tabela única.
- **Integração Banco Central:** `MensagemBancoCentral`, o log das mensagens trocadas com o Banco Central, com chave de idempotência única.

Não são persistidos: Cliente, API Gateway, os adaptadores (não guardam estado), o Banco Central (sistema externo), o HSM e as sessões/tokens (que no diagrama de implantação ficam no Redis).

## Endpoints

Os endpoints seguem os diagramas de sequência:

- Consultar saldo: `GET /contas/{id}/saldo`
- Transferências: `POST /transferencias/pix`, `/ted` e `/doc`
- Pagamentos: `POST /pagamentos/pix`, `/boleto` e `/saldo`
- Apoio: cadastro de usuário e conta, depósito, extrato, consulta de transferências e pagamentos, e `GET /integracao/mensagens` (log do Banco Central).

A validação do token (`validarToken()`) não foi implementada, pois o foco do trabalho é a persistência.

## Roteiro de demonstração

1. Saldo inicial da Ana: `GET /contas/1/saldo`
2. Transferência PIX de R$ 300 da Ana para o Bruno: `POST /transferencias/pix`
3. Conferir o que foi gravado: `GET /contas/1/saldo`, `GET /contas/1/extrato` (CREDITO, RESERVA e DEBITO) e `GET /integracao/mensagens`
4. Saldo insuficiente: `POST /transferencias/pix` com valor acima do saldo. Retorna 422, mas a tentativa fica registrada em `GET /transferencias/conta/1`
5. Falha no Banco Central: `POST /transferencias/pix` com a chave PIX `FALHAR`. A reserva é devolvida, o que aparece em `GET /contas/1/extrato`
6. TED: `POST /transferencias/ted`, que grava na mesma tabela de transferências com outra subclasse
7. Boleto de R$ 150,00: `POST /pagamentos/boleto` com o código de barras `00190000000000150000000000000000000000000000`
8. Pagamento com saldo em conta: `POST /pagamentos/saldo`, com débito direto e sem reserva

Para olhar o banco diretamente, abra o `banco.db` com o `sqlite3` ou com o DB Browser for SQLite.

## Decisões de projeto

- Um só projeto Spring com um pacote por microsserviço. Entre pacotes a referência é apenas por ID (`usuarioId`, `contaOrigemId`), sem chave estrangeira, respeitando o database-per-service do diagrama de implantação.
- Herança `SINGLE_TABLE` para os tipos de transferência e de pagamento.
- Saldo com reserva (`saldo` e `saldo_reservado`), espelhando `reservarValor()`, comunicação com o Banco Central e `debitar()` das sequências.
- Cada passo do fluxo é uma transação curta. Se o Banco Central falhar depois da reserva, a reserva é devolvida (compensação) e a operação fica com status `FALHA`.
- O Banco Central é simulado por `AdaptadorSimulado`, pois o foco é a persistência.

## Limitações

- O SQLite guarda valores decimais como ponto flutuante. Em produção o ideal é PostgreSQL, como no diagrama de implantação.
- O `ddl-auto=update` é prático para o trabalho, mas em produção seriam usadas migrações versionadas (Flyway). Se um enum ganhar novo valor, apague o `banco.db`.
- O SQLite aceita um escritor por vez, por isso o pool de conexões tem tamanho 1.