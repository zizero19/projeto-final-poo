package desktop.view.Cliente;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.text.ParseException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

import model.Cliente;
import service.ClienteService;
import service.TurmaService;
import model.Turma;

public class FormCliente extends JDialog {

    private final ClienteService clienteService;
    private final TurmaService turmaService;
    private final Cliente clienteEmEdicao;
    private final Runnable aoSalvar;

    private final JTextField txtNome = new JTextField();
    private final JFormattedTextField txtCpf;
    private final JTextField txtEmail = new JTextField();
    private final JFormattedTextField txtTelefone;
    private final JComboBox<Turma> cbTurma = new JComboBox<>();

    public FormCliente(ClienteService clienteService, TurmaService turmaService) {
        this(clienteService, turmaService, null, null);
    }

    public FormCliente(ClienteService clienteService, TurmaService turmaService,
            Cliente clienteEditar, Runnable aoSalvar) {
        super((Frame) null, clienteEditar == null ? "Cadastro de Cliente" : "Editar Cliente", true);
        this.clienteService = clienteService;
        this.turmaService = turmaService;
        this.clienteEmEdicao = clienteEditar;
        this.aoSalvar = aoSalvar;

        txtCpf = criarCampoCpf();
        txtTelefone = criarCampoTelefone();

        configurarTela();
        carregarTurmas();

        if (clienteEditar != null) {
            preencherCampos(clienteEditar);
        }
    }

    private void configurarTela() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 330);
        setLocationRelativeTo(null);

        JPanel campos = new JPanel(new GridLayout(5, 2, 10, 10));
        campos.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        campos.add(new JLabel("Nome:"));
        campos.add(txtNome);
        campos.add(new JLabel("CPF:"));
        campos.add(txtCpf);
        campos.add(new JLabel("Email:"));
        campos.add(txtEmail);
        campos.add(new JLabel("Turma:"));
        campos.add(cbTurma);
        campos.add(new JLabel("Telefone:"));
        campos.add(txtTelefone);

        boolean edicao = clienteEmEdicao != null;
        JButton btnSalvar = new JButton(edicao ? "Salvar" : "Cadastrar");
        JButton btnCancelar = new JButton("Cancelar");

        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());

        JPanel botoes = new JPanel();
        botoes.add(btnCancelar);
        botoes.add(btnSalvar);

        setLayout(new BorderLayout());
        add(campos, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);
    }

    private JFormattedTextField criarCampoCpf() {
        try {
            MaskFormatter mascara = new MaskFormatter("###.###.###-##");
            mascara.setPlaceholderCharacter('_');
            return new JFormattedTextField(mascara);
        } catch (ParseException e) {
            throw new RuntimeException("Erro ao criar máscara do CPF.", e);
        }
    }

    private JFormattedTextField criarCampoTelefone() {
        try {
            MaskFormatter mascara = new MaskFormatter("(##) #####-####");
            mascara.setPlaceholderCharacter('_');
            return new JFormattedTextField(mascara);
        } catch (ParseException e) {
            throw new RuntimeException("Erro ao criar máscara do telefone.", e);
        }
    }

    private void carregarTurmas() {
        List<Turma> turmas = turmaService.listarTurmas();
        cbTurma.addItem(null);
        for (Turma turma : turmas) {
            cbTurma.addItem(turma);
        }
    }

    private void preencherCampos(Cliente cliente) {
        txtNome.setText(cliente.getNome());
        txtCpf.setText(cliente.getCpf());
        txtCpf.setEditable(false);
        txtEmail.setText(cliente.getEmail());
        txtTelefone.setText(cliente.getTelefone());
        cbTurma.setSelectedItem(cliente.getTurmaMatriculada());
    }

    private void salvar() {
        String nome = txtNome.getText().trim();
        String cpf = txtCpf.getText().trim();
        String email = txtEmail.getText().trim();
        String telefone = txtTelefone.getText().trim();
        Turma turma = (Turma) cbTurma.getSelectedItem();

        if (nome.isEmpty()) {
            erro("O nome deve ser preenchido.", txtNome);
            return;
        }

        if (cpf.isEmpty()) {
            erro("O CPF deve ser preenchido.", txtCpf);
            return;
        }

        if (email.isEmpty()) {
            erro("O email deve ser preenchido.", txtEmail);
            return;
        }

        if (telefone.isEmpty()) {
            erro("O telefone deve ser preenchido.", txtTelefone);
            return;
        }

        if (turma == null) {
            JOptionPane.showMessageDialog(this, "A turma deve ser selecionada.", "Dados inválidos",
                    JOptionPane.WARNING_MESSAGE);
            cbTurma.requestFocus();
            return;
        }

        boolean edicao = clienteEmEdicao != null;

        if (!edicao && clienteService.buscarPorCpf(cpf) != null) {
            erro("Já existe um cliente com o CPF " + cpf + ".", txtCpf);
            return;
        }

        if (!edicao) {
            Cliente cliente = new Cliente();
            cliente.setNome(nome);
            cliente.setCpf(cpf);
            cliente.setEmail(email);
            cliente.setTelefone(telefone);
            cliente.setTurmaMatriculada(turma);
            clienteService.salvarCliente(cliente);
        } else {
            clienteEmEdicao.setNome(nome);
            clienteEmEdicao.setEmail(email);
            clienteEmEdicao.setTelefone(telefone);
            clienteEmEdicao.setTurmaMatriculada(turma);
            clienteService.atualizarCliente(clienteEmEdicao);
        }

        JOptionPane.showMessageDialog(this,
                edicao ? "Cliente atualizado com sucesso!" : "Cliente cadastrado com sucesso!",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);

        if (aoSalvar != null) {
            aoSalvar.run();
        }

        dispose();
    }

    private void erro(String mensagem, JTextField campo) {
        JOptionPane.showMessageDialog(this, mensagem, "Dados inválidos", JOptionPane.WARNING_MESSAGE);
        campo.requestFocus();
    }

    public void abrir() {
        setVisible(true);
    }
}