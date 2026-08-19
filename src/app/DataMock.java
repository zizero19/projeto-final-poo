package app;

import java.util.ArrayList;
import java.util.List;

import model.Caixa;
import model.Cliente;
import model.ItemPedido;
import model.Pedido;
import model.Produto;
import model.Turma;
import model.enums.CategoriaProduto;
import model.enums.DiaSemana;
import model.enums.FormaPagamento;
import model.enums.Turno;

/**
 * Classe responsável por inserir dados de teste (mock) nos repositórios do
 * sistema de cantina escolar, simulando um banco de dados populado.
 *
 * Regras de negócio respeitadas:
 * - Pedido SEM cliente -> forma de pagamento normal (DINHEIRO, PIX ou
 * CARTÃO).
 * - Pedido COM cliente -> forma de pagamento FIADO, cliente fica marcado
 * como devendo (isDevendo = true) e o pedido entra no histórico dele.
 *
 * Uso: no App.java, logo após criar o Contexto, chame:
 * DataMock.popular(contexto);
 */
public class DataMock {

    public static void popular(Contexto contexto) {
        List<Turma> turmas = popularTurmas(contexto);
        List<Cliente> clientes = popularClientes(contexto, turmas);
        List<Produto> produtos = popularProdutos(contexto);
        Caixa caixa = abrirCaixa(contexto);

        popularPedidos(contexto, caixa, clientes, produtos);

        System.out.println("Dados de teste inseridos com sucesso!");
    }

    // ===================== TURMAS =====================
    private static List<Turma> popularTurmas(Contexto contexto) {
        List<Turma> turmas = new ArrayList<>();

        turmas.add(new Turma("Análise e Desenvolvimento de Sistemas", 32, Turno.NOTURNO, true, diasUteis()));
        turmas.add(new Turma("Engenharia de Software", 28, Turno.VESPERTINO, true, diasUteis()));
        turmas.add(new Turma("Sistemas da Informação", 25, Turno.MATUTINO, true, diasUteis()));
        turmas.add(new Turma("Redes de Computador", 30, Turno.VESPERTINO, true, diasUteis()));
        turmas.add(new Turma("Técnico em Informática", 20, Turno.NOTURNO, true, diasSegundaQuartaSexta()));

        for (Turma turma : turmas) {
            contexto.getTurmaRepository().salvarTurma(turma);
        }

        return turmas;
    }

    private static List<DiaSemana> diasUteis() {
        return new ArrayList<>(List.of(
                DiaSemana.SEGUNDA,
                DiaSemana.TERCA,
                DiaSemana.QUARTA,
                DiaSemana.QUINTA,
                DiaSemana.SEXTA));
    }

    private static List<DiaSemana> diasSegundaQuartaSexta() {
        return new ArrayList<>(List.of(
                DiaSemana.SEGUNDA,
                DiaSemana.QUARTA,
                DiaSemana.SEXTA));
    }

    // ===================== CLIENTES =====================
    private static List<Cliente> popularClientes(Contexto contexto, List<Turma> turmas) {
        List<Cliente> clientes = new ArrayList<>();

        clientes.add(new Cliente("Ana Beatriz Souza", "111.111.111-11", "ana.souza@escola.com",
                turmas.get(0), "(48) 99111-1111", false, new ArrayList<Pedido>()));

        clientes.add(new Cliente("Bruno Carvalho Lima", "222.222.222-22", "bruno.lima@escola.com",
                turmas.get(1), "(48) 99222-2222", false, new ArrayList<Pedido>()));

        clientes.add(new Cliente("Carla Mendes Rocha", "333.333.333-33", "carla.rocha@escola.com",
                turmas.get(2), "(48) 99333-3333", false, new ArrayList<Pedido>()));

        clientes.add(new Cliente("Diego Fernandes Alves", "444.444.444-44", "diego.alves@escola.com",
                turmas.get(3), "(48) 99444-4444", false, new ArrayList<Pedido>()));

        clientes.add(new Cliente("Eduarda Martins Pires", "555.555.555-55", "eduarda.pires@escola.com",
                turmas.get(4), "(48) 99555-5555", false, new ArrayList<Pedido>()));

        for (Cliente cliente : clientes) {
            contexto.getClienteRepository().salvarCliente(cliente);
        }

        return clientes;
    }

