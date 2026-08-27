package service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import model.Caixa;
import model.Pedido;
import repository.CaixaRepository;
import repository.PedidoRepository;

public class CaixaService {

    private final CaixaRepository caixaRepository;
    private final PedidoRepository pedidoRepository;

    public CaixaService(CaixaRepository caixaRepository, PedidoRepository pedidoRepository) {
        this.caixaRepository = caixaRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public Caixa abrirCaixa() {
        if (caixaRepository.buscarCaixaAberto() != null) {
            throw new RegraNegocioException("Já existe um caixa aberto. Feche-o antes de abrir um novo.");
        }

        Caixa caixa = new Caixa();
        boolean salvo = caixaRepository.salvarCaixa(caixa);
        if (!salvo) {
            throw new RegraNegocioException("Não foi possível abrir o caixa.");
        }
        return caixa;
    }

    public Caixa fecharCaixa() {
        Caixa caixaAberto = caixaRepository.buscarCaixaAberto();
        if (caixaAberto == null) {
            throw new RegraNegocioException("Não há caixa aberto no momento.");
        }

        caixaRepository.fecharCaixa(caixaAberto.getId(), LocalDateTime.now());
        return caixaRepository.buscarPorId(caixaAberto.getId());
    }

    public Caixa buscarCaixaAberto() {
        return caixaRepository.buscarCaixaAberto();
    }

    public Caixa buscarPorId(Long id) {
        return caixaRepository.buscarPorId(id);
    }

    public Caixa buscarPorData(LocalDate data) {
        return caixaRepository.buscarPorData(data);
    }

    public List<Caixa> listarCaixas() {
        return caixaRepository.listarCaixas();
    }

    public List<Caixa> listarCaixasFechados() {
        return caixaRepository.listarCaixas().stream()
                .filter(c -> !c.isAberto())
                .toList();
    }

    public List<Pedido> listarPedidosDoCaixa(Long caixaId) {
        if (caixaId == null) {
            return List.of();
        }
        return pedidoRepository.buscarPedidosPorCaixa(caixaId);
    }
}
