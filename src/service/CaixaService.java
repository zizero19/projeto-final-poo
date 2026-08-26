package service;

import java.time.LocalDate;
import java.util.List;

import model.Caixa;
import repository.CaixaRepository;

public class CaixaService {

    private final CaixaRepository caixaRepository;

    public CaixaService(CaixaRepository caixaRepository) {
        this.caixaRepository = caixaRepository;
    }

    public Caixa salvarCaixa(Caixa caixa) {
        if (caixa == null) {
            throw new IllegalArgumentException("Caixa não pode ser nulo.");
        }
        return caixaRepository.salvarCaixa(caixa) ? caixa : null;
    }

    public List<Caixa> listarCaixas() {
        return caixaRepository.listarCaixas();
    }

    public Caixa buscarPorId(Long id) {
        return caixaRepository.buscarPorId(id);
    }

    public Caixa buscarPorData(LocalDate data) {
        return caixaRepository.buscarPorData(data);
    }

    public Caixa buscarCaixaAberto() {
        return caixaRepository.buscarCaixaAberto();
    }
}
