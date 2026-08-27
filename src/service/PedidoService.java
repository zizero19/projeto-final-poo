package service;

import java.math.BigDecimal;
import java.util.List;

import model.Caixa;
import model.Cliente;
import model.ItemPedido;
import model.Pedido;
import model.Produto;
import model.enums.FormaPagamento;
import model.enums.StatusPedido;
import repository.CaixaRepository;
import repository.ClienteRepository;
import repository.PedidoRepository;
import repository.ProdutoRepository;

/**
 * Camada de negócio do Pedido.
 *
 * O Pedido não é tratado como CRUD: existe um fluxo (criar -> aguardando
 * pagamento -> confirmar pagamento/cancelar) e cada etapa tem suas próprias
 * regras, concentradas aqui para não depender da View.
 */
public class PedidoService {

    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final CaixaRepository caixaRepository;
    private final PedidoRepository pedidoRepository;

    public PedidoService(
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository,
            CaixaRepository caixaRepository,
            PedidoRepository pedidoRepository) {
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.caixaRepository = caixaRepository;
        this.pedidoRepository = pedidoRepository;
    }

    /**
     * Valida e monta um item a ser adicionado ao pedido em construção.
     * Não mexe no estoque real ainda (isso só acontece ao criar o pedido de
     * fato) para não "perder" estoque caso o usuário desista do pedido.
     */
    public boolean existeCaixaAberto() {
        return caixaRepository.buscarCaixaAberto() != null;
    }

    public ItemPedido criarItem(Produto produto, int quantidade, int quantidadeJaReservadaDoProduto) {
        if (produto == null) {
            throw new RegraNegocioException("Selecione um produto.");
        }
        if (quantidade <= 0) {
            throw new RegraNegocioException("A quantidade deve ser maior que zero.");
        }

        int disponivel = produto.getQtdEstoque() - quantidadeJaReservadaDoProduto;
        if (quantidade > disponivel) {
            throw new RegraNegocioException(
                    "Estoque insuficiente para \"" + produto.getNome() + "\". Disponível: " + disponivel + ".");
        }

        return new ItemPedido(produto, quantidade, produto.getPreco().multiply(BigDecimal.valueOf(quantidade)));
    }

    /**
     * Cria o pedido de fato: valida os dados, resolve o cliente (quando
     * FIADO), debita o estoque real dos produtos e persiste o pedido já
     * como AGUARDANDO_PAGAMENTO.
     */
    public Pedido criarPedido(List<ItemPedido> itens, FormaPagamento formaPagamento, String cpfCliente) {
        if (itens == null || itens.isEmpty()) {
            throw new RegraNegocioException("Adicione ao menos um item ao pedido.");
        }
        if (formaPagamento == null) {
            throw new RegraNegocioException("Selecione a forma de pagamento.");
        }

        Caixa caixaAberto = caixaRepository.buscarCaixaAberto();
        if (caixaAberto == null) {
            throw new RegraNegocioException(
                    "Não há caixa aberto. Abra o caixa antes de registrar um pedido.");
        }

        Cliente cliente = null;
        if (formaPagamento == FormaPagamento.FIADO) {
            if (cpfCliente == null || cpfCliente.isBlank()) {
                throw new RegraNegocioException("Informe o CPF do cliente para pedidos no fiado.");
            }
            cliente = clienteRepository.buscarPorCpf(cpfCliente.trim())
                    .orElseThrow(() -> new RegraNegocioException(
                            "Nenhum cliente encontrado com o CPF informado."));
        }

        debitarEstoque(itens);

        Pedido pedido = new Pedido(cliente, "");
        pedido.setItens(itens);
        pedido.setFormaPagamento(formaPagamento);
        pedido.setPrecoTotal(pedido.calcularTotal());
        pedido.cobrarPedido();

        pedidoRepository.salvarPedido(pedido, caixaAberto.getId());

        // FIADO já representa uma venda registrada neste caixa, mesmo que o
        // pagamento ainda esteja pendente. O valor só será lançado uma vez.
        if (formaPagamento == FormaPagamento.FIADO) {
            caixaRepository.incrementarTotalVendas(caixaAberto.getId(), pedido.getPrecoTotal());
            cliente.adicionarDivida(pedido.getPrecoTotal());
            clienteRepository.atualizarSaldoDevedor(cliente.getCpf(), cliente.getSaldoDevedor());
        }

        return pedido;
    }

    private void debitarEstoque(List<ItemPedido> itens) {
        for (ItemPedido item : itens) {
            Produto produtoAtual = produtoRepository.buscarProduto(item.getProduto().getId());
            if (produtoAtual == null) {
                throw new RegraNegocioException("O produto \"" + item.getProduto().getNome()
                        + "\" não está mais disponível.");
            }
            if (item.getQuantidade() > produtoAtual.getQtdEstoque()) {
                throw new RegraNegocioException("Estoque insuficiente para \"" + produtoAtual.getNome() + "\".");
            }
            produtoAtual.diminuirEstoque(item.getQuantidade());
            produtoRepository.atualizarProduto(produtoAtual);
        }
    }

