package view;

import javax.swing.JOptionPane;

import model.Cliente;
import model.Turma;
import model.enums.Turno;
import repository.ClienteRepository;

public class MenuCliente {
    private ClienteRepository repository = new ClienteRepository();

    public void menu() {

      int opcao = -1;

do {

    try {

        String entrada = JOptionPane.showInputDialog(
                "========= MENU CLIENTE =========\n"
                + "1 - Cadastrar\n"
                + "2 - Listar\n"
                + "3 - Buscar por CPF\n"
                + "4 - Remover\n"
                + "0 - Voltar\n\n"
                + "Escolha uma opção:");

        // Cancelou ou fechou a janela
        if (entrada == null) {
            break;
        }

        // Não digitou nada
        if (entrada.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Digite uma opção!",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            continue;
        }

        opcao = Integer.parseInt(entrada);

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
                JOptionPane.showMessageDialog(null, "Voltando ao menu principal...");
                break;

            default:
                JOptionPane.showMessageDialog(null, "Opção inválida.");
        }

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(
                null,
                "Digite apenas números!",
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }

} while (opcao != 0);
}
    // rever os dados do metodo cadastro se de fato sao esses

    private void cadastrar() {

        String nome = JOptionPane.showInputDialog("Nome:");
        String cpf = JOptionPane.showInputDialog("CPF:");
        String email = JOptionPane.showInputDialog("Email:");
        String telefone = JOptionPane.showInputDialog("Telefone:");
        String nomeTurma = JOptionPane.showInputDialog("Turma:");
        String turnoDigitado = JOptionPane.showInputDialog("Turno (Matutino, Vespertino, Noturno ou Integral):");

        Turma turmaMatriculada = new Turma();
        turmaMatriculada.setNomeTurma(nomeTurma);
        turmaMatriculada.setQtdALunos(0);
        turmaMatriculada.setTurno(Turno.valueOf(turnoDigitado.toUpperCase()));
        turmaMatriculada.setAtivo(true);

        // Cria o cliente
        Cliente cliente = new Cliente(nome, cpf, email, turmaMatriculada, telefone, false, null);
        repository.adicionar(cliente);
        JOptionPane.showMessageDialog(null, "Cliente cadastrado com sucesso!");
        JOptionPane.showMessageDialog(null, "Cliente cadastrado com sucesso!\n" + cliente.toString());

    }

    private void listar() {

        StringBuilder sb = new StringBuilder();
        for (Cliente cliente : repository.listar()) {
            sb.append(cliente.toString()).append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Lista de Clientes", JOptionPane.INFORMATION_MESSAGE);

    }

    private void buscar() {

        String cpf = JOptionPane.showInputDialog("CPF:  ");
        Cliente cliente = repository.buscarPorCpf(cpf);

        if (cliente != null) {
        JOptionPane.showMessageDialog(null, "Cliente encontrado:\n" + cliente.toString());
        } else {
          JOptionPane.showMessageDialog(null, "Cliente não encontrado.");
        }

    }

    
      private void remover() {
      
      String cpf = JOptionPane.showInputDialog("CPF:  ");
        Cliente cliente = repository.buscarPorCpf(cpf);
      
      if (repository.remover(cpf)) {
        JOptionPane.showMessageDialog(null, "Cliente encontrado: \n" + cliente.toString());
      JOptionPane.showMessageDialog(null, "Cliente removido com sucesso!");
      } else {
        JOptionPane.showMessageDialog(null, "Cliente não encontrado.");
      }
      }
     
     
    
}
