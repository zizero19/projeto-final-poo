package service;

import java.math.BigDecimal;
import java.util.List;

import model.Pedido;
import repository.CaixaRepository;
import repository.ClienteRepository;
import repository.PedidoRepository;
import repository.ProdutoRepository;

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

    public Pedido salvarPedido(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido não pode ser nulo.");
        }
        pedidoRepository.salvarPedido(pedido);
        return pedido;
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.listarPedidos();
    }

    public Pedido buscarPorId(Long id) {
        return pedidoRepository.buscarPorId(id);
    }

    public List<Pedido> buscarPedidosPorCpfDeCliente(String cpf) {
        return pedidoRepository.buscarPedidosPorCpfDeCliente(cpf);
    }

    public boolean removerPedido(Long id) {
        pedidoRepository.removerPedido(id);
        return true;
    }

    public BigDecimal calcularSaldoDevedor(String cpf) {
        return pedidoRepository.calcularSaldoDevedor(cpf);
    }

    // Mantidas como dependências para as próximas regras de negócio do pedido.
    public ClienteRepository getClienteRepository() {
        return clienteRepository;
    }

    public ProdutoRepository getProdutoRepository() {
        return produtoRepository;
    }

    public CaixaRepository getCaixaRepository() {
        return caixaRepository;
    }
}
