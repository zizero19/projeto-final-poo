package view;

import java.util.List;

import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.table.DefaultTableModel;

import app.Contexto;
import model.Produto;
import model.enums.CategoriaProduto;

public class MenuProduto {
    private Contexto contexto;

    public MenuProduto(Contexto contexto) {
        this.contexto = contexto;
    }

    public void menu() {
        int opcao;

        do {

            String entrada = JOptionPane.showInputDialog(null,
                    "========== MENU PRODUTO ==========\n" +
                            "1 - Cadastrar Produto\n" +
                            "2 - Listar Produtos\n" +
                            "3 - Buscar Produto\n" +
                            "4 - Atualizar Produto\n" +
                            "5 - Acrescentar Estoque Produto\n" +
                            "6 - Remover Produto\n" +
                            "0 - Voltar\n\n" +
                            "Escolha uma opção:");

            if (entrada == null) {
                return;
            }

            opcao = Integer.parseInt(entrada);

            switch (opcao) {

                case 1:
                    cadastrarProduto();
                    break;

                case 2:
                    listarProdutos();
                    break;

                case 3:
                    lerFormaBusca();
                    break;

                case 4:
                    atualizarProduto();
                    break;

                case 5:
                    acrescentarEstoqueProduto();
                    break;

                case 6:
                    removerProduto();
                    break;

                case 0:
                    JOptionPane.showMessageDialog(null, "Voltando...");
                    break;

                default:
                    JOptionPane.showMessageDialog(null, "Opção inválida.");
            }

        } while (opcao != 0);

    }

    public void cadastrarProduto() {
        String nome = JOptionPane.showInputDialog("Digite o nome do produto:");
        CategoriaProduto categoriaProduto = lerCategoria();
        double preco = Double.parseDouble(JOptionPane.showInputDialog("Digite o preço do produto:"));
        int quantidadeEstoque = Integer
                .parseInt(JOptionPane.showInputDialog("Digite a quantidade em estoque do produto:"));

        Produto novoProduto = new Produto(nome, categoriaProduto, preco, quantidadeEstoque);

        if (contexto.getProdutoRepository().salvarProduto(novoProduto)) {
            JOptionPane.showMessageDialog(null, "Produto cadastrado com sucesso!");
        } else {
            JOptionPane.showMessageDialog(null, "Produto ja existe ou esta com dados vazios.");
        }
    }

    public void listarProdutos() {
        List<Produto> produtos = contexto.getProdutoRepository().listarProdutos();

        if (produtos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum produto cadastrado.");
            return;
        }

        String[] colunas = {
                "ID",
                "Nome",
                "Categoria",
                "Preço",
                "Estoque"
        };

        DefaultTableModel model = new DefaultTableModel(colunas, 0);

        for (Produto p : produtos) {
            model.addRow(new Object[] {
                    p.getId(),
                    p.getNome(),
                    p.getCategoria(),
                    p.getPreco(),
                    p.getQtdEstoque()
            });
        }

        JTable tabela = new JTable(model);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setPreferredSize(new DimensionUIResource(600, 300));

        JOptionPane.showMessageDialog(null, scroll);
    }

    public void buscarProdutoPorId() {
        int id = Integer.parseInt(JOptionPane.showInputDialog("Digite o ID do produto a ser buscado:"));

        Produto produtoBuscado = contexto.getProdutoRepository().buscarProduto(id);

        if (produtoBuscado != null) {
            JOptionPane.showMessageDialog(null,
                    "Produto encontrado:\n" +
                            "ID: " + produtoBuscado.getId() + "\n" +
                            "Nome: " + produtoBuscado.getNome() + "\n" +
                            "Categoria: " + produtoBuscado.getCategoria() + "\n" +
                            "Preço: " + produtoBuscado.getPreco() + "\n" +
                            "Estoque: " + produtoBuscado.getQtdEstoque());
        } else {
            JOptionPane.showMessageDialog(null, "Produto com ID " + id + " não encontrado.");
        }
    }

    public void buscarProdutoPorNome() {
        String nome = JOptionPane.showInputDialog(null, "Insira o nome do Produto:");

        Produto produtoBuscado = contexto.getProdutoRepository().buscarProduto(nome);

        if (produtoBuscado != null) {
            JOptionPane.showMessageDialog(null,
                    "Produto encontrado:\n" +
                            "ID: " + produtoBuscado.getId() + "\n" +
                            "Nome: " + produtoBuscado.getNome() + "\n" +
                            "Categoria: " + produtoBuscado.getCategoria() + "\n" +
                            "Preço: " + produtoBuscado.getPreco() + "\n" +
                            "Estoque: " + produtoBuscado.getQtdEstoque());
        } else {
            JOptionPane.showMessageDialog(null, "Produto com Nome '" + nome + "'' não encontrado.");
        }
    }

