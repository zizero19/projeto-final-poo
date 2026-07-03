package view;

import java.util.Scanner;
import model.Funcionario;
import repository.FuncionarioRepository;

public class MenuFuncionario {

    private Scanner sc = new Scanner(System.in);
    private FuncionarioRepository repository = new FuncionarioRepository();

    public void menu() {

        int opcao;

        do {
            System.out.println("\n===== MENU FUNCIONÁRIO =====");
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

        System.out.print("Nome: ");
        String nome = sc.nextLine();

        System.out.print("CPF: ");
        String cpf = sc.nextLine();

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Cargo: ");
        String cargo = sc.nextLine();

        System.out.print("Turno: ");
        String turno = sc.nextLine();

        Funcionario funcionario = new Funcionario(nome, cpf, email, cargo, turno);

        repository.adicionar(funcionario);

        System.out.println("Funcionário cadastrado com sucesso!");
    }

    private void listar() {

        for (Funcionario funcionario : repository.listar()) {
            System.out.println("----------------------------");
            System.out.println(funcionario);
            System.out.println("Cargo: " + funcionario.getCargo());
            System.out.println("Turno: " + funcionario.getTurno());
        }

    }

    private void buscar() {

        System.out.print("CPF: ");
        String cpf = sc.nextLine();

        Funcionario funcionario = repository.buscarPorCpf(cpf);

        if (funcionario != null) {
            System.out.println(funcionario);
            System.out.println("Cargo: " + funcionario.getCargo());
            System.out.println("Turno: " + funcionario.getTurno());
        } else {
            System.out.println("Funcionário não encontrado.");
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