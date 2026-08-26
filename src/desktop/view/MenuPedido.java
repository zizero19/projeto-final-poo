package desktop.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.table.DefaultTableModel;

import service.ClienteService;
import service.PedidoService;
import service.ProdutoService;
import model.Cliente;
import model.ItemPedido;
import model.Pedido;
import model.Produto;
import model.enums.FormaPagamento;
import model.enums.StatusPedido;
import util.FormatacaoUtil;

public class MenuPedido {
    private final ClienteService clienteService;
    private final ProdutoService produtoService;
    private final PedidoService pedidoService;

    public MenuPedido(
            ClienteService clienteService,
            ProdutoService produtoService,
            PedidoService pedidoService) {
        this.clienteService = clienteService;
        this.produtoService = produtoService;
        this.pedidoService = pedidoService;
    }

    public void menu() {
        int opcao;

        do {
            String entrada = JOptionPane.showInputDialog(
                    "========= MENU PEDIDO =========\n"
                            + "1 - Realizar Pedido\n"
                            + "2 - Listar Pedidos\n"
                            + "3 - Buscar Pedido por ID\n"
                            + "4 - Confirmar Pagamento / Finalizar Pedido\n"
                            + "5 - Cancelar Pedido\n"
                            + "6 - Remover Pedido\n"
                            + "0 - Voltar\n\n"
                            + "Escolha uma opção:");

            if (entrada == null) {
                return;
            }

            opcao = Integer.parseInt(entrada);

            switch (opcao) {
                case 1:
                    realizarPedido();
                    break;
                case 2:
                    listarPedidos();
                    break;
                case 3:
                    buscarPedidoPorId();
                    break;
                case 4:
                    confirmarPagamento();
                    break;
                case 5:
                    cancelarPedido();
                    break;
                case 6:
                    removerPedido();
                    break;
                case 0:
                    JOptionPane.showMessageDialog(null, "Voltando ao menu principal...");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opção inválida.");
            }

        } while (opcao != 0);
    }