    private void restaurarEstoque(Pedido pedido) {
        if (pedido.getItens() == null) {
            return;
        }
        for (ItemPedido item : pedido.getItens()) {
            if (item.getProduto() == null) {
                continue;
            }
            Produto produtoAtual = produtoRepository.buscarProduto(item.getProduto().getId());
            if (produtoAtual == null) {
                continue;
            }
            produtoAtual.aumentarEstoque(item.getQuantidade());
            produtoRepository.atualizarProduto(produtoAtual);
        }
    }

    /**
     * Confirma o pagamento de um pedido AGUARDANDO_PAGAMENTO. Exige que
     * exista um caixa aberto, já que o pedido passa a ficar associado a ele.
     */
    public Pedido confirmarPagamento(Long pedidoId) {
        Pedido pedido = buscarPorId(pedidoId);
        if (pedido == null) {
            throw new RegraNegocioException("Pedido não encontrado.");
        }
        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new RegraNegocioException("Somente pedidos aguardando pagamento podem ser confirmados.");
        }

        Caixa caixaAberto = caixaRepository.buscarCaixaAberto();
        if (caixaAberto == null) {
            throw new RegraNegocioException(
                    "Não há caixa aberto no momento. Abra o caixa antes de confirmar pagamentos.");
        }

        if (pedido.getFormaPagamento() == FormaPagamento.FIADO) {
            registrarPagamentoFiado(pedidoId, pedido.getSaldoDevedor());
            return buscarPorId(pedidoId);
        }

