package view;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.table.DefaultTableModel;

import app.Contexto;
import model.Cliente;
import model.Turma;

public class MenuCliente {
    private Contexto contexto;

    public MenuCliente(Contexto contexto) {
        this.contexto = contexto;
    }

    public void menu() {
        int opcao;

        do {
            opcao = Integer.parseInt(JOptionPane.showInputDialog(
                    "========= MENU CLIENTE =========\n"
                            + "1 - Cadastrar Cliente\n"
                            + "2 - Listar Clientes\n"
                            + "3 - Buscar Cliente por CPF\n"
                            + "4 - Remover Cliente por CPF\n"
                            + "5 - Atualizar Cliente por CPF\n"
                            + "6 - Historico de Pedidos de Clientes\n"
                            + "0 - Voltar\n"
                            + "Escolha uma opção:"));

            switch (opcao) {

                case 1:
                    cadastrarCliente();
                    break;

                case 2:
                    listarClientes();
                    break;

                case 3:
                    buscarCliente();
                    break;

                case 4:
                    removerCliente();
                    break;

                case 5:
                    atualizarCliente();
                    break;

                case 0:
                    JOptionPane.showMessageDialog(null, "Voltando ao menu principal...");
                    break;

                default:
                    JOptionPane.showMessageDialog(null, "Opção inválida.");
            }

        } while (opcao != 0);

    }

    private void cadastrarCliente() {
        Cliente novoCliente = new Cliente();

        String nome = JOptionPane.showInputDialog("Nome:");
        String cpf = JOptionPane.showInputDialog("CPF (com . e -):");
        String email = JOptionPane.showInputDialog("Email:");
        Turma turma = lerTurma();
        String telefone = JOptionPane.showInputDialog("Telefone (com () e -):");

        novoCliente.setNome(nome);
        novoCliente.setCpf(cpf);
        novoCliente.setEmail(email);
        novoCliente.setTurmaMatriculada(turma);
        novoCliente.setTelefone(telefone);

        Cliente clienteExistente = contexto.getClienteRepository().buscarPorCpf(cpf);

        if (clienteExistente != null) {
            JOptionPane.showMessageDialog(null, "Cliente com CPF " + cpf + " já existe.");
            return;
        }

        contexto.getClienteRepository().salvarCliente(novoCliente);
        JOptionPane.showMessageDialog(null, "Cliente cadastrado com sucesso!");
    }

    private void listarClientes() {
        List<Cliente> clientes = contexto.getClienteRepository().listarClientes();

        if (clientes.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum cliente cadastrado.");
            return;
        }

        String[] colunas = { "Nome", "CPF", "Email", "Turma Matriculada", "Telefone" };

        DefaultTableModel model = new DefaultTableModel(colunas, 0);

        for (Cliente c : clientes) {
            model.addRow(new Object[] {
                    c.getNome(),
                    c.getCpf(),
                    c.getEmail(),
                    c.getTurmaMatriculada().getNomeTurma(),
                    c.getTelefone()
            });
        }

        JTable tabela = new JTable(model);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setPreferredSize(new DimensionUIResource(600, 300));

        JOptionPane.showMessageDialog(null, scroll);
    }

    private void buscarCliente() {
        String cpf = JOptionPane.showInputDialog("CPF:");
        Cliente cliente = contexto.getClienteRepository().buscarPorCpf(cpf);

        if (cliente != null) {
            JOptionPane.showMessageDialog(null, "Cliente encontrado:\n" + cliente.toString());
        } else {
            JOptionPane.showMessageDialog(null, "Cliente com CPF " + cpf + "não encontrado.");
        }

    }

    private void removerCliente() {
        String cpf = JOptionPane.showInputDialog("CPF:  ");
        Cliente cliente = contexto.getClienteRepository().buscarPorCpf(cpf);

        if (cliente != null) {
            contexto.getClienteRepository().excluirCliente(cpf);
            JOptionPane.showMessageDialog(null, "Cliente com CPF " + cpf + " removido com sucesso!");
        } else {
            JOptionPane.showMessageDialog(null, "Cliente não encontrado.");
        }
    }