    public void atualizarProduto() {
        int id = Integer.parseInt(
                JOptionPane.showInputDialog("Digite o ID do produto:"));

        Produto produto = contexto.getProdutoRepository().buscarProduto(id);

        if (produto == null) {
            JOptionPane.showMessageDialog(null,
                    "Produto com ID " + id + " não encontrado.");
            return;
        }

        JTextField txtNome = new JTextField(produto.getNome());
        JComboBox<CategoriaProduto> cbCategoria = new JComboBox<>(CategoriaProduto.values());
        cbCategoria.setSelectedItem(produto.getCategoria());
        JTextField txtPreco = new JTextField(String.valueOf(produto.getPreco()));
        JTextField txtEstoque = new JTextField(String.valueOf(produto.getQtdEstoque()));

        JPanel painel = new JPanel(new GridLayout(4, 2, 5, 5));

        painel.add(new JLabel("Nome:"));
        painel.add(txtNome);

        painel.add(new JLabel("Categoria:"));
        painel.add(cbCategoria);

        painel.add(new JLabel("Preço:"));
        painel.add(txtPreco);

        painel.add(new JLabel("Quantidade em estoque:"));
        painel.add(txtEstoque);

        int opcao = JOptionPane.showConfirmDialog(
                null,
                painel,
                "Atualizar Produto",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (opcao == JOptionPane.OK_OPTION) {
            produto.setNome(txtNome.getText());
            produto.setCategoria((CategoriaProduto) cbCategoria.getSelectedItem());
            produto.setPreco(Double.parseDouble(txtPreco.getText()));
            produto.setQtdEstoque(Integer.parseInt(txtEstoque.getText()));

            JOptionPane.showMessageDialog(null, "Produto atualizado com sucesso!");
        }
    }

    public void removerProduto() {
        int id = Integer.parseInt(JOptionPane.showInputDialog("Digite o ID do produto a ser removido:"));

        boolean excluido = contexto.getProdutoRepository().excluirProduto(id);

        if (excluido) {
            JOptionPane.showMessageDialog(null, "Produto com ID " + id + " removido com sucesso.");
        } else {
            JOptionPane.showMessageDialog(null, "Produto com ID " + id + " não encontrado.");
        }
    }

    public void acrescentarEstoqueProduto() {
        int id = Integer.parseInt(JOptionPane.showInputDialog("Digite o ID do produto:"));

        Produto produto = contexto.getProdutoRepository().buscarProduto(id);

        if (produto == null) {
            JOptionPane.showMessageDialog(null, "Produto com ID " + id + " não encontrado.");
            return;
        }

        int quantidade = Integer.parseInt(JOptionPane.showInputDialog("Digite a quantidade a ser acrescentada:"));

        if (quantidade < 0) {
            JOptionPane.showMessageDialog(null,
                    "Quantidade inválida. Não é possível acrescentar uma quantidade negativa.");
            return;
        }

        produto.setQtdEstoque(produto.getQtdEstoque() + quantidade);

        JOptionPane.showMessageDialog(null,
                "Estoque atualizado com sucesso! Novo estoque: " + produto.getQtdEstoque());
    }

    public CategoriaProduto lerCategoria() {
        String[] opcoes = {
                "Salgado",
                "Doce",
                "Bebida",
                "Gelados",
                "Salgadinhos"
        };

        while (true) {

            int opcao = JOptionPane.showOptionDialog(
                    null,
                    "Escolha a categoria:",
                    "Categoria",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    opcoes,
                    opcoes[0]);

            switch (opcao) {
                case 0:
                    return CategoriaProduto.SALGADO;
                case 1:
                    return CategoriaProduto.DOCE;
                case 2:
                    return CategoriaProduto.BEBIDA;
                case 3:
                    return CategoriaProduto.GELADOS;
                case 4:
                    return CategoriaProduto.SALGADINHOS;
                default:
                    JOptionPane.showMessageDialog(null, "Categoria inválida.");
            }
        }
    }

    public void lerFormaBusca() {
        String[] opcoes = {
                "Buscar por Nome", "Buscar por Id", "Sair" };
        while (true) {
            int escolha = JOptionPane.showOptionDialog(
                    null,
                    "Escolha a forma de busca do Produto",
                    "Buscar Produto",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    opcoes,
                    opcoes[0]);

            switch (escolha) {
                case 0:
                    buscarProdutoPorNome();
                    break;

                case 1:
                    buscarProdutoPorId();
                    break;

                case 2:
                    JOptionPane.showMessageDialog(null, "Saindo da busca de Produto.");
                    return;
            }
        }

    }
}
