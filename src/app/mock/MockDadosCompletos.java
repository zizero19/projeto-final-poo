package app.mock;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import repository.TurmaRepository;

/**
 * Cenário de teste completo, incluindo pedidos e caixas históricos.
 */
public final class MockDadosCompletos {

    private MockDadosCompletos() {
    }

    public static void popular(
            ProdutoRepository produtoRepository,
            ClienteRepository clienteRepository,
            TurmaRepository turmaRepository,
            PedidoRepository pedidoRepository,
            CaixaRepository caixaRepository) {

        MockDadosBasicos.popular(produtoRepository, clienteRepository, turmaRepository);

        if (!pedidoRepository.listarPedidos().isEmpty() || !caixaRepository.listarCaixas().isEmpty()) {
            return;
        }

        List<Produto> produtos = produtoRepository.listarProdutos();
        List<Cliente> clientes = clienteRepository.listarClientes();

        List<Caixa> caixasFechados = criarCaixasFechados(caixaRepository);
        criarPedidosFinalizados(pedidoRepository, caixaRepository, produtos, clientes, caixasFechados);
        criarPedidosAguardandoPagamento(pedidoRepository, produtos, clientes);

        Caixa caixaAtual = new Caixa();
        caixaRepository.salvarCaixa(caixaAtual);
    }

    private static List<Caixa> criarCaixasFechados(CaixaRepository repository) {
        List<Caixa> caixas = new ArrayList<>();
        LocalDateTime agora = LocalDateTime.now();

        for (int i = 0; i < 6; i++) {
            Caixa caixa = new Caixa();
            caixa.setAberto(false);
            caixa.setAbertura(agora.minusDays(6 - i).withHour(18).withMinute(0).withSecond(0).withNano(0));
            caixa.setFechamento(caixa.getAbertura().plusHours(5));
            repository.salvarCaixa(caixa);
            caixas.add(caixa);
        }
        return caixas;
    }

    private static void criarPedidosFinalizados(
            PedidoRepository pedidoRepository,
            CaixaRepository caixaRepository,
            List<Produto> produtos,
            List<Cliente> clientes,
            List<Caixa> caixas) {

        for (int i = 0; i < 36; i++) {
            Produto produto1 = produtos.get(i % produtos.size());
            Produto produto2 = produtos.get((i + 7) % produtos.size());
            Cliente cliente = (i % 4 == 0) ? clientes.get(i % clientes.size()) : null;

            Pedido pedido = new Pedido(cliente, "Pedido de teste " + (i + 1));
            pedido.setDataHora(caixas.get(i % caixas.size()).getAbertura().plusMinutes(10 + (i * 7L) % 250));
            pedido.setFormaPagamento(cliente != null ? FormaPagamento.FIADO : FormaPagamento.values()[i % 3]);
            pedido.adicionarItem(criarItem(produto1, (i % 3) + 1));
            pedido.adicionarItem(criarItem(produto2, (i % 2) + 1));
            pedido.setPrecoTotal(pedido.calcularTotal());
            pedido.setStatus(StatusPedido.FINALIZADO);

            pedidoRepository.salvarPedido(pedido);

            Caixa caixa = caixas.get(i % caixas.size());
            pedidoRepository.atualizarStatusECaixa(pedido.getId(), StatusPedido.FINALIZADO, caixa.getId());
            caixaRepository.incrementarTotalVendas(caixa.getId(), pedido.getPrecoTotal());
        }
    }

    private static void criarPedidosAguardandoPagamento(
            PedidoRepository pedidoRepository,
            List<Produto> produtos,
            List<Cliente> clientes) {

        for (int i = 0; i < 6; i++) {
            Produto produto = produtos.get((i + 3) % produtos.size());
            Cliente cliente = (i % 2 == 0) ? clientes.get((i + 5) % clientes.size()) : null;

            Pedido pedido = new Pedido(cliente, "Pedido aguardando pagamento " + (i + 1));
            pedido.setFormaPagamento(cliente != null ? FormaPagamento.FIADO : FormaPagamento.PIX);
            pedido.adicionarItem(criarItem(produto, i % 3 + 1));
            pedido.setPrecoTotal(pedido.calcularTotal());
            pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
            pedidoRepository.salvarPedido(pedido);
        }
    }

    private static ItemPedido criarItem(Produto produto, int quantidade) {
        BigDecimal subtotal = produto.getPreco().multiply(BigDecimal.valueOf(quantidade));
        return new ItemPedido(produto, quantidade, subtotal);
    }
}
