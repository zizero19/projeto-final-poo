package view;

import java.awt.GridLayout;
import java.util.ArrayList;
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
import model.enums.DiaSemana;
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
                            + "3 - Buscar Turma\n"
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
                    lerFormaBusca();
                    break;

                case 4:
                    atualizarTurma();
                    break;

                case 5:
                    inativarTurma();
                    break;

                case 6:
                    excluirTurma();
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
        String nome = JOptionPane.showInputDialog("Digite o nome da turma:");
        int qtdAlunos = Integer.parseInt(JOptionPane.showInputDialog("Digite a quantidade de alunos da turma:"));
        Turno turno = lerTurnoTurma();
        List<DiaSemana> diasAula = lerDiasAulaTurma();
        novaTurma.setNomeTurma(nome);
        novaTurma.setQtdALunos(qtdAlunos);
        novaTurma.setTurno(turno);
        novaTurma.setDiasAula(diasAula);
        novaTurma.setAtivo(true);

        if (contexto.getTurmaRepository().buscarTurma(novaTurma.getId()) != null) {
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

        String[] colunas = { "ID", "Nome", "Quantidade de Alunos", "Turno", "Dias de Aula", "Esta Ativa?" };

        DefaultTableModel model = new DefaultTableModel(colunas, 0);

        for (Turma t : turmas) {
            model.addRow(new Object[] {
                    t.getId(),
                    t.getNomeTurma(),
                    t.getQtdALunos(),
                    t.getTurno(),
                    formatarDiasAula(t.getDiasAula()),
                    t.isAtivo() ? "Sim" : "Não" });
        }

        JTable tabela = new JTable(model);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setPreferredSize(new DimensionUIResource(600, 300));

        JOptionPane.showMessageDialog(null, scroll);
    }

    public void buscarTurmaPorId() {
        int id = Integer.parseInt(JOptionPane.showInputDialog("Digite o ID da turma a ser buscada:"));

        Turma turma = contexto.getTurmaRepository().buscarTurma(id);

        if (turma != null) {
            JOptionPane.showMessageDialog(null,
                    "Turma encontrada:\n"
                            + "ID: " + turma.getId() + "\n"
                            + "Nome: " + turma.getNomeTurma() + "\n"
                            + "Quantidade de Alunos: " + turma.getQtdALunos() + "\n"
                            + "Turno: " + turma.getTurno() + "\n"
                            + "Dias de Aula: " + formatarDiasAula(turma.getDiasAula()) + "\n"
                            + "Está Ativa? " + (turma.isAtivo() ? "Sim" : "Não"));
        } else {
            JOptionPane.showMessageDialog(null, "Turma não encontrada.");
            return;

        }

    }

    public void atualizarTurma() {
        int id = Integer.parseInt(JOptionPane.showInputDialog("Digite o ID da turma a ser atualizada:"));

        Turma turma = contexto.getTurmaRepository().buscarTurma(id);

        if (turma == null) {
            JOptionPane.showMessageDialog(null, "Turma não encontrada.");
            return;
        }

        JTextField txtNome = new JTextField(turma.getNomeTurma());
        JTextField txtQtdAlunos = new JTextField(String.valueOf(turma.getQtdALunos()));
        JComboBox<Turno> comboTurno = new JComboBox<>(Turno.values());
        comboTurno.setSelectedItem(turma.getTurno());
        JPanel painelDias = criarPainelDiasAula(turma.getDiasAula());
        JCheckBox chkAtivo = new JCheckBox("Ativo/Desativado", turma.isAtivo());
        chkAtivo.setSelected(turma.isAtivo());

        JPanel painel = new JPanel(new GridLayout(0, 1));

        painel.add(new JLabel("Nome:"));
        painel.add(txtNome);

        painel.add(new JLabel("Quantidade de Alunos:"));
        painel.add(txtQtdAlunos);

        painel.add(new JLabel("Turno:"));
        painel.add(comboTurno);

        painel.add(new JLabel("Dias de Aula:"));
        painel.add(painelDias);

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
            turma.setDiasAula(obterDiasSelecionados(painelDias));
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

    public List<DiaSemana> lerDiasAulaTurma() {
        JPanel painel = criarPainelDiasAula(new ArrayList<>());

        int opcao = JOptionPane.showConfirmDialog(
                null,
                painel,
                "Dias de Aula",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (opcao != JOptionPane.OK_OPTION) {
            return new ArrayList<>();
        }

        List<DiaSemana> diasSelecionados = obterDiasSelecionados(painel);

        if (diasSelecionados.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Selecione pelo menos um dia de aula.");
            return lerDiasAulaTurma();
        }

        return diasSelecionados;
    }

    private JPanel criarPainelDiasAula(List<DiaSemana> diasSelecionados) {
        JPanel painel = new JPanel(new GridLayout(0, 1));
        painel.add(new JLabel("Selecione os dias de aula:"));

        for (DiaSemana dia : DiaSemana.values()) {
            JCheckBox checkBox = new JCheckBox(dia.toString());
            checkBox.setSelected(diasSelecionados.contains(dia));
            checkBox.setName(dia.name());
            painel.add(checkBox);
        }

        return painel;
    }

    private List<DiaSemana> obterDiasSelecionados(JPanel painel) {
        List<DiaSemana> diasSelecionados = new ArrayList<>();

        for (java.awt.Component componente : painel.getComponents()) {
            if (componente instanceof JCheckBox) {
                JCheckBox checkBox = (JCheckBox) componente;

                if (checkBox.isSelected()) {
                    diasSelecionados.add(DiaSemana.valueOf(checkBox.getName()));
                }
            }
        }

        return diasSelecionados;
    }

    private String formatarDiasAula(List<DiaSemana> dias) {
        if (dias == null || dias.isEmpty()) {
            return "Nenhum dia selecionado";
        }

        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < dias.size(); i++) {
            resultado.append(dias.get(i));

            if (i < dias.size() - 1) {
                resultado.append(", ");
            }
        }

        return resultado.toString();
    }

    public void inativarTurma() {
        int id = Integer.parseInt(JOptionPane.showInputDialog("Digite o ID da turma a ser buscada:"));

        Turma turma = contexto.getTurmaRepository().buscarTurma(id);

        if (turma == null) {
            JOptionPane.showMessageDialog(null, "Turma não encontrada para inativar.");
        } else {
            turma.setAtivo(false);
            JOptionPane.showMessageDialog(null, "Turma inativada com sucesso!");

        }
    }

    public void excluirTurma() {

        int id = Integer.parseInt(JOptionPane.showInputDialog("Digite o ID da turma a ser excluída:"));

        Turma turma = contexto.getTurmaRepository().buscarTurma(id);

        if (turma == null) {
            JOptionPane.showMessageDialog(null, "Turma não encontrada para exclusão.");
        } else {
            contexto.getTurmaRepository().excluirTurma(id);
            JOptionPane.showMessageDialog(null, "Turma excluída com sucesso!");
        }
    }

    public void buscarTurmaPorNome() {
        String nome = JOptionPane.showInputDialog("Digite o nome da turma a ser buscada:");

        Turma turmaEncontrada = contexto.getTurmaRepository().buscarTurma(nome);

        if (turmaEncontrada != null) {
            JOptionPane.showMessageDialog(null,
                    "Turma encontrada:\n"
                            + "ID: " + turmaEncontrada.getId() + "\n"
                            + "Nome: " + turmaEncontrada.getNomeTurma() + "\n"
                            + "Quantidade de Alunos: " + turmaEncontrada.getQtdALunos() + "\n"
                            + "Turno: " + turmaEncontrada.getTurno() + "\n"
                            + "Dias de Aula: " + turmaEncontrada.getDiasAula() + "\n"
                            + "Está Ativa? " + (turmaEncontrada.isAtivo() ? "Sim" : "Não"));
        } else {
            JOptionPane.showMessageDialog(null, "Turma não encontrada.");
            return;
        }
    }

    public void lerFormaBusca() {
        String[] opcoes = {
                "Buscar por Nome", "Buscar por Id", "Sair" };
        while (true) {
            int escolha = JOptionPane.showOptionDialog(
                    null,
                    "Escolha a forma de busca de Turma",
                    "Busca Turma",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    opcoes,
                    opcoes[0]);

            switch (escolha) {
                case 0:
                    buscarTurmaPorNome();
                    break;

                case 1:
                    buscarTurmaPorId();
                    break;

                case 2:
                    JOptionPane.showMessageDialog(null, "Saindo da busca de Turma.");
                    return;
            }
        }

    }

}