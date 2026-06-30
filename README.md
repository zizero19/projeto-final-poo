
<img width="956" height="802" alt="{2C763E19-6170-48F4-A435-0725957AA7FE}" src="https://github.com/user-attachments/assets/163f7619-e8c6-452c-8253-80c68e0b54c8" />

# 📚 Classes do Projeto

## Pessoa

Classe abstrata que representa qualquer pessoa cadastrada no sistema. Ela serve como base para clientes e funcionários, armazenando informações comuns.

### Atributos
- `nome: String`
- `cpf: String`
- `email: String`

### Métodos
- `validarCpf(): boolean`  
  Verifica se o CPF informado é válido.

- `exibirDados(): String`  
  Retorna as informações da pessoa em formato de texto.

---

## Funcionário

Herda da classe **Pessoa** e representa o colaborador responsável pelo atendimento da cantina.

### Atributos
- `cargo: String`
- `turno: String`

### Métodos
- `atenderPedido(): void`  
  Inicia o atendimento de um novo pedido.

- `cancelarPedido(): void`  
  Cancela um pedido quando necessário.

- `fecharCaixa(): void`  
  Realiza o fechamento do caixa ao final do expediente.

---

## Cliente

Herda da classe **Pessoa** e representa o consumidor da cantina que realiza pedidos em fiado.

### Atributos
- `matricula: String`
- `telefone: String`

### Métodos
- `consultarHistorico(): void`  
  Exibe o histórico de compras realizadas pelo cliente.

---

## Produto

Representa um item comercializado pela cantina.

### Atributos
- `id: int`
- `nome: String`
- `categoria: String`
- `preco: double`
- `quantidadeEstoque: int`

### Métodos
- `diminuirEstoque(): void`  
  Reduz a quantidade disponível após uma venda.

- `aumentarEstoque(): void`  
  Incrementa a quantidade disponível após reposição.

- `possuiEstoque(): boolean`  
  Verifica se há unidades disponíveis para venda.

- `atualizarPreco(): void`  
  Atualiza o preço do produto.

---

## ItemPedido

Representa um produto adicionado a um pedido, juntamente com sua quantidade.

### Atributos
- `produto: Produto`
- `quantidade: int`
- `subtotal: double`

### Métodos
- `calcularSubtotal(): double`  
  Calcula o valor total do item com base na quantidade e no preço do produto.

---

## Pedido

Representa uma compra realizada por um cliente.

### Atributos
- `numero: int`
- `cliente: Cliente`
- `funcionario: Funcionario`
- `itens: List<ItemPedido>`
- `horario: LocalDateTime`
- `status: StatusPedido`

### Métodos
- `adicionarItem(): void`  
  Adiciona um produto ao pedido.

- `removerItem(): void`  
  Remove um produto do pedido.

- `calcularTotal(): double`  
  Calcula o valor total do pedido.

- `finalizarPedido(): void`  
  Finaliza o pedido e registra a venda.

- `cancelarPedido(): void`  
  Cancela o pedido antes da conclusão da venda.

---

## Estoque

Responsável pelo gerenciamento dos produtos disponíveis para venda.

### Atributos
- `produtos: List<Produto>`

### Métodos
- `adicionarProduto(): void`  
  Adiciona um novo produto ao estoque.

- `removerProduto(): void`  
  Remove um produto do estoque.

- `buscarProduto(): Produto`  
  Localiza um produto cadastrado.

- `verificarDisponibilidade(): boolean`  
  Verifica se um produto possui estoque suficiente.

- `atualizarQuantidade(): void`  
  Atualiza a quantidade disponível de um produto.

---

## Caixa

Representa o controle financeiro da cantina durante o expediente.

### Atributos
- `pedidos: List<Pedido>`
- `totalVendas: double`
- `aberto: boolean`

### Métodos
- `abrirCaixa(): void`  
  Inicia as operações do caixa.

- `registrarVenda(): void`  
  Registra uma venda realizada.

- `calcularFaturamento(): double`  
  Calcula o valor total arrecadado.

- `fecharCaixa(): void`  
  Finaliza as operações do caixa e gera o fechamento diário.
