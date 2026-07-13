package view;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JCheckBox;
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
import model.Turma;
import model.enums.Turno;

public class MenuTurma {
    Contexto contexto;

    public MenuTurma(Contexto contexto) {
        this.contexto = contexto;
    }

    public void menu() {
        int opcao;

        do {
            opcao = Integer.parseInt(JOptionPane.showInputDialog(
                    "========= MENU TURMA =========\n"
                            + "1 - Criar Turma\n"
                            + "2 - Listar Turmas\n"
                            + "3 - Buscar Turma por ID\n"
                            + "4 - Atualizar Turma\n"
                            + "5 - Inativar Turma\n"
                            + "6 - Excluir Turma\n"
                            + "0 - Voltar\n\n"
                            + "Escolha uma opção:"));

            switch (opcao) {

                case 1:
                    cadastrarTurma();
                    break;

                case 2:
                    listarTurmas();
                    break;

                case 3:
                    buscarTurma();
                    break;

                case 4:
                    atualizarTurma();
                    break;

                case 0:
                    JOptionPane.showMessageDialog(
                            null,
                            "Voltando ao menu principal...");
                    break;

                default:
                    JOptionPane.showMessageDialog(
                            null,
                            "Opção inválida! Digite novamente.",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    break;
            }
        } while (opcao != 0);
    }

    public void cadastrarTurma() {
        Turma novaTurma = new Turma();

        int id = Integer.parseInt(JOptionPane.showInputDialog("Digite o ID da turma:"));
        String nome = JOptionPane.showInputDialog("Digite o nome da turma:");
        int qtdAlunos = Integer.parseInt(JOptionPane.showInputDialog("Digite a quantidade de alunos da turma:"));
        Turno turno = lerTurnoTurma();

        novaTurma.setId(id);
        novaTurma.setNomeTurma(nome);
        novaTurma.setQtdALunos(qtdAlunos);
        novaTurma.setTurno(turno);
        novaTurma.setAtivo(true);

        if (contexto.getTurmaRepository().buscarPorId(novaTurma.getId()) != null) {
            JOptionPane.showMessageDialog(null, "Já existe uma turma com esse ID. Por favor, escolha outro ID.");
            return;
        }

        contexto.getTurmaRepository().salvarTurma(novaTurma);
    }

    public void listarTurmas() {
        List<Turma> turmas = contexto.getTurmaRepository().listarTurmas();

        if (turmas.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhuma turma cadastrada.");
            return;
        }

        String[] colunas = { "ID", "Nome", "Quantidade de Alunos", "Turno", "Esta Ativa?" };

        DefaultTableModel model = new DefaultTableModel(colunas, 0);

        for (Turma t : turmas) {
            model.addRow(new Object[] {
                    t.getId(),
                    t.getNomeTurma(),
                    t.getQtdALunos(),
                    t.getTurno(),
                    t.isAtivo() ? "Sim" : "Não" });
        }

        JTable tabela = new JTable(model);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setPreferredSize(new DimensionUIResource(600, 300));

        JOptionPane.showMessageDialog(null, scroll);
    }

    public void buscarTurma() {
        int id = Integer.parseInt(JOptionPane.showInputDialog("Digite o ID da turma a ser buscada:"));

        Turma turma = contexto.getTurmaRepository().buscarPorId(id);

        if (turma == null) {
            JOptionPane.showMessageDialog(null,
                    "Turma encontrada:\n"
                            + "ID: " + turma.getId() + "\n"
                            + "Nome: " + turma.getNomeTurma() + "\n"
                            + "Quantidade de Alunos: " + turma.getQtdALunos() + "\n"
                            + "Turno: " + turma.getTurno() + "\n"
                            + "Está Ativa? " + (turma.isAtivo() ? "Sim" : "Não"));
        } else {
            JOptionPane.showMessageDialog(null, "Turma não encontrada.");
            return;

        }

    }

    public void atualizarTurma() {
        int id = Integer.parseInt(JOptionPane.showInputDialog("Digite o ID da turma a ser atualizada:"));

        Turma turma = contexto.getTurmaRepository().buscarPorId(id);

        if (turma == null) {
            JOptionPane.showMessageDialog(null, "Turma não encontrada.");
            return;
        }

        JTextField txtNome = new JTextField(turma.getNomeTurma());
        JTextField txtQtdAlunos = new JTextField(String.valueOf(turma.getQtdALunos()));
        JComboBox<Turno> comboTurno = new JComboBox<>(Turno.values());
        comboTurno.setSelectedItem(turma.getTurno());
        JCheckBox chkAtivo = new JCheckBox("Ativo", turma.isAtivo());
        chkAtivo.setSelected(turma.isAtivo());

        JPanel painel = new JPanel(new GridLayout(0, 1));

        painel.add(new JLabel("Nome:"));
        painel.add(txtNome);

        painel.add(new JLabel("Quantidade de Alunos:"));
        painel.add(txtQtdAlunos);

        painel.add(new JLabel("Turno:"));
        painel.add(comboTurno);

        painel.add(new JLabel("Esta Ativa?"));
        painel.add(chkAtivo);

        int opcao = JOptionPane.showConfirmDialog(
                null,
                painel,
                "Atualizar Turma",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (opcao == JOptionPane.OK_OPTION) {
            turma.setNomeTurma(txtNome.getText());
            turma.setQtdALunos(Integer.parseInt(txtQtdAlunos.getText()));
            turma.setTurno((Turno) comboTurno.getSelectedItem());
            turma.setAtivo(chkAtivo.isSelected());

            JOptionPane.showMessageDialog(null, "Turma atualizada com sucesso!");
        }

    }

    public Turno lerTurnoTurma() {
        String[] opcoes = { "Matutino", "Vespertino", "Noturno", "Integral" };

        while (true) {
            int escolha = JOptionPane.showOptionDialog(
                    null,
                    "Escolha o turno da turma:",
                    "Turno",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    opcoes,
                    opcoes[0]);

            switch (escolha) {
                case 0:
                    return Turno.MATUTINO;
                case 1:
                    return Turno.VESPERTINO;
                case 2:
                    return Turno.NOTURNO;
                case 3:
                    return Turno.INTEGRAL;
                default:
                    JOptionPane.showMessageDialog(null, "Opção inválida. Por favor, selecione novamente.");
            }
        }
    }
}