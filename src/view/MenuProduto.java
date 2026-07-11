package view;

import java.util.List;

import javax.swing.JOptionPane;

import model.Produto;
import model.enums.CategoriaProduto;
import repository.ProdutoRepository;

public class MenuProduto {

    private ProdutoRepository repository;

    public MenuProduto() {
        repository = new ProdutoRepository();
    }

    public void executar() {

        int opcao;

        do {

            String escolha = JOptionPane.showInputDialog(null,
                    "========== MENU PRODUTO ==========\n" +
                    "1 - Cadastrar Produto\n" +
                    "2 - Listar Produtos\n" +
                    "3 - Buscar Produto por ID\n" +
                    "4 - Atualizar Produto\n" +
                    "5 - Remover Produto\n" +
                    "0 - Voltar\n" +
                    "Escolha uma opção:");

            if (escolha == null) {
                break;
            }

            try {
                opcao = Integer.parseInt(escolha);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Opção inválida.");
                opcao = -1;
            }

            switch (opcao) {

                case 1:
                    cadastrarProduto();
                    break;

                case 2:
                    listarProdutos();
                    break;

                case 3:
                    buscarProduto();
                    break;

                case 4:
                    atualizarProduto();
                    break;

                case 5:
                    removerProduto();
                    break;

                case 0:
                    JOptionPane.showMessageDialog(null, "V3oltando...");
                    break;

                default:
                    if (opcao != -1) {
                        JOptionPane.showMessageDialog(null, "Opção inválida.");
                    }

            }

        } while (opcao != 0);

    }

    private void cadastrarProduto() {

        Integer id = lerIdOuVoltar();

        if (id == null) {
            return;
        }

        if (repository.buscarProdutoPorId(id) != null) {
            JOptionPane.showMessageDialog(null, "Já existe um produto cadastrado com esse ID.");
            return;
        }

        Produto produto = new Produto();
        produto.setId(id);

        String nome = JOptionPane.showInputDialog(null, "Nome:");
        produto.setNome(nome);

        CategoriaProduto categoria = lerCategoria();
        if (categoria == null) {
            return;
        }
        produto.setCategoria(categoria);

        String precoTexto = JOptionPane.showInputDialog(null, "Preço:");
        produto.setPreco(Double.parseDouble(precoTexto));

        String quantidadeTexto = JOptionPane.showInputDialog(null, "Quantidade em estoque:");
        produto.setQuantidadeEstoque(Integer.parseInt(quantidadeTexto));

        String mensagem = repository.adicionarProduto(produto);

        JOptionPane.showMessageDialog(null, mensagem);

    }

    private void listarProdutos() {

        List<Produto> produtos = repository.listarProdutos();

        if (produtos.isEmpty()) {

            JOptionPane.showMessageDialog(null, "Nenhum produto cadastrado.");
            return;

        }

        StringBuilder texto = new StringBuilder("========== PRODUTOS ==========\n");

        for (Produto produto : produtos) {

            texto.append(produto);
            texto.append("\n----------------------------\n");

        }

        JOptionPane.showMessageDialog(null, texto.toString());

    }

    private void buscarProduto() {

        Integer id = lerIdOuVoltar();

        if (id == null) {
            return;
        }

        Produto produto = repository.buscarProdutoPorId(id);

        if (produto == null) {

            JOptionPane.showMessageDialog(null, "Produto não encontrado.");

        } else {

            JOptionPane.showMessageDialog(null, produto.toString());

        }

    }

    private void atualizarProduto() {

        Integer id = lerIdOuVoltar();

        if (id == null) {
            return;
        }

        Produto produtoExistente = repository.buscarProdutoPorId(id);

        if (produtoExistente == null) {
            JOptionPane.showMessageDialog(null, "Produto não encontrado.");
            return;
        }

        Produto produto = new Produto();
        produto.setId(id);

        String nome = JOptionPane.showInputDialog(null, "Novo nome:");
        produto.setNome(nome);

        CategoriaProduto categoria = lerCategoria();
        if (categoria == null) {
            return;
        }
        produto.setCategoria(categoria);

        String precoTexto = JOptionPane.showInputDialog(null, "Novo preço:");
        produto.setPreco(Double.parseDouble(precoTexto));

        String quantidadeTexto = JOptionPane.showInputDialog(null, "Nova quantidade em estoque:");
        produto.setQuantidadeEstoque(Integer.parseInt(quantidadeTexto));

        String mensagem = repository.atualizarProduto(produto);

        JOptionPane.showMessageDialog(null, mensagem);

    }

    private void removerProduto() {

        Integer id = lerIdOuVoltar();

        if (id == null) {
            return;
        }

        Produto produtoExistente = repository.buscarProdutoPorId(id);

        if (produtoExistente == null) {
            JOptionPane.showMessageDialog(null, "Produto não encontrado.");
            return;
        }

        String mensagem = repository.removerProduto(id);

        JOptionPane.showMessageDialog(null, mensagem);

    }

    private CategoriaProduto lerCategoria() {

        CategoriaProduto[] categorias = CategoriaProduto.values();

        while (true) {

            StringBuilder texto = new StringBuilder("Categorias:\n");

            for (int i = 0; i < categorias.length; i++) {
                texto.append((i + 1) + " - " + categorias[i] + "\n");
            }

            texto.append("Escolha a categoria:");

            String escolha = JOptionPane.showInputDialog(null, texto.toString());

            if (escolha == null) {
                return null;
            }

            try {

                int opcao = Integer.parseInt(escolha);

                if (opcao >= 1 && opcao <= categorias.length) {
                    return categorias[opcao - 1];
                }

            } catch (NumberFormatException e) {
                
            }

            JOptionPane.showMessageDialog(null, "Categoria inválida.");

        }

    }

    private Integer lerIdOuVoltar() {

        while (true) {

            String entrada = JOptionPane.showInputDialog(null, "ID (ou deixe em branco para voltar):");

            if (entrada == null || entrada.trim().isEmpty()) {
                return null;
            }

            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "ID inválido. Digite um número ou deixe em branco para voltar.");
            }

        }

    }

}