    private void atualizarCliente() {
        String cpf = JOptionPane.showInputDialog(null, "CPF:");

        Cliente cliente = contexto.getClienteRepository().buscarPorCpf(cpf);

        if (cliente == null) {
            JOptionPane.showMessageDialog(null, "Cliente com CPF " + cpf + " não encontrado");
            return;
        }

        JTextField txtNome = new JTextField(cliente.getNome());
        JTextField txtCpf = new JTextField(cliente.getCpf());
        JTextField txtEmail = new JTextField(cliente.getEmail());
        JComboBox<Turma> comboTurmas = new JComboBox<>();
        comboTurmas.setSelectedItem(cliente.getTurmaMatriculada());
        JTextField txtTelefone = new JTextField(cliente.getTelefone());

        JPanel painel = new JPanel(new GridLayout(5, 0));

        painel.add(new JLabel("Nome:"));
        painel.add(txtNome);

        painel.add(new JLabel("CPF:"));
        painel.add(txtCpf);

        painel.add(new JLabel("Email:"));
        painel.add(txtEmail);

        painel.add(new JLabel("Turma:"));
        painel.add(comboTurmas);

        painel.add(new JLabel("Telefone"));
        painel.add(txtTelefone);

        int opcao = JOptionPane.showConfirmDialog(
                null,
                painel,
                "Atualizar Cliente",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (opcao == JOptionPane.OK_OPTION) {
            cliente.setNome(txtNome.getText());
            cliente.setCpf(txtCpf.getText());
            cliente.setEmail(txtEmail.getText());
            cliente.setTurmaMatriculada((Turma) comboTurmas.getSelectedItem());
            cliente.setTelefone(txtTelefone.getText());
        }

    }

    // public void historicoPedidos() {
    // String cpf = JOptionPane.showInputDialog(null, "Digite o CPF do Cliente:");

    // Cliente clienteBuscado = contexto.getClienteRepository().buscarPorCpf(cpf);

    // if (clienteBuscado == null) {
    // JOptionPane.showMessageDialog(null, "Cliente com o CPF " + cpf + " não
    // encontrado.");
    // return;
    // }

    // List<Pedido> pedidosDoCliente =
    // contexto.getPedidoRepository().buscarPedidosPorCpfDeCliente(cpf);

    // if (pedidosDoCliente.isEmpty()) {
    // JOptionPane.showMessageDialog(null, "Nenhum pedido associado a esse
    // cliente.");
    // return;
    // }

    // JTextField txtNome = new JTextField(cliente.getNome());
    // JTextField txtCpf = new JTextField(cliente.getCpf());
    // JTextField txtEmail = new JTextField(cliente.getEmail());
    // JComboBox<Turma> comboTurmas = new JComboBox<>();
    // comboTurmas.setSelectedItem(cliente.getTurmaMatriculada());
    // JTextField txtTelefone = new JTextField(cliente.getTelefone());

    // JPanel painel = new JPanel(new GridLayout(5, 0));

    // painel.add(new JLabel("Nome:"));
    // painel.add(txtNome);

    // painel.add(new JLabel("CPF:"));
    // painel.add(txtCpf);

    // painel.add(new JLabel("Email:"));
    // painel.add(txtEmail);

    // painel.add(new JLabel("Turma:"));
    // painel.add(comboTurmas);

    // painel.add(new JLabel("Telefone"));
    // painel.add(txtTelefone);

    // int opcao = JOptionPane.showConfirmDialog(
    // null,
    // painel,
    // "Atualizar Cliente",
    // JOptionPane.OK_CANCEL_OPTION,
    // JOptionPane.PLAIN_MESSAGE);

    // if (opcao == JOptionPane.OK_OPTION) {
    // cliente.setNome(txtNome.getText());
    // cliente.setCpf(txtCpf.getText());
    // cliente.setEmail(txtEmail.getText());
    // cliente.setTurmaMatriculada((Turma) comboTurmas.getSelectedItem());
    // cliente.setTelefone(txtTelefone.getText());
    // }
    // }

    public Turma lerTurma() {
        List<Turma> turmas = contexto.getTurmaRepository().listarTurmas();
        Turma turmaSelecionada = new Turma();

        JComboBox<Turma> comboTurmas = new JComboBox<>();

        for (Turma turma : turmas) {
            comboTurmas.addItem(turma);
        }

        int opcao = JOptionPane.showConfirmDialog(
                null,
                comboTurmas,
                "Selecione a turma",
                JOptionPane.OK_CANCEL_OPTION);

        if (opcao == JOptionPane.OK_OPTION) {
            turmaSelecionada = (Turma) comboTurmas.getSelectedItem();
        }

        return turmaSelecionada;
    }
}
