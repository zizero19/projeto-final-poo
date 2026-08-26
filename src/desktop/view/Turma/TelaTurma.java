package desktop.view.Turma;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import model.Turma;
import model.enums.DiaSemana;
import model.enums.Turno;
import service.TurmaService;

public class TelaTurma extends JDialog {

    private final TurmaService turmaService;

    private final JTable tabela;
    private final DefaultTableModel model;
    private List<Turma> turmasExibidas;

    private final JTextField txtBusca = new JTextField();

    public TelaTurma(TurmaService turmaService) {
        super((Frame) null, "Gerenciamento de Turmas", true);
        this.turmaService = turmaService;

        String[] colunas = { "ID", "Nome", "Qtd. Alunos", "Turno", "Dias de Aula", "Ativa" };
        model = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(model);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        configurarTela();
        carregarTurmas(turmaService.listarTurmas());
    }

    private void configurarTela() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel topo = new JPanel(new GridLayout(2, 1, 5, 5));
        topo.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));

        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCadastrar = new JButton("Cadastrar Turma");
        JButton btnEditar = new JButton("Editar Turma");
        JButton btnAtivarInativar = new JButton("Ativar/Inativar Turma");
        JButton btnExcluir = new JButton("Excluir Turma");
        JButton btnResumoHoje = new JButton("Resumo de Hoje");

        btnCadastrar.addActionListener(e -> cadastrarTurma());
        btnEditar.addActionListener(e -> editarTurma());
        btnAtivarInativar.addActionListener(e -> alternarAtivo());
        btnExcluir.addActionListener(e -> excluirTurma());
        btnResumoHoje.addActionListener(e -> resumoTurmasHoje());

        painelAcoes.add(btnCadastrar);
        painelAcoes.add(btnEditar);
        painelAcoes.add(btnAtivarInativar);
        painelAcoes.add(btnExcluir);
        painelAcoes.add(btnResumoHoje);

        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBuscar = new JButton("Buscar");
        JButton btnLimpar = new JButton("Limpar");
        txtBusca.setColumns(20);

        btnBuscar.addActionListener(e -> pesquisar());
        btnLimpar.addActionListener(e -> {
            txtBusca.setText("");
            carregarTurmas(turmaService.listarTurmas());
        });

        painelBusca.add(new JLabel("Buscar por nome:"));
        painelBusca.add(txtBusca);
        painelBusca.add(btnBuscar);
        painelBusca.add(btnLimpar);

        topo.add(painelAcoes);
        topo.add(painelBusca);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Turma selecionada = obterTurmaSelecionada();
                    if (selecionada != null) {
                        new FormDetalheTurma(selecionada).abrir();
                    }
                }
            }
        });

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        rodape.add(btnFechar);

        add(topo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }

    private void carregarTurmas(List<Turma> turmas) {
        turmasExibidas = turmas;
        model.setRowCount(0);

        for (Turma t : turmas) {
            model.addRow(new Object[] {
                    t.getId(),
                    t.getNomeTurma(),
                    t.getQtdALunos(),
                    t.getTurno(),
                    formatarDiasAula(t.getDiasAula()),
                    t.isAtivo() ? "Sim" : "Não"
            });
        }
    }

    private Turma obterTurmaSelecionada() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            return null;
        }
        return turmasExibidas.get(linha);
    }

    private void cadastrarTurma() {
        new FormTurma(turmaService, null, () -> carregarTurmas(turmaService.listarTurmas())).abrir();
    }

    private void editarTurma() {
        Turma selecionada = obterTurmaSelecionada();
        if (selecionada == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma turma na tabela.", "Nenhuma turma selecionada",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        new FormTurma(turmaService, selecionada, () -> carregarTurmas(turmaService.listarTurmas())).abrir();
    }

    private void alternarAtivo() {
        Turma selecionada = obterTurmaSelecionada();
        if (selecionada == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma turma na tabela.", "Nenhuma turma selecionada",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean novoStatus = !selecionada.isAtivo();
        String acao = novoStatus ? "ativar" : "inativar";

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Deseja " + acao + " a turma \"" + selecionada.getNomeTurma() + "\"?",
                "Confirmação", JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            selecionada.setAtivo(novoStatus);
            if (turmaService.atualizarTurma(selecionada)) {
                carregarTurmas(turmaService.listarTurmas());
            } else {
                JOptionPane.showMessageDialog(this, "Não foi possível atualizar a turma.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void excluirTurma() {
        Turma selecionada = obterTurmaSelecionada();
        if (selecionada == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma turma na tabela.", "Nenhuma turma selecionada",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String mensagem = "Deseja realmente excluir a turma abaixo?\n\n"
                + "Nome: " + selecionada.getNomeTurma() + "\n"
                + "Turno: " + selecionada.getTurno();

        int confirmacao = JOptionPane.showConfirmDialog(this, mensagem, "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacao == JOptionPane.YES_OPTION) {
            boolean excluida = turmaService.excluirTurma(selecionada.getId());
            if (excluida) {
                JOptionPane.showMessageDialog(this, "Turma excluída com sucesso!");
                carregarTurmas(turmaService.listarTurmas());
            } else {
                JOptionPane.showMessageDialog(this, "Não foi possível excluir a turma.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void pesquisar() {
        String texto = txtBusca.getText().trim();
        if (texto.isEmpty()) {
            carregarTurmas(turmaService.listarTurmas());
            return;
        }
        carregarTurmas(turmaService.buscarTurmasPorNome(texto));
    }

    private String formatarDiasAula(List<DiaSemana> dias) {
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

    private void resumoTurmasHoje() {
        List<Turma> turmas = turmaService.listarTurmas();
        DiaSemana diaAtual = obterDiaSemanaAtual();

        if (diaAtual == null) {
            JOptionPane.showMessageDialog(this, "A faculdade não possui atividades aos sábados e domingos.");
            return;
        }

        List<Turma> turmasDaNoite = new ArrayList<>();
        for (Turma turma : turmas) {
            if (!turma.isAtivo() || !turma.getDiasAula().contains(diaAtual)) {
                continue;
            }
            if (turma.getTurno() != Turno.NOTURNO && turma.getTurno() != Turno.INTEGRAL) {
                continue;
            }
            turmasDaNoite.add(turma);
        }

        if (turmasDaNoite.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nenhuma turma ativa opera no período noturno hoje (" + diaAtual + ").");
            return;
        }

        String[] colunas = { "Turma", "Turno", "Quantidade de Alunos" };
        DefaultTableModel modelResumo = new DefaultTableModel(colunas, 0);
        int totalAlunos = 0;

        for (Turma turma : turmasDaNoite) {
            modelResumo.addRow(new Object[] { turma.getNomeTurma(), turma.getTurno(), turma.getQtdALunos() });
            totalAlunos += turma.getQtdALunos();
        }

        JTable tabelaResumo = new JTable(modelResumo);
        tabelaResumo.setEnabled(false);
        JScrollPane scroll = new JScrollPane(tabelaResumo);
        scroll.setPreferredSize(new Dimension(600, 250));

        JLabel lblDia = new JLabel("Dia: " + diaAtual);
        JLabel lblTurmas = new JLabel("Turmas em operação à noite: " + turmasDaNoite.size());
        JLabel lblTotal = new JLabel("Total estimado de alunos: " + totalAlunos);

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.add(lblDia);
        painel.add(Box.createVerticalStrut(10));
        painel.add(lblTurmas);
        painel.add(Box.createVerticalStrut(10));
        painel.add(scroll);
        painel.add(Box.createVerticalStrut(10));
        painel.add(lblTotal);

        JOptionPane.showMessageDialog(this, painel, "Resumo das Turmas de Hoje", JOptionPane.INFORMATION_MESSAGE);
    }

    private DiaSemana obterDiaSemanaAtual() {
        DayOfWeek dia = LocalDate.now().getDayOfWeek();
        switch (dia) {
            case MONDAY:
                return DiaSemana.SEGUNDA;
            case TUESDAY:
                return DiaSemana.TERCA;
            case WEDNESDAY:
                return DiaSemana.QUARTA;
            case THURSDAY:
                return DiaSemana.QUINTA;
            case FRIDAY:
                return DiaSemana.SEXTA;
            default:
                return null;
        }
    }

    public void abrir() {
        setVisible(true);
    }
}