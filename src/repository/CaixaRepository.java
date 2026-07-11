package repository;

import java.util.ArrayList;

import model.Caixa;

public class CaixaRepository {
    private ArrayList<Caixa> caixas;

    public CaixaRepository() {
        this.caixas = new ArrayList<>();
    }

    public void salvar(Caixa caixa) {
        if (caixa == null) {
            System.out.println("Caixa não pode ser nulo.");
            return;
        }

        if (caixa.isAberto()) {
            System.out.println("Caixa ainda esta aberto.");
            return;
        }

        caixas.add(caixa);
    }

    // listar
    // buscar por id
    // buscar por periodo

}
