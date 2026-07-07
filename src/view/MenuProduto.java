package view;

import java.util.List;
import java.util.Scanner;

import model.Produto;
import model.enums.CategoriaProduto;
import repository.ProdutoRepository;

public class MenuProduto {

    private Scanner scanner;
    private ProdutoRepository repository;

    public MenuProduto() {
        scanner = new Scanner(System.in);
        repository = new ProdutoRepository();
    }

    public void executar() {

        int opcao;

        do {

            System.out.println("\n========== MENU PRODUTO ==========");
            System.out.println("1 - Cadastrar Produto");
            System.out.println("2 - Listar Produtos");
            System.out.println("3 - Buscar Produto por ID");
            System.out.println("4 - Atualizar Produto");
            System.out.println("5 - Remover Produto");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opção: ");

            opcao = scanner.nextInt();
            scanner.nextLine();

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
                    System.out.println("Retornando...");
                    break;

                default:
                    System.out.println("Opção inválida.");

            }

        } while (opcao != 0);

    }

    private void cadastrarProduto() {

        Integer id = lerIdOuVoltar();

        if (id == null) {
            return;
        }

        if (repository.buscarProdutoPorId(id) != null) {
            System.out.println("\nJá existe um produto cadastrado com esse ID.");
            return;
        }

        Produto produto = new Produto();
        produto.setId(id);

        System.out.print("Nome: ");
        produto.setNome(scanner.nextLine());

        produto.setCategoria(lerCategoria());

        System.out.print("Preço: ");
        produto.setPreco(scanner.nextDouble());

        System.out.print("Quantidade em estoque: ");
        produto.setQuantidadeEstoque(scanner.nextInt());
        scanner.nextLine();

        String mensagem = repository.adicionarProduto(produto);

        System.out.println("\n" + mensagem);

    }

    private void listarProdutos() {

        List<Produto> produtos = repository.listarProdutos();

        if (produtos.isEmpty()) {

            System.out.println("\nNenhum produto cadastrado.");
            return;

        }

        System.out.println("\n========== PRODUTOS ==========");

        for (Produto produto : produtos) {

            System.out.println(produto);
            System.out.println("----------------------------");

        }

    }

    private void buscarProduto() {

        Integer id = lerIdOuVoltar();

        if (id == null) {
            return;
        }

        Produto produto = repository.buscarProdutoPorId(id);

        if (produto == null) {

            System.out.println("\nProduto não encontrado.");

        } else {

            System.out.println();
            System.out.println(produto);

        }

    }

    private void atualizarProduto() {

        Integer id = lerIdOuVoltar();

        if (id == null) {
            return;
        }

        Produto produtoExistente = repository.buscarProdutoPorId(id);

        if (produtoExistente == null) {
            System.out.println("\nProduto não encontrado.");
            return;
        }

        Produto produto = new Produto();
        produto.setId(id);

        System.out.print("Novo nome: ");
        produto.setNome(scanner.nextLine());

        produto.setCategoria(lerCategoria());

        System.out.print("Novo preço: ");
        produto.setPreco(scanner.nextDouble());

        System.out.print("Nova quantidade em estoque: ");
        produto.setQuantidadeEstoque(scanner.nextInt());
        scanner.nextLine();

        String mensagem = repository.atualizarProduto(produto);

        System.out.println("\n" + mensagem);

    }

    private void removerProduto() {

        Integer id = lerIdOuVoltar();

        if (id == null) {
            return;
        }

        Produto produtoExistente = repository.buscarProdutoPorId(id);

        if (produtoExistente == null) {
            System.out.println("\nProduto não encontrado.");
            return;
        }

        String mensagem = repository.removerProduto(id);

        System.out.println("\n" + mensagem);

    }

    private CategoriaProduto lerCategoria() {

        CategoriaProduto[] categorias = CategoriaProduto.values();

        while (true) {

            System.out.println("\nCategorias:");

            for (int i = 0; i < categorias.length; i++) {

                System.out.println((i + 1) + " - " + categorias[i]);

            }

            System.out.print("Escolha a categoria: ");

            int opcao = scanner.nextInt();
            scanner.nextLine();

            if (opcao >= 1 && opcao <= categorias.length) {

                return categorias[opcao - 1];

            }

            System.out.println("Categoria inválida.");

        }

    }

    private Integer lerIdOuVoltar() {

        while (true) {

            System.out.print("ID (ou digite V para voltar): ");
            String entrada = scanner.nextLine();

            if (entrada.equalsIgnoreCase("V")) {
                return null;
            }

            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("ID inválido. Digite um número ou V para voltar.");
            }

        }

    }

}