    // ===================== PRODUTOS (CANTINA) =====================
    private static List<Produto> popularProdutos(Contexto contexto) {
        List<Produto> produtos = new ArrayList<>();

        // Salgados
        produtos.add(new Produto("Coxinha de Frango", CategoriaProduto.SALGADO, 7.50, 40));
        produtos.add(new Produto("Pastel de Carne", CategoriaProduto.SALGADO, 8.00, 30));
        produtos.add(new Produto("Empada de Frango", CategoriaProduto.SALGADO, 6.50, 25));
        produtos.add(new Produto("Esfiha de Carne", CategoriaProduto.SALGADO, 6.00, 35));

        // Doces
        produtos.add(new Produto("Brigadeiro", CategoriaProduto.DOCE, 3.50, 50));
        produtos.add(new Produto("Fatia de Bolo de Chocolate", CategoriaProduto.DOCE, 6.00, 20));
        produtos.add(new Produto("Cocada", CategoriaProduto.DOCE, 4.00, 30));

        // Bebidas
        produtos.add(new Produto("Suco de Laranja 300ml", CategoriaProduto.BEBIDA, 5.00, 45));
        produtos.add(new Produto("Refrigerante Lata", CategoriaProduto.BEBIDA, 6.00, 60));
        produtos.add(new Produto("Água Mineral 500ml", CategoriaProduto.BEBIDA, 3.00, 70));

        // Gelados
        produtos.add(new Produto("Picolé de Morango", CategoriaProduto.GELADOS, 4.50, 25));
        produtos.add(new Produto("Sorvete Casquinha", CategoriaProduto.GELADOS, 5.50, 20));

        // Salgadinhos
        produtos.add(new Produto("Batata Frita Pacote", CategoriaProduto.SALGADINHOS, 5.00, 40));
        produtos.add(new Produto("Salgadinho de Milho", CategoriaProduto.SALGADINHOS, 4.00, 40));

        for (Produto produto : produtos) {
            contexto.getProdutoRepository().salvarProduto(produto);
        }

        return produtos;
    }

    // ===================== CAIXA =====================
    private static Caixa abrirCaixa(Contexto contexto) {
        Caixa caixa = new Caixa();
        contexto.getCaixaRepository().salvarCaixa(caixa);
        return caixa;
    }

