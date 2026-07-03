package view;

import java.util.Scanner;

public class MenuPessoa {

    private Scanner sc = new Scanner(System.in);

    private MenuFuncionario menuFuncionario = new MenuFuncionario();
    // depois você pode criar:
    // private MenuCliente menuCliente = new MenuCliente();

    public void menu() {

        int opcao;

        do {

            System.out.println("\n===== MENU PESSOAS =====");
            System.out.println("1 - Funcionários");
            System.out.println("2 - Clientes");
            System.out.println("0 - Voltar");
            System.out.print("Opção: ");

            opcao = sc.nextInt();

            switch (opcao) {

                case 1:
                    menuFuncionario.menu();
                    break;

                case 2:
                    System.out.println("Menu de clientes ainda não implementado.");
                    // menuCliente.menu();
                    break;

                case 0:
                    break;

                default:
                    System.out.println("Opção inválida.");
            }

        } while (opcao != 0);
    }
}