    public void realizarPedido() {
        List<Produto> produtos = produtoService.listarProdutos();
        if (produtos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum produto cadastrado.");
            return;
        }

        String[] colunasProdutos = { "ID", "Nome", "Categoria", "Preço", "Estoque" };
        DefaultTableModel modelProdutos = new DefaultTableModel(colunasProdutos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<Produto> produtosExibidos = new ArrayList<>(produtos);
        for (Produto p : produtosExibidos) {
            modelProdutos.addRow(new Object[] {
                    p.getId(),
                    p.getNome(),
                    p.getCategoria(),
                    FormatacaoUtil.formatarMoeda(p.getPreco()),
                    p.getQtdEstoque()
            });
        }

        JTable tabelaProdutos = new JTable(modelProdutos);
        tabelaProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollProdutos = new JScrollPane(tabelaProdutos);
        scrollProdutos.setPreferredSize(new DimensionUIResource(560, 150));

        JTextField txtQuantidade = new JTextField(5);
        JButton btnAdicionar = new JButton("Adicionar Item");
        JPanel painelAdicionar = new JPanel();
        painelAdicionar.add(new JLabel("Quantidade:"));
        painelAdicionar.add(txtQuantidade);
        painelAdicionar.add(btnAdicionar);

        List<ItemPedido> itensPedido = new ArrayList<>();
        String[] colunasItens = { "Produto", "Quantidade", "Subtotal" };
        DefaultTableModel modelItens = new DefaultTableModel(colunasItens, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabelaItens = new JTable(modelItens);
        JScrollPane scrollItens = new JScrollPane(tabelaItens);
        scrollItens.setPreferredSize(new DimensionUIResource(560, 120));

        JLabel lblTotal = new JLabel("Total: R$ 0,00");

        Runnable atualizarTotal = () -> {
            BigDecimal total = BigDecimal.ZERO;
            for (ItemPedido i : itensPedido) {
                total = total.add(i.getSubtotal());
            }
            lblTotal.setText("Total: " + FormatacaoUtil.formatarMoeda(total));
        };

        btnAdicionar.addActionListener(e -> {
            int linhaSelecionada = tabelaProdutos.getSelectedRow();
            if (linhaSelecionada == -1) {
                JOptionPane.showMessageDialog(null, "Selecione um produto na tabela.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Produto produtoSelecionado = produtosExibidos.get(linhaSelecionada);
            int quantidade;
            try {
                quantidade = Integer.parseInt(txtQuantidade.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Quantidade inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (quantidade <= 0) {
                JOptionPane.showMessageDialog(null, "A quantidade deve ser maior que zero.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (quantidade > produtoSelecionado.getQtdEstoque()) {
                JOptionPane.showMessageDialog(null, "Quantidade indisponível em estoque.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            ItemPedido item = new ItemPedido(produtoSelecionado, quantidade,
                    produtoSelecionado.getPreco().multiply(BigDecimal.valueOf(quantidade)));
            itensPedido.add(item);
            produtoSelecionado.diminuirEstoque(quantidade);
            modelItens.addRow(new Object[] {
                    produtoSelecionado.getNome(),
                    item.getQuantidade(),
                    FormatacaoUtil.formatarMoeda(item.getSubtotal())
            });
            modelProdutos.setValueAt(produtoSelecionado.getQtdEstoque(), linhaSelecionada, 4);
            atualizarTotal.run();
            txtQuantidade.setText("");
        });

        JTextField txtCpfCliente = new JTextField();
        JPanel painelCliente = new JPanel(new GridLayout(1, 2, 5, 5));
        painelCliente.add(new JLabel("CPF do cliente (opcional):"));
        painelCliente.add(txtCpfCliente);

        JComboBox<FormaPagamento> cbFormaPagamento = new JComboBox<>(FormaPagamento.values());
        JPanel painelPagamento = new JPanel(new GridLayout(1, 2, 5, 5));
        painelPagamento.add(new JLabel("Forma de pagamento:"));
        painelPagamento.add(cbFormaPagamento);

        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.add(scrollProdutos, BorderLayout.NORTH);
        painel.add(painelAdicionar, BorderLayout.CENTER);
        painel.add(scrollItens, BorderLayout.SOUTH);

        JPanel painelInferior = new JPanel(new BorderLayout(10, 10));
        painelInferior.add(painelCliente, BorderLayout.NORTH);
        painelInferior.add(painelPagamento, BorderLayout.CENTER);
        painelInferior.add(lblTotal, BorderLayout.SOUTH);

        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.add(painel, BorderLayout.CENTER);
        principal.add(painelInferior, BorderLayout.SOUTH);

        int confirm = JOptionPane.showConfirmDialog(null, principal, "Realizar Pedido", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (confirm != JOptionPane.OK_OPTION || itensPedido.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Pedido cancelado ou sem itens.");
            return;
        }

        Cliente cliente = null;
        String cpf = txtCpfCliente.getText() == null ? "" : txtCpfCliente.getText().trim();
        if (!cpf.isEmpty()) {
            cliente = clienteService.buscarPorCpf(cpf);
        }

        Pedido novoPedido = new Pedido(cliente, "");
        novoPedido.setItens(itensPedido);
        novoPedido.setFormaPagamento((FormaPagamento) cbFormaPagamento.getSelectedItem());
        novoPedido.setStatus(StatusPedido.EM_PREPARO);
        novoPedido.setPrecoTotal(novoPedido.calcularTotal());

        pedidoService.salvarPedido(novoPedido);

        if (cliente != null) {
            cliente.setDevendo(true);
            if (cliente.getHistoricoPedidos() == null) {
                cliente.setHistoricoPedidos(new ArrayList<>());
            }
            cliente.getHistoricoPedidos().add(novoPedido);
        }

        JOptionPane.showMessageDialog(null,
                "Pedido realizado com sucesso!\nTotal: " + FormatacaoUtil.formatarMoeda(novoPedido.getPrecoTotal()));
    }

    public void listarPedidos() {
        List<Pedido> pedidos = pedidoService.listarPedidos();
        if (pedidos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum pedido cadastrado.");
            return;
        }

        String[] colunas = { "ID", "Cliente", "Data/Hora", "Status", "Forma de Pagamento", "Total" };
        DefaultTableModel model = new DefaultTableModel(colunas, 0);
        for (Pedido p : pedidos) {
            model.addRow(new Object[] {
                    p.getId(),
                    p.getCliente() != null ? p.getCliente().getNome() : "-",
                    FormatacaoUtil.formatarDataHora(p.getDataHora()),
                    p.getStatus(),
                    p.getFormaPagamento(),
                    FormatacaoUtil.formatarMoeda(p.calcularTotal())
            });
        }

        JTable tabela = new JTable(model);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setPreferredSize(new DimensionUIResource(700, 250));
        JOptionPane.showMessageDialog(null, scroll);
    }

    public void buscarPedidoPorId() {
        Long id = Long.parseLong(JOptionPane.showInputDialog("Digite o ID do pedido a ser buscado:"));
        Pedido pedido = pedidoService.buscarPorId(id);

        if (pedido == null) {
            JOptionPane.showMessageDialog(null, "Pedido não encontrado.");
            return;
        }

        mostrarDetalhesPedido(pedido);
    }

    public void confirmarPagamento() {
        Long id = Long.parseLong(JOptionPane.showInputDialog("Digite o ID do pedido para confirmar o pagamento:"));
        Pedido pedido = pedidoService.buscarPorId(id);

        if (pedido == null) {
            JOptionPane.showMessageDialog(null, "Pedido não encontrado.");
            return;
        }

        pedido.setStatus(StatusPedido.FINALIZADO);
        pedido.setPrecoTotal(pedido.calcularTotal());
        JOptionPane.showMessageDialog(null,
                "Pagamento confirmado com sucesso.\nTotal: " + FormatacaoUtil.formatarMoeda(pedido.getPrecoTotal()));
    }

    public void cancelarPedido() {
        Long id = Long.parseLong(JOptionPane.showInputDialog("Digite o ID do pedido a ser cancelado:"));
        Pedido pedido = pedidoService.buscarPorId(id);

        if (pedido == null) {
            JOptionPane.showMessageDialog(null, "Pedido não encontrado.");
            return;
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        JOptionPane.showMessageDialog(null, "Pedido cancelado com sucesso.");
    }

    public void removerPedido() {
        Long id = Long.parseLong(JOptionPane.showInputDialog("Digite o ID do pedido a ser removido:"));
        Pedido pedido = pedidoService.buscarPorId(id);

        if (pedido == null) {
            JOptionPane.showMessageDialog(null, "Pedido não encontrado.");
            return;
        }

        pedidoService.removerPedido(id);
        JOptionPane.showMessageDialog(null, "Pedido removido com sucesso.");
    }

    public void mostrarDetalhesPedido(Pedido pedido) {
        if (pedido == null) {
            JOptionPane.showMessageDialog(null, "Pedido não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] colunas = { "Produto", "Quantidade", "Preço Unitário", "Subtotal" };
        DefaultTableModel modelItens = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (pedido.getItens() != null) {
            for (ItemPedido item : pedido.getItens()) {
                if (item == null || item.getProduto() == null) {
                    continue;
                }
                modelItens.addRow(new Object[] {
                        item.getProduto().getNome(),
                        item.getQuantidade(),
                        FormatacaoUtil.formatarMoeda(item.getProduto().getPreco()),
                        FormatacaoUtil.formatarMoeda(item.getSubtotal())
                });
            }
        }

        JTable tabelaItens = new JTable(modelItens);
        JScrollPane scrollItens = new JScrollPane(tabelaItens);
        scrollItens.setPreferredSize(new DimensionUIResource(600, 180));

        JLabel lblTotal = new JLabel("Total do Pedido: " + FormatacaoUtil.formatarMoeda(pedido.calcularTotal()));
        JPanel painelPrincipal = new JPanel(new BorderLayout(5, 5));
        painelPrincipal.add(
                new JLabel("ID: " + pedido.getId() + " | Data/Hora: "
                        + FormatacaoUtil.formatarDataHora(pedido.getDataHora()) + " | Status: " + pedido.getStatus()),
                BorderLayout.NORTH);
        painelPrincipal.add(scrollItens, BorderLayout.CENTER);
        painelPrincipal.add(lblTotal, BorderLayout.SOUTH);
        JOptionPane.showMessageDialog(null, painelPrincipal, "Detalhes do Pedido #" + pedido.getId(),
                JOptionPane.PLAIN_MESSAGE);
    }
}