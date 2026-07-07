package view;

import java.util.Scanner;

import model.Cliente;
import repository.ClienteRepository;

public class MenuCliente {
    private Scanner sc = new Scanner(System.in);
    private ClienteRepository repository = new ClienteRepository();

    public void menu() {

        int opcao;

        do {
            System.out.println("\n===== MENU CLIENTE =====");
            System.out.println("1 - Cadastrar");
            System.out.println("2 - Listar");
            System.out.println("3 - Buscar por CPF");
            System.out.println("4 - Remover");
            System.out.println("0 - Voltar");
            System.out.print("Opção: ");

            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {

                case 1:
                    cadastrar();
                    break;

                case 2:
                    listar();
                    break;

                case 3:
                    buscar();
                    break;

                case 4:
                    remover();
                    break;

                case 0:
                    System.out.println("Voltando...");
                    break;

                default:
                    System.out.println("Opção inválida.");
            }

        } while (opcao != 0);
    }

    private void cadastrar() {

        // System.out.print("Nome: ");
        // String nome = sc.nextLine();

        // System.out.print("CPF: ");
        // String cpf = sc.nextLine();

        // System.out.print("Email: ");
        // String email = sc.nextLine();

        // System.out.print("Cargo: ");
        // String cargo = sc.nextLine();

        // System.out.print("Turno: ");
        // String turno = sc.nextLine();

        // Cliente cliente = new Cliente(nome, cpf, email, cargo, turno);

        // repository.adicionar(cliente);

        // System.out.println("Cliente cadastrado com sucesso!");
    }

    private void listar() {

        for (Cliente cliente : repository.listar()) {
            System.out.println("----------------------------");
            System.out.println(cliente);
            // System.out.println("Cargo: " + cliente.getCargo());
            // System.out.println("Turno: " + cliente.getTurno());
        }

    }

    private void buscar() {

        System.out.print("CPF: ");
        String cpf = sc.nextLine();

        Cliente cliente = repository.buscarPorCpf(cpf);

        if (cliente != null) {
            System.out.println(cliente);
            // System.out.println("Cargo: " + cliente.getCargo());
            // System.out.println("Turno: " + cliente.getTurno());
        } else {
            System.out.println("Cliente não encontrado.");
        }

    }

    private void remover() {

        System.out.print("CPF: ");
        String cpf = sc.nextLine();

        if (repository.remover(cpf)) {
            System.out.println("Removido com sucesso!");
        } else {
            System.out.println("Funcionário não encontrado.");
        }

    }
}
