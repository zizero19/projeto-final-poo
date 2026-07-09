import model.Caixa;
import model.Cliente;
import model.Estoque;
import model.ItemPedido;
import model.Pedido;
import model.Produto;
import model.Turma;
import model.enums.CategoriaProduto;
import model.enums.StatusPedido;
import model.enums.Turno;

public class App {

    public static void main(String[] args) throws Exception {
        testarEstoque();
        System.out.println();
        testarCaixa();
    }

    private static void testarEstoque() {
        System.out.println("############# TESTE ESTOQUE #############\n");

        Estoque estoque = new Estoque();

        Produto coxinha = new Produto(1, "Coxinha", CategoriaProduto.SALGADO, 6.50, 10);
        Produto refrigerante = new Produto(2, "Refrigerante", CategoriaProduto.BEBIDA, 5.00, 0);
        Produto brigadeiro = new Produto(3, "Brigadeiro", CategoriaProduto.DOCE, 3.00, 20);

        estoque.adicionarProduto(coxinha);
        estoque.adicionarProduto(refrigerante);
        estoque.adicionarProduto(brigadeiro);

        System.out.println(estoque);

        System.out.println("--- buscarProduto(1) ---");
        System.out.println(estoque.buscarProduto(1));

        System.out.println("\n--- buscarProduto(99) [não existe] ---");
        System.out.println(estoque.buscarProduto(99));

        System.out.println("\n--- verificarDisponibilidade(1) [tem estoque] ---");
        estoque.verificarDisponibilidade(1);

        System.out.println("\n--- verificarDisponibilidade(2) [sem estoque] ---");
        estoque.verificarDisponibilidade(2);

        System.out.println("\n--- verificarDisponibilidade(99) [não existe] ---");
        estoque.verificarDisponibilidade(99);

        System.out.println("\n--- removerProduto(3) ---");
        estoque.removerProduto(3);
        System.out.println(estoque);

        System.out.println("--- removerProduto(99) [não existe] ---");
        estoque.removerProduto(99);
    }

    private static void testarCaixa() {
        System.out.println("############# TESTE CAIXA #############\n");

        Turma turma = new Turma("Info 3", 30, Turno.NOTURNO, true);

        Cliente cliente1 = new Cliente("Ana Souza", "111.111.111-11", "ana@email.com",
                turma, "48999990000", false, new java.util.ArrayList<>());
        Cliente cliente2 = new Cliente("Bruno Lima", "222.222.222-22", "bruno@email.com",
                turma, "48988880000", false, new java.util.ArrayList<>());

        Produto coxinha = new Produto(1, "Coxinha", CategoriaProduto.SALGADO, 6.50, 10);
        Produto suco = new Produto(4, "Suco", CategoriaProduto.BEBIDA, 4.00, 15);

        Caixa caixa = new Caixa(2); // qtdMaxClientes = 2

        System.out.println("--- registrarPedido antes de abrir o caixa ---");
        Pedido pedidoAntes = new Pedido(0, cliente1);
        caixa.registrarPedido(pedidoAntes);

        System.out.println("\n--- abrindo o caixa ---");
        caixa.abrir();
        System.out.println(caixa);

        // Pedido 1: cliente1, FINALIZADO -> deve entrar no total de vendas
        Pedido pedido1 = new Pedido(1, cliente1);
        pedido1.adicionarItem(new ItemPedido(coxinha, 2, 0));
        pedido1.setStatus(StatusPedido.FINALIZADO);

        // Pedido 2: cliente2, FIADO -> deve entrar no total de vendas
        Pedido pedido2 = new Pedido(2, cliente2);
        pedido2.adicionarItem(new ItemPedido(suco, 3, 0));
        pedido2.setStatus(StatusPedido.FIADO);

        // Pedido 3: cliente1, CANCELADO -> NÃO deve entrar no total de vendas
        Pedido pedido3 = new Pedido(3, cliente1);
        pedido3.adicionarItem(new ItemPedido(coxinha, 1, 0));
        pedido3.setStatus(StatusPedido.CANCELADO);

        System.out.println("\n--- registrando pedido 1 (FINALIZADO) ---");
        caixa.registrarPedido(pedido1);

        System.out.println("--- registrando pedido 2 (FIADO) ---");
        caixa.registrarPedido(pedido2);

        System.out.println("--- registrando pedido 3 (CANCELADO) ---");
        caixa.registrarPedido(pedido3);

        System.out.println("\n--- estado do caixa após os 3 pedidos ---");
        System.out.println(caixa);

        // Cliente novo (3º cliente) -> deve ser recusado, pois qtdMaxClientes = 2
        Cliente cliente3 = new Cliente("Carla Dias", "333.333.333-33", "carla@email.com",
                turma, "48977770000", false, new java.util.ArrayList<>());
        Pedido pedido4 = new Pedido(4, cliente3);
        pedido4.setStatus(StatusPedido.FINALIZADO);

        System.out.println("\n--- registrando pedido 4 (3º cliente, limite = 2) ---");
        caixa.registrarPedido(pedido4);

        System.out.println("\n--- fechando o caixa ---");
        caixa.fechar();
        System.out.println(caixa);

        System.out.println("\n--- tentando registrar pedido com o caixa fechado ---");
        caixa.registrarPedido(pedido4);
    }
}