        pedido.finalizarPedido();
        pedidoRepository.atualizarStatusECaixa(pedido.getId(), StatusPedido.FINALIZADO, caixaAberto.getId());
        caixaRepository.incrementarTotalVendas(caixaAberto.getId(), pedido.getPrecoTotal());
        return pedido;
    }

    /**
     * Registra um pagamento parcial ou total da dívida do cliente. O valor é
     * distribuído pelos pedidos FIADO mais antigos primeiro.
     */
    public void quitarSaldoDevedor(String cpf, BigDecimal valorPagamento) {
        if (cpf == null || cpf.isBlank()) {
            throw new RegraNegocioException("CPF do cliente deve ser informado.");
        }
        if (valorPagamento == null || valorPagamento.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O valor do pagamento deve ser maior que zero.");
        }

        Caixa caixaAberto = caixaRepository.buscarCaixaAberto();
        if (caixaAberto == null) {
            throw new RegraNegocioException("É necessário ter um caixa aberto para registrar um pagamento.");
        }

        Cliente cliente = clienteRepository.buscarPorCpf(cpf)
                .orElseThrow(() -> new RegraNegocioException("Cliente não encontrado."));

        BigDecimal saldoCliente = cliente.getSaldoDevedor();
        if (valorPagamento.compareTo(saldoCliente) > 0) {
            throw new RegraNegocioException("O pagamento não pode ser maior que o saldo devedor de "
                    + util.FormatacaoUtil.formatarMoeda(saldoCliente) + ".");
        }

        BigDecimal restante = valorPagamento.setScale(2, java.math.RoundingMode.HALF_UP);
        List<Pedido> fiados = pedidoRepository.buscarPedidosFiadoEmAbertoPorCliente(cpf);

        for (Pedido pedido : fiados) {
            if (restante.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal saldoPedido = pedido.getSaldoDevedor();
            BigDecimal pagamentoPedido = restante.min(saldoPedido);
            pedido.registrarPagamento(pagamentoPedido);
            pedidoRepository.atualizarValorPago(pedido.getId(), pedido.getValorPago());

            if (pedido.getSaldoDevedor().compareTo(BigDecimal.ZERO) == 0) {
                pedidoRepository.atualizarStatusECaixa(pedido.getId(), StatusPedido.FINALIZADO,
                        pedidoRepository.buscarCaixaIdDoPedido(pedido.getId()));
            }

            restante = restante.subtract(pagamentoPedido);
        }

        cliente.registrarPagamento(valorPagamento);
        clienteRepository.atualizarSaldoDevedor(cpf, cliente.getSaldoDevedor());
    }

    /**
     * Registra o pagamento restante de um único pedido FIADO.
     */
    public void registrarPagamentoFiado(Long pedidoId, BigDecimal valorPagamento) {
        if (pedidoId == null) {
            throw new RegraNegocioException("Pedido deve ser informado.");
        }
        if (valorPagamento == null || valorPagamento.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O valor do pagamento deve ser maior que zero.");
        }

        Caixa caixaAberto = caixaRepository.buscarCaixaAberto();
        if (caixaAberto == null) {
            throw new RegraNegocioException("É necessário ter um caixa aberto para registrar um pagamento.");
        }

        Pedido pedido = buscarPorId(pedidoId);
        if (pedido == null || pedido.getFormaPagamento() != FormaPagamento.FIADO) {
            throw new RegraNegocioException("Pedido FIADO não encontrado.");
        }
        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new RegraNegocioException("O pedido FIADO não está em aberto.");
        }
        if (valorPagamento.compareTo(pedido.getSaldoDevedor()) > 0) {
            throw new RegraNegocioException("O pagamento não pode ser maior que o saldo do pedido.");
        }

        Cliente cliente = pedido.getCliente();
        if (cliente == null) {
            throw new RegraNegocioException("O pedido FIADO não possui cliente associado.");
        }

        pedido.registrarPagamento(valorPagamento);
        pedidoRepository.atualizarValorPago(pedido.getId(), pedido.getValorPago());

        cliente.registrarPagamento(valorPagamento);
        clienteRepository.atualizarSaldoDevedor(cliente.getCpf(), cliente.getSaldoDevedor());

        if (pedido.getSaldoDevedor().compareTo(BigDecimal.ZERO) == 0) {
            pedido.finalizarPedido();
            pedidoRepository.atualizarStatusECaixa(pedido.getId(), StatusPedido.FINALIZADO,
                    pedidoRepository.buscarCaixaIdDoPedido(pedido.getId()));
        }
    }

    /**
     * Cancela um pedido que ainda não foi finalizado e devolve os produtos
     * ao estoque.
     */
    public Pedido cancelarPedido(Long pedidoId) {
        Pedido pedido = buscarPorId(pedidoId);
        if (pedido == null) {
            throw new RegraNegocioException("Pedido não encontrado.");
        }
        if (pedido.getStatus() == StatusPedido.FINALIZADO || pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new RegraNegocioException("Não é possível cancelar um pedido que já foi finalizado ou cancelado.");
        }

        restaurarEstoque(pedido);

        if (pedido.getFormaPagamento() == FormaPagamento.FIADO) {
            Long caixaId = obterCaixaDoPedido(pedido.getId());
            BigDecimal saldoPedido = pedido.getSaldoDevedor();
            caixaRepository.decrementarTotalVendas(caixaId, pedido.getPrecoTotal());
            pedido.cancelarPedido();
            pedidoRepository.atualizarStatusECaixa(pedido.getId(), StatusPedido.CANCELADO, caixaId);

            if (pedido.getCliente() != null && saldoPedido.compareTo(BigDecimal.ZERO) > 0) {
                Cliente cliente = clienteRepository.buscarPorCpf(pedido.getCliente().getCpf()).orElse(null);
                if (cliente != null) {
                    cliente.registrarPagamento(saldoPedido);
                    clienteRepository.atualizarSaldoDevedor(cliente.getCpf(), cliente.getSaldoDevedor());
                }
            }
        } else {
            pedido.cancelarPedido();
            // Mantém a associação com o caixa para preservar o histórico.
            Long caixaId = pedidoRepository.buscarCaixaIdDoPedido(pedido.getId());
            pedidoRepository.atualizarStatusECaixa(pedido.getId(), StatusPedido.CANCELADO, caixaId);
        }

        return pedido;
    }

    private Long obterCaixaDoPedido(Long pedidoId) {
        Long caixaId = pedidoRepository.buscarCaixaIdDoPedido(pedidoId);
        if (caixaId == null) {
            throw new RegraNegocioException("O pedido FIADO não está associado a nenhum caixa.");
        }
        return caixaId;
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.listarPedidos();
    }

    public List<Pedido> listarPedidosAguardandoPagamento() {
        return pedidoRepository.listarPedidos().stream()
                .filter(p -> p.getStatus() == StatusPedido.AGUARDANDO_PAGAMENTO
                        && p.getFormaPagamento() != FormaPagamento.FIADO)
                .toList();
    }

    public List<Pedido> listarPedidosFiadoEmAberto() {
        return pedidoRepository.listarPedidos().stream()
                .filter(p -> p.getFormaPagamento() == FormaPagamento.FIADO
                        && p.getStatus() != StatusPedido.FINALIZADO
                        && p.getStatus() != StatusPedido.CANCELADO)
                .toList();
    }

    public List<Pedido> listarPedidosFinalizados() {
        return pedidoRepository.listarPedidos().stream()
                .filter(p -> p.getStatus() == StatusPedido.FINALIZADO)
                .toList();
    }

    public Pedido buscarPorId(Long id) {
        return pedidoRepository.buscarPorId(id);
    }

    public List<Pedido> buscarPedidosPorCpfDeCliente(String cpf) {
        return pedidoRepository.buscarPedidosPorCpfDeCliente(cpf);
    }

    public List<Pedido> listarPedidosDoCaixa(Long caixaId) {
        return pedidoRepository.buscarPedidosPorCaixa(caixaId);
    }

    public boolean removerPedido(Long id) {
        pedidoRepository.removerPedido(id);
        return true;
    }

    public BigDecimal calcularSaldoDevedor(String cpf) {
        Cliente cliente = clienteRepository.buscarPorCpf(cpf).orElse(null);
        return cliente == null ? BigDecimal.ZERO : cliente.getSaldoDevedor();
    }
}
