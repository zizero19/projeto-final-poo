package repository;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import model.Turma;

public class TurmaRepository {
    private ArrayList<Turma> turmas;

    public TurmaRepository() {
        this.turmas = new ArrayList<>();
    }

    public void salvarTurma(Turma turma) {
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

    public ArrayList<Turma> listarTurmas() {
        return turmas;
    }


    // ver soluçao usando objeto para nao dar erro
    public Turma buscarPorId(int id) {
        for (Turma turma : turmas) {
            if (turma.getId() == id) {
                return turma;
            } 
           
        }
        return null;
    }

    public void excluirTurma(int id) {
        Turma turma = buscarPorId(id);
        if (turma != null) {
            turmas.remove(turma);
        } else {
            JOptionPane.showMessageDialog(null, "Turma não encontrada.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

}
