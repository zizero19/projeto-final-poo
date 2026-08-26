package desktop.view.Turma;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Turma;
import model.enums.DiaSemana;

public class FormDetalheTurma extends JDialog {

    public FormDetalheTurma(Turma turma) {
        super((Frame) null, "Detalhes da Turma", true);
        configurarTela(turma);
    }

    private void configurarTela(Turma turma) {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(440, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel painel = new JPanel(new GridLayout(0, 2, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        painel.add(new JLabel("ID:"));
        painel.add(new JLabel(String.valueOf(turma.getId())));

        painel.add(new JLabel("Nome:"));
        painel.add(new JLabel(turma.getNomeTurma()));

        painel.add(new JLabel("Quantidade de alunos:"));
        painel.add(new JLabel(String.valueOf(turma.getQtdALunos())));

        painel.add(new JLabel("Turno:"));
        painel.add(new JLabel(turma.getTurno().toString()));

        painel.add(new JLabel("Dias de aula:"));
        painel.add(new JLabel(formatarDias(turma.getDiasAula())));

        painel.add(new JLabel("Está ativa?"));
        painel.add(new JLabel(turma.isAtivo() ? "Sim" : "Não"));

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());
        JPanel botoes = new JPanel();
        botoes.add(btnFechar);

        add(painel, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);
    }

    private String formatarDias(java.util.List<DiaSemana> dias) {
        if (dias == null || dias.isEmpty()) {
            return "Nenhum dia selecionado";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dias.size(); i++) {
            sb.append(dias.get(i));
            if (i < dias.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public void abrir() {
        setVisible(true);
    }
}