package desktop.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import desktop.view.Cliente.MenuCliente;
import service.CaixaService;
import service.ClienteService;
import service.PedidoService;
import service.ProdutoService;
import service.TurmaService;
import desktop.view.Produto.MenuProduto;
import desktop.view.Turma.MenuTurma;

public class MenuPrincipal {

    private final ClienteService clienteService;
    private final ProdutoService produtoService;
    private final PedidoService pedidoService;
    private final CaixaService caixaService;
    private final TurmaService turmaService;

    public MenuPrincipal(
            ClienteService clienteService,
            ProdutoService produtoService,
            PedidoService pedidoService,
            CaixaService caixaService,
            TurmaService turmaService) {
        this.clienteService = clienteService;
        this.produtoService = produtoService;
        this.pedidoService = pedidoService;
        this.caixaService = caixaService;
        this.turmaService = turmaService;
    }

    public void iniciar() {

        JFrame tela = new JFrame("Menu Principal");

        tela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tela.setSize(700, 450);
        tela.setLocationRelativeTo(null);
        tela.setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Selecione uma opção:");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setHorizontalAlignment(SwingConstants.LEFT);

        JPanel painelTitulo = new JPanel(new BorderLayout());
        painelTitulo.setBorder(
                BorderFactory.createEmptyBorder(30, 30, 20, 30));

        painelTitulo.add(titulo, BorderLayout.WEST);

        JButton btnClientes = new JButton("Clientes");
        JButton btnProdutos = new JButton("Produtos");
        JButton btnPedidos = new JButton("Pedidos");
        JButton btnCaixa = new JButton("Caixa");
        JButton btnTurmas = new JButton("Turmas");
        JButton btnSair = new JButton("Sair");

        Font fonteBotoes = new Font("Arial", Font.BOLD, 18);

        btnClientes.setFont(fonteBotoes);
        btnProdutos.setFont(fonteBotoes);
        btnPedidos.setFont(fonteBotoes);
        btnCaixa.setFont(fonteBotoes);
        btnTurmas.setFont(fonteBotoes);
        btnSair.setFont(fonteBotoes);

        JPanel painelBotoes = new JPanel(
                new GridLayout(3, 2, 10, 10));

        painelBotoes.setBorder(
                BorderFactory.createEmptyBorder(2, 10, 10, 10));

        painelBotoes.add(btnClientes);
        painelBotoes.add(btnProdutos);

        painelBotoes.add(btnPedidos);
        painelBotoes.add(btnCaixa);

        painelBotoes.add(btnTurmas);
        painelBotoes.add(btnSair);

        btnClientes.addActionListener(e -> {
            tela.setVisible(false);
            new MenuCliente(clienteService, pedidoService, turmaService).menu();
            tela.setVisible(true);
        });

        btnProdutos.addActionListener(e -> {
            tela.setVisible(false);
            new MenuProduto(produtoService).menu();
            tela.setVisible(true);
        });

        btnPedidos.addActionListener(e -> {
            tela.setVisible(false);
            new MenuPedido(clienteService, produtoService, pedidoService).menu();
            tela.setVisible(true);
        });

        btnCaixa.addActionListener(e -> {
            tela.setVisible(false);
            new MenuCaixa(caixaService).menu();
            tela.setVisible(true);
        });

        btnTurmas.addActionListener(e -> {
            tela.setVisible(false);
            new MenuTurma(turmaService).menu();
            tela.setVisible(true);
        });

        btnSair.addActionListener(e -> {
            tela.dispose();
        });

        tela.add(painelTitulo, BorderLayout.NORTH);
        tela.add(painelBotoes, BorderLayout.CENTER);

        tela.setVisible(true);
    }
}