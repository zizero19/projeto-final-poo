package desktop.view.Pedido;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.table.DefaultTableModel;

import model.ItemPedido;
import model.Pedido;
import model.Produto;
import model.enums.FormaPagamento;
import service.PedidoService;
import service.ProdutoService;
import service.RegraNegocioException;
import util.FormatacaoUtil;

/**
 * Tela operacional de criação de um pedido.
 *
 * Fluxo: buscar produto -> selecionar -> informar quantidade -> adicionar
 * -> ver pedido atual -> remover item se necessário -> escolher forma de
 * pagamento -> finalizar.
 */
public class FormNovoPedido extends JDialog {

    private final PedidoService pedidoService;
    private final ProdutoService produtoService;
    private final Runnable aoConcluir;

    private final JTextField txtBuscaProduto = new JTextField();
    private final JTable tabelaProdutos;
    private final DefaultTableModel modelProdutos;
    private List<Produto> produtosExibidos = new ArrayList<>();

    private final JTextField txtQuantidade = new JTextField(5);

    private final JTable tabelaItens;
    private final DefaultTableModel modelItens;
    private final List<ItemPedido> itensPedido = new ArrayList<>();
    private final Map<Long, Integer> quantidadeReservadaPorProduto = new HashMap<>();

    private final JLabel lblTotal = new JLabel("Total: R$ 0,00");

    private final JComboBox<FormaPagamento> cbFormaPagamento = new JComboBox<>(FormaPagamento.values());
    private final JTextField txtCpfCliente = new JTextField();

