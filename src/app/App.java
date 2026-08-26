package app;

import desktop.view.MenuPrincipal;
import repository.CaixaRepository;
import repository.ClienteRepository;
import repository.PedidoRepository;
import repository.ProdutoRepository;
import repository.TurmaRepository;
import service.CaixaService;
import service.ClienteService;
import service.PedidoService;
import service.ProdutoService;
import service.TurmaService;

public class App {

    public static void main(String[] args) {
        ClienteRepository clienteRepository = new ClienteRepository();
        ProdutoRepository produtoRepository = new ProdutoRepository();
        PedidoRepository pedidoRepository = new PedidoRepository();
        CaixaRepository caixaRepository = new CaixaRepository();
        TurmaRepository turmaRepository = new TurmaRepository();

        ClienteService clienteService = new ClienteService(clienteRepository);
        ProdutoService produtoService = new ProdutoService(produtoRepository);
        CaixaService caixaService = new CaixaService(caixaRepository);
        TurmaService turmaService = new TurmaService(turmaRepository);

        PedidoService pedidoService = new PedidoService(
                clienteRepository,
                produtoRepository,
                caixaRepository,
                pedidoRepository);

        MenuPrincipal menuPrincipal = new MenuPrincipal(
                clienteService,
                produtoService,
                pedidoService,
                caixaService,
                turmaService);

        menuPrincipal.iniciar();
    }
}
