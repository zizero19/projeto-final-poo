package repository;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import model.Turma;
import model.enums.Turno;

public class TurmaRepository {
    private ArrayList<Turma> turmas;

    public TurmaRepository() {
        this.turmas = new ArrayList<>();
    }

    public void salvar(Turma turma) {
        if (turma == null) {
            JOptionPane.showMessageDialog(null, "Turma não pode ser nula.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (buscarPorId(turma.getId()) != null) {
            JOptionPane.showMessageDialog(null, "Turma com ID " + turma.getId() + " já existe.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        turmas.add(turma);
    }

    public ArrayList<Turma> listar() {
        return turmas;
    }

    public Turma buscarPorId(int id) {
        for (Turma turma : turmas) {
            if (turma.getId() == id) {
                return turma;
            }
        }
        return null;
    }

    public void inativarTurma(int id) {
        Turma turma = buscarPorId(id);
        if (turma != null) {
            turma.setAtivo(false);
        }
    }

    public void atualizarTurma(int id, String nomeTurma, int qtdALunos, Turno turno, boolean isAtivo) {
        Turma turma = buscarPorId(id);
        if (turma != null) {
            turma.setNomeTurma(nomeTurma);
            turma.setQtdALunos(qtdALunos);
            turma.setTurno(turno);
            turma.setAtivo(isAtivo);
        }
    }

    public void excluirTurma(int id) {
        Turma turma = buscarPorId(id);
        if (turma != null) {
            turmas.remove(turma);
        }
    }

}
