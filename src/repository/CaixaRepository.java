package repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.Caixa;

public class CaixaRepository {
    private List<Caixa> caixas;

    public CaixaRepository() {
        this.caixas = new ArrayList<>();
    }

    public boolean salvarCaixa(Caixa caixa) {
        if (caixa == null) {
            return false;
        }

        if (!caixa.isAberto()) {
            return false;
        }

        caixas.add(caixa);
        return true;
    }

    public List<Caixa> listarCaixas() {
        if (caixas.isEmpty()) {
            return null;
        }

        return caixas;
    }

    public Caixa buscarPorId(int id) {
        for (Caixa caixa : caixas) {
            if (caixa.getId() == id) {
                return caixa;
            }
        }

        return null;
    }

    public Caixa buscarPorData(LocalDate data) {
        for (Caixa caixa : caixas) {
            if (caixa.getAbertura() != null &&
                    caixa.getAbertura().toLocalDate().equals(data)) {
                return caixa;
            }
        }

        return null;
    }

    public Caixa buscarCaixaAberto() {
        for (Caixa caixa : caixas) {
            if (caixa.isAberto()) {
                return caixa;
            }
        }

        return null;
    }

}
