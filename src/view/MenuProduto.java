package view;

import java.util.Scanner;

import repository.ProdutoRepository;

public class MenuProduto {
    public Scanner scanner = new Scanner(System.in);
    ProdutoRepository produtoRepository = new ProdutoRepository();

    public void executar() {
        int opcao;
        System.out.println("Menu Produto");

        do {
            System.out.println("1. Cadastrar Produto");
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();

            switch (opcao) {
                case 1:
                    // produtoRepository.createProduto();
                    break;

                default:
                    break;
            }

        } while (opcao != 0);
    }
}
