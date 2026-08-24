package view.Turma;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import app.Contexto;
import model.Turma;
import model.enums.DiaSemana;
import model.enums.Turno;

public class FormTurma extends JDialog {
    private final Contexto contexto;

    private final JTextField txtNome = new JTextField(25);
    private final JTextField txtQtdAlunos = new JTextField(25);

    private final JComboBox<Turno> cbTurno = new JComboBox<>(Turno.values());

    private final List<JCheckBox> checksDias = new ArrayList<>();

    public FormTurma(Contexto contexto) {
        super((Frame) null, "Cadastro de Turma", true);
        this.contexto = contexto;
        configurarTela();
    }

    private void configurarTela() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 380);
        setLocationRelativeTo(null);

        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(25, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        painel.add(new JLabel("Nome da turma:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        painel.add(txtNome, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;

        painel.add(new JLabel("Quantidade estimada de alunos:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        painel.add(txtQtdAlunos, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;

        painel.add(new JLabel("Turno:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        painel.add(cbTurno, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        painel.add(new JLabel("Dias de aula:"), gbc);
        JPanel painelDias = new JPanel(new GridBagLayout());

        GridBagConstraints diasGbc = new GridBagConstraints();

        diasGbc.anchor = GridBagConstraints.WEST;
        diasGbc.insets = new Insets(4, 4, 4, 20);

        int coluna = 0;
        int linha = 0;

        for (DiaSemana dia : DiaSemana.values()) {
            JCheckBox check = new JCheckBox(dia.toString());

            check.setName(dia.name());
            checksDias.add(check);

            diasGbc.gridx = coluna;
            diasGbc.gridy = linha;

            painelDias.add(check, diasGbc);

            coluna++;
            if (coluna == 2) {
                coluna = 0;
                linha++;
            }
        }

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.weighty = 1;

        painel.add(painelDias, gbc);

        JButton btnCancelar = new JButton("Cancelar");
        JButton btnCadastrar = new JButton("Cadastrar");

        btnCancelar.addActionListener(e -> {
            dispose();
        });

        btnCadastrar.addActionListener(e -> {
            cadastrar();
        });

        JPanel painelBotoes = new JPanel();

        painelBotoes.add(btnCancelar);
        painelBotoes.add(btnCadastrar);

        setLayout(new java.awt.BorderLayout());
        add(painel, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void cadastrar() {
        String nome = txtNome.getText().trim();
        String qtdTexto = txtQtdAlunos.getText().trim();
        Turno turno = (Turno) cbTurno.getSelectedItem();
        List<DiaSemana> dias = obterDiasSelecionados();

        if (nome.isEmpty()) {
            erro("O nome da turma deve ser preenchido.", txtNome);
            return;
        }

        if (qtdTexto.isEmpty()) {
            erro("A quantidade de alunos deve ser preenchida.", txtQtdAlunos);
            return;
        }

        int qtdAlunos;

        try {
            qtdAlunos = Integer.parseInt(qtdTexto);
        } catch (NumberFormatException e) {
            erro("Digite uma quantidade inteira válida.", txtQtdAlunos);
            return;
        }

        if (qtdAlunos <= 0) {
            erro("A quantidade de alunos deve ser maior que zero.", txtQtdAlunos);
            return;
        }

        if (turno == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione o turno da turma.",
                    "Dados inválidos",
                    JOptionPane.WARNING_MESSAGE);
            cbTurno.requestFocus();
            return;
        }

        if (dias.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione pelo menos um dia de aula.",
                    "Dados inválidos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (contexto.getTurmaRepository().buscarTurma(nome) != null) {
            erro("Já existe uma turma com esse nome.", txtNome);
            return;
        }

        Turma turma = new Turma(nome, qtdAlunos, turno, true, dias);

        contexto.getTurmaRepository().salvarTurma(turma);

        JOptionPane.showMessageDialog(
                this,
                "Turma cadastrada com sucesso!",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private List<DiaSemana> obterDiasSelecionados() {
        List<DiaSemana> dias = new ArrayList<>();

        for (JCheckBox check : checksDias) {
            if (check.isSelected()) {
                dias.add(DiaSemana.valueOf(check.getName()));
            }
        }
        return dias;
    }

    private void erro(String mensagem, JTextField campo) {
        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Dados inválidos",
                JOptionPane.WARNING_MESSAGE);
        campo.requestFocus();
    }

    public void abrir() {
        setVisible(true);
    }
}