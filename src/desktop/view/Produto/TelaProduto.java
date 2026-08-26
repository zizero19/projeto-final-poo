package desktop.view.Produto;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

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
import javax.swing.table.DefaultTableModel;

import model.Produto;
import service.ProdutoService;
import util.FormatacaoUtil;

public class TelaProduto extends JDialog {

    private final ProdutoService produtoService;

    private final JTable tabela;
    private final DefaultTableModel model;
    private List<Produto> produtosExibidos;

    private final JComboBox<String> cbModoBusca = new JComboBox<>(new String[] { "Nome", "ID" });
    private final JTextField txtBusca = new JTextField();

    public TelaProduto(ProdutoService produtoService) {
        super((Frame) null, "Gerenciamento de Produtos", true);
        this.produtoService = produtoService;

        String[] colunas = { "ID", "Nome", "Categoria", "Preço", "Estoque" };
        model = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(model);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        configurarTela();
        carregarProdutos(produtoService.listarProdutos());
    }

    private void configurarTela() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(750, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel topo = new JPanel(new GridLayout(2, 1, 5, 5));
        topo.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));

        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCadastrar = new JButton("Cadastrar Produto");
        JButton btnEditar = new JButton("Editar Produto");
        JButton btnExcluir = new JButton("Excluir Produto");

        btnCadastrar.addActionListener(e -> cadastrarProduto());
        btnEditar.addActionListener(e -> editarProduto());
        btnExcluir.addActionListener(e -> excluirProduto());

        painelAcoes.add(btnCadastrar);
        painelAcoes.add(btnEditar);
        painelAcoes.add(btnExcluir);

        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBuscar = new JButton("Buscar");
        JButton btnLimpar = new JButton("Limpar");
        txtBusca.setColumns(20);

        btnBuscar.addActionListener(e -> pesquisar());
        btnLimpar.addActionListener(e -> {
            txtBusca.setText("");
            carregarProdutos(produtoService.listarProdutos());
        });

        painelBusca.add(new JLabel("Buscar por:"));
        painelBusca.add(cbModoBusca);
        painelBusca.add(txtBusca);
        painelBusca.add(btnBuscar);
        painelBusca.add(btnLimpar);

        topo.add(painelAcoes);
        topo.add(painelBusca);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Produto selecionado = obterProdutoSelecionado();
                    if (selecionado != null) {
                        new FormDetalheProduto(selecionado).abrir();
                    }
                }
            }
        });

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        rodape.add(btnFechar);

        add(topo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }

    private void carregarProdutos(List<Produto> produtos) {
        produtosExibidos = produtos;
        model.setRowCount(0);

        for (Produto p : produtos) {
            model.addRow(new Object[] {
                    p.getId(),
                    p.getNome(),
                    p.getCategoria(),
                    FormatacaoUtil.formatarMoeda(p.getPreco()),
                    p.getQtdEstoque()
            });
        }
    }

    private Produto obterProdutoSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            return null;
        }
        return produtosExibidos.get(linha);
    }

    private void cadastrarProduto() {
        new FormProduto(produtoService, null, () -> carregarProdutos(produtoService.listarProdutos())).abrir();
    }

    private void editarProduto() {
        Produto selecionado = obterProdutoSelecionado();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela.", "Nenhum produto selecionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        new FormProduto(produtoService, selecionado, () -> carregarProdutos(produtoService.listarProdutos())).abrir();
    }

    private void excluirProduto() {
        Produto selecionado = obterProdutoSelecionado();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela.", "Nenhum produto selecionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String mensagem = "Deseja realmente excluir o produto abaixo?\n\n"
                + "Nome: " + selecionado.getNome() + "\n"
                + "Categoria: " + selecionado.getCategoria() + "\n"
                + "Preço: " + FormatacaoUtil.formatarMoeda(selecionado.getPreco());

        int confirmacao = JOptionPane.showConfirmDialog(this, mensagem, "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacao == JOptionPane.YES_OPTION) {
            boolean excluido = produtoService.excluirProduto(selecionado.getId());
            if (excluido) {
                JOptionPane.showMessageDialog(this, "Produto excluído com sucesso!");
                carregarProdutos(produtoService.listarProdutos());
            } else {
                JOptionPane.showMessageDialog(this, "Não foi possível excluir o produto.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void pesquisar() {
        String texto = txtBusca.getText().trim();
        String modo = (String) cbModoBusca.getSelectedItem();

        if (texto.isEmpty()) {
            carregarProdutos(produtoService.listarProdutos());
            return;
        }

        if ("ID".equals(modo)) {
            Long id;
            try {
                id = Long.parseLong(texto);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Digite um ID numérico válido.", "Busca inválida",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Produto produto = produtoService.buscarProduto(id);
            carregarProdutos(produto != null ? List.of(produto) : List.of());
        } else {
            carregarProdutos(produtoService.buscarProdutosPorNome(texto));
        }
    }

    public void abrir() {
        setVisible(true);
    }
}