    // ===================== PEDIDOS =====================
    private static void popularPedidos(Contexto contexto, Caixa caixa, List<Cliente> clientes,
            List<Produto> produtos) {

        // ---------- Pedido 1: SEM cliente, pago em DINHEIRO, finalizado ----------
        Pedido pedido1 = new Pedido(0, null, "Sem observações");
        adicionarItem(pedido1, produtos.get(0), 2); // 2x Coxinha
        adicionarItem(pedido1, produtos.get(8), 1); // 1x Refrigerante
        pedido1.setFormaPagamento(FormaPagamento.DINHEIRO);
        finalizarFluxoCompleto(pedido1);
        registrarPedido(contexto, caixa, pedido1);

        // ---------- Pedido 2: SEM cliente, pago em PIX, finalizado ----------
        Pedido pedido2 = new Pedido(0, null, "Retirar rápido, intervalo curto");
        adicionarItem(pedido2, produtos.get(4), 3); // 3x Brigadeiro
        adicionarItem(pedido2, produtos.get(9), 1); // 1x Água
        pedido2.setFormaPagamento(FormaPagamento.PIX);
        finalizarFluxoCompleto(pedido2);
        registrarPedido(contexto, caixa, pedido2);

        // ---------- Pedido 3: SEM cliente, pago em CARTÃO, ainda em preparo
        // ----------
        Pedido pedido3 = new Pedido(0, null, "");
        adicionarItem(pedido3, produtos.get(1), 1); // 1x Pastel
        adicionarItem(pedido3, produtos.get(10), 1); // 1x Picolé
        pedido3.setFormaPagamento(FormaPagamento.CARTÃO);
        pedido3.setPrecoTotal(pedido3.calcularTotal());
        registrarPedido(contexto, caixa, pedido3); // permanece EM_PREPARO

        // ---------- Pedido 4: COM cliente (Ana), FIADO, finalizado ----------
        Cliente ana = clientes.get(0);
        Pedido pedido4 = new Pedido(0, ana, "Pagar no fim do mês");
        adicionarItem(pedido4, produtos.get(2), 2); // 2x Empada
        adicionarItem(pedido4, produtos.get(7), 1); // 1x Suco
        pedido4.setFormaPagamento(FormaPagamento.FIADO);
        finalizarFluxoCompleto(pedido4);
        registrarPedido(contexto, caixa, pedido4);
        marcarComoFiado(ana, pedido4);

        // ---------- Pedido 5: COM cliente (Bruno), FIADO, aguardando pagamento
        // ----------
        Cliente bruno = clientes.get(1);
        Pedido pedido5 = new Pedido(0, bruno, "Aluno costuma pagar semanalmente");
        adicionarItem(pedido5, produtos.get(3), 2); // 2x Esfiha
        adicionarItem(pedido5, produtos.get(12), 1); // 1x Batata Frita
        pedido5.setFormaPagamento(FormaPagamento.FIADO);
        pedido5.cobrarPedido(); // EM_PREPARO -> AGUARDANDO_PAGAMENTO
        pedido5.setPrecoTotal(pedido5.calcularTotal());
        registrarPedido(contexto, caixa, pedido5);
        marcarComoFiado(bruno, pedido5);

        // ---------- Pedido 6: COM cliente (Carla), FIADO, ainda em preparo
        // ----------
        Cliente carla = clientes.get(2);
        Pedido pedido6 = new Pedido(0, carla, "");
        adicionarItem(pedido6, produtos.get(5), 1); // 1x Fatia de Bolo
        adicionarItem(pedido6, produtos.get(9), 2); // 2x Água
        pedido6.setFormaPagamento(FormaPagamento.FIADO);
        pedido6.setPrecoTotal(pedido6.calcularTotal());
        registrarPedido(contexto, caixa, pedido6);
        marcarComoFiado(carla, pedido6);

        // ---------- Pedido 7: SEM cliente, pago em DINHEIRO, cancelado ----------
        Pedido pedido7 = new Pedido(0, null, "Cliente desistiu do pedido");
        adicionarItem(pedido7, produtos.get(13), 2); // 2x Salgadinho de Milho
        pedido7.setFormaPagamento(FormaPagamento.DINHEIRO);
        pedido7.setPrecoTotal(pedido7.calcularTotal());
        pedido7.cancelarPedido();
        estornarEstoque(pedido7); // simula rollback de estoque no cancelamento
        registrarPedido(contexto, caixa, pedido7);

        // ---------- Pedido 8: COM cliente (Diego), FIADO, finalizado ----------
        Cliente diego = clientes.get(3);
        Pedido pedido8 = new Pedido(0, diego, "Pedido do intervalo da tarde");
        adicionarItem(pedido8, produtos.get(11), 3); // 3x Sorvete Casquinha
        pedido8.setFormaPagamento(FormaPagamento.FIADO);
        finalizarFluxoCompleto(pedido8);
        registrarPedido(contexto, caixa, pedido8);
        marcarComoFiado(diego, pedido8);
    }

    // ===================== MÉTODOS AUXILIARES =====================

    private static void adicionarItem(Pedido pedido, Produto produto, int quantidade) {
        ItemPedido item = new ItemPedido(produto, quantidade, produto.getPreco() * quantidade);
        pedido.adicionarItem(item);
        produto.diminuirEstoque(quantidade);
    }

    private static void estornarEstoque(Pedido pedido) {
        for (ItemPedido item : pedido.getItens()) {
            item.getProduto().aumentarEstoque(item.getQuantidade());
        }
    }

    private static void finalizarFluxoCompleto(Pedido pedido) {
        pedido.cobrarPedido(); // EM_PREPARO -> AGUARDANDO_PAGAMENTO
        pedido.finalizarPedido(); // AGUARDANDO_PAGAMENTO -> FINALIZADO
        pedido.setPrecoTotal(pedido.calcularTotal());
    }

    private static void marcarComoFiado(Cliente cliente, Pedido pedido) {
        cliente.setDevendo(true);
        cliente.getHistoricoPedidos().add(pedido);
    }

    private static void registrarPedido(Contexto contexto, Caixa caixa, Pedido pedido) {
        contexto.getPedidoRepository().salvarPedido(pedido);
        caixa.registrarPedido(pedido);
    }
}