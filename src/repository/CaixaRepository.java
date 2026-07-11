package repository;

import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import model.Caixa;

public class CaixaRepository {
    private ArrayList<Caixa> caixas;

    public CaixaRepository() {
        this.caixas = new ArrayList<>();
    }


    public void abrirCaixa(Caixa caixa) {

    

    if (caixa == null) {
        JOptionPane.showMessageDialog(null, "Caixa não pode ser nulo.");
        return;
    }

    if (caixa.isAberto()) {
        JOptionPane.showMessageDialog(null, "O caixa já está aberto.");
        return;
    }

    caixa.abrir();
    caixas.add(caixa);

    JOptionPane.showMessageDialog(null,
            "Caixa aberto com sucesso!\n\n" + caixa);
}



    public void salvar(Caixa caixa) {

    if (caixa == null) {
        JOptionPane.showMessageDialog(null,
                "Caixa não pode ser nulo.");
        return;
    }

    caixas.add(caixa);

    JOptionPane.showMessageDialog(null,
            "Caixa registrado com sucesso.");
}



    public void listar() {
        if (caixas.isEmpty()) {
           JOptionPane.showMessageDialog(null, "Nenhum caixa cadastrado.");
            return;
        }

        for (Caixa caixa : caixas) {
            JOptionPane.showMessageDialog(null, caixa);
        }
    }


    public void buscarPorId(int id) {
        for (Caixa caixa : caixas) {
            if (caixa.getId() == id) {
               JOptionPane.showMessageDialog(null, "Caixa encontrado:\n" + caixa);
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Caixa com ID " + id + " não encontrado.");
    }


   public void buscarPorData(LocalDate data) {

    boolean encontrou = false;

    for (Caixa caixa : caixas) {

        if (caixa.getAbertura() != null &&
            caixa.getAbertura().toLocalDate().equals(data)) {

            JOptionPane.showMessageDialog(null, "Caixa encontrado na data " + data + ":\n" + caixa);
            encontrou = true;
        }
    }

    if (!encontrou) {
        JOptionPane.showMessageDialog(null, "Nenhum caixa encontrado na data " + data);
    }
}



}