    public FormNovoPedido(PedidoService pedidoService, ProdutoService produtoService, Runnable aoConcluir) {
        super((Frame) null, "Novo Pedido", true);
        this.pedidoService = pedidoService;
        this.produtoService = produtoService;
        this.aoConcluir = aoConcluir;

        String[] colunasProdutos = { "ID", "Nome", "Categoria", "Preço", "Estoque" };
        modelProdutos = new DefaultTableModel(colunasProdutos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaProdutos = new JTable(modelProdutos);
        tabelaProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        String[] colunasItens = { "Produto", "Quantidade", "Preço Unitário", "Subtotal" };
        modelItens = new DefaultTableModel(colunasItens, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaItens = new JTable(modelItens);
        tabelaItens.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        configurarTela();
        carregarProdutos(produtoService.listarProdutos());
    }

    private void configurarTela() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(680, 640);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // ---- Bloco de busca + tabela de produtos ----
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBusca.add(new JLabel("Produto:"));
        txtBuscaProduto.setColumns(22);
        painelBusca.add(txtBuscaProduto);
        JButton btnLimparBusca = new JButton("Limpar");
        btnLimparBusca.addActionListener(e -> {
            txtBuscaProduto.setText("");
            carregarProdutos(produtoService.listarProdutos());
        });
        painelBusca.add(btnLimparBusca);

        txtBuscaProduto.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarProdutos();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarProdutos();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarProdutos();
            }
        });

        JScrollPane scrollProdutos = new JScrollPane(tabelaProdutos);
        scrollProdutos.setPreferredSize(new DimensionUIResource(620, 160));

        JPanel painelAdicionar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelAdicionar.add(new JLabel("Quantidade:"));
        painelAdicionar.add(txtQuantidade);
        JButton btnAdicionar = new JButton("Adicionar Item");
        btnAdicionar.addActionListener(e -> adicionarItem());
        painelAdicionar.add(btnAdicionar);

        JPanel painelTopo = new JPanel(new BorderLayout(5, 5));
        painelTopo.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));
        painelTopo.add(painelBusca, BorderLayout.NORTH);
        painelTopo.add(scrollProdutos, BorderLayout.CENTER);
        painelTopo.add(painelAdicionar, BorderLayout.SOUTH);

        // ---- Bloco do pedido atual ----
        JScrollPane scrollItens = new JScrollPane(tabelaItens);
        scrollItens.setPreferredSize(new DimensionUIResource(620, 150));
        scrollItens.setBorder(BorderFactory.createTitledBorder("Pedido Atual"));

        JButton btnRemoverItem = new JButton("Remover Item");
        btnRemoverItem.addActionListener(e -> removerItem());
        JPanel painelRemover = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelRemover.add(btnRemoverItem);

        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 15f));
        JPanel painelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelTotal.add(lblTotal);

        JPanel painelPedidoAtual = new JPanel(new BorderLayout(5, 5));
        painelPedidoAtual.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 15));
        painelPedidoAtual.add(scrollItens, BorderLayout.CENTER);
        JPanel painelPedidoRodape = new JPanel(new BorderLayout());
        painelPedidoRodape.add(painelRemover, BorderLayout.WEST);
        painelPedidoRodape.add(painelTotal, BorderLayout.EAST);
        painelPedidoAtual.add(painelPedidoRodape, BorderLayout.SOUTH);

        // ---- Bloco de pagamento ----
        JPanel painelPagamento = new JPanel(new GridLayout(2, 2, 8, 8));
        painelPagamento.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));
        painelPagamento.add(new JLabel("Forma de pagamento:"));
        painelPagamento.add(cbFormaPagamento);
        painelPagamento.add(new JLabel("CPF do cliente (obrigatório se FIADO):"));
        painelPagamento.add(txtCpfCliente);
        txtCpfCliente.setEnabled(false);

        cbFormaPagamento.addActionListener(e -> {
            boolean fiado = cbFormaPagamento.getSelectedItem() == FormaPagamento.FIADO;
            txtCpfCliente.setEnabled(fiado);
            if (!fiado) {
                txtCpfCliente.setText("");
            }
        });

        JButton btnFinalizar = new JButton("Finalizar Pedido");
        btnFinalizar.addActionListener(e -> finalizarPedido());
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        painelBotoes.add(btnCancelar);
        painelBotoes.add(btnFinalizar);

        JPanel painelInferior = new JPanel(new BorderLayout());
        painelInferior.add(painelPagamento, BorderLayout.CENTER);
        painelInferior.add(painelBotoes, BorderLayout.SOUTH);

        add(painelTopo, BorderLayout.NORTH);
        add(painelPedidoAtual, BorderLayout.CENTER);
        add(painelInferior, BorderLayout.SOUTH);
    }

    private void carregarProdutos(List<Produto> produtos) {
        produtosExibidos = produtos;
        modelProdutos.setRowCount(0);

        for (Produto p : produtos) {
            int reservado = quantidadeReservadaPorProduto.getOrDefault(p.getId(), 0);
            modelProdutos.addRow(new Object[] {
                    p.getId(),
                    p.getNome(),
                    p.getCategoria(),
                    FormatacaoUtil.formatarMoeda(p.getPreco()),
                    p.getQtdEstoque() - reservado
            });
        }
    }

    private void filtrarProdutos() {
        String texto = txtBuscaProduto.getText().trim();
        if (texto.isEmpty()) {
            carregarProdutos(produtoService.listarProdutos());
            return;
        }
        List<Produto> encontrados = produtoService.buscarProdutosPorNome(texto);
        carregarProdutos(encontrados);
        if (encontrados.isEmpty()) {
            modelProdutos.setRowCount(0);
        }
    }

    private void adicionarItem() {
        int linhaSelecionada = tabelaProdutos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela.", "Nenhum produto selecionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int quantidade;
        try {
            quantidade = Integer.parseInt(txtQuantidade.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Informe uma quantidade numérica válida.", "Quantidade inválida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Produto produtoSelecionado = produtosExibidos.get(linhaSelecionada);
        int reservado = quantidadeReservadaPorProduto.getOrDefault(produtoSelecionado.getId(), 0);

        try {
            ItemPedido item = pedidoService.criarItem(produtoSelecionado, quantidade, reservado);
            itensPedido.add(item);
            quantidadeReservadaPorProduto.merge(produtoSelecionado.getId(), quantidade, Integer::sum);

            modelItens.addRow(new Object[] {
                    produtoSelecionado.getNome(),
                    item.getQuantidade(),
                    FormatacaoUtil.formatarMoeda(produtoSelecionado.getPreco()),
                    FormatacaoUtil.formatarMoeda(item.getSubtotal())
            });

            atualizarTotal();
            txtQuantidade.setText("");
            carregarProdutos(produtosExibidos);
        } catch (RegraNegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Não foi possível adicionar o item",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void removerItem() {
        if (itensPedido.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O pedido ainda não possui itens.", "Nada para remover",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int linhaSelecionada = tabelaItens.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um item do pedido para remover.",
                    "Nenhum item selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ItemPedido item = itensPedido.get(linhaSelecionada);
        itensPedido.remove(linhaSelecionada);
        modelItens.removeRow(linhaSelecionada);

        Long produtoId = item.getProduto().getId();
        int restante = quantidadeReservadaPorProduto.getOrDefault(produtoId, 0) - item.getQuantidade();
        if (restante <= 0) {
            quantidadeReservadaPorProduto.remove(produtoId);
        } else {
            quantidadeReservadaPorProduto.put(produtoId, restante);
        }

        atualizarTotal();
        carregarProdutos(produtosExibidos);
    }

    private void atualizarTotal() {
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        for (ItemPedido item : itensPedido) {
            total = total.add(item.getSubtotal());
        }
        lblTotal.setText("Total: " + FormatacaoUtil.formatarMoeda(total));
    }

    private void finalizarPedido() {
        FormaPagamento formaPagamento = (FormaPagamento) cbFormaPagamento.getSelectedItem();
        String cpf = txtCpfCliente.getText() == null ? "" : txtCpfCliente.getText().trim();

        try {
            Pedido pedido = pedidoService.criarPedido(itensPedido, formaPagamento, cpf);
            JOptionPane.showMessageDialog(this,
                    "Pedido registrado com sucesso!\nTotal: " + FormatacaoUtil.formatarMoeda(pedido.getPrecoTotal()),
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            if (aoConcluir != null) {
                aoConcluir.run();
            }
            dispose();
        } catch (RegraNegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Não foi possível finalizar o pedido",
                    JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro ao acessar o banco de dados.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void abrir() {
        setVisible(true);
    }
}
