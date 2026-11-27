package br.ceub.poo.upa.servico;

import br.ceub.poo.upa.algoritmo.Busca;
import br.ceub.poo.upa.algoritmo.Ordenacao;
import br.ceub.poo.upa.estrutura.Fila;
import br.ceub.poo.upa.estrutura.FilaPrioridade;
import br.ceub.poo.upa.estrutura.Pilha;
import br.ceub.poo.upa.excecao.CpfInvalidoException;
import br.ceub.poo.upa.excecao.PacienteDuplicadoException;
import br.ceub.poo.upa.excecao.PacienteNaoEncontradoException;
import br.ceub.poo.upa.modelo.Atendimento;
import br.ceub.poo.upa.modelo.Enfermeiro;
import br.ceub.poo.upa.modelo.Medico;
import br.ceub.poo.upa.modelo.Paciente;
import br.ceub.poo.upa.modelo.Prioridade;
import br.ceub.poo.upa.modelo.SinaisVitais;
import br.ceub.poo.upa.modelo.StatusPaciente;
import br.ceub.poo.upa.modelo.Triagem;
import br.ceub.poo.upa.observador.ObservadorChamada;
import br.ceub.poo.upa.repositorio.PacienteRepository;
import br.ceub.poo.upa.triagem.ClassificadorRisco;
import br.ceub.poo.upa.util.GeradorSenha;
import br.ceub.poo.upa.util.ValidadorCpf;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * CORAÇÃO DO SISTEMA: regras de negócio do fluxo da UPA.
 *   chegada ──► [Fila de TRIAGEM (FIFO)] ──► triagem ──► [Fila de ATENDIMENTO (prioridade)]
 *           ──► chamada pelo médico ──► [Pilha de HISTÓRICO]
 * <p>SOLID – SRP: esta classe NÃO lê teclado nem imprime na tela (isso é do {@code MenuCli}).
 * Ela só aplica regras. Por isso pode ser testada com JUnit.</p>
 * <p>SOLID – DIP / Injeção de dependência: classificador, repositório e relógio chegam
 * PRONTOS pelo construtor. Nos testes passamos um {@code Clock.fixed(...)} para controlar o tempo.</p>
 */
public class UpaService {

    private final ClassificadorRisco classificador;
    private final PacienteRepository repositorio;
    private final Clock relogio;
    private final GeradorSenha geradorSenha = new GeradorSenha();

    private final Fila<Paciente> filaTriagem = new Fila<>();
    private final FilaPrioridade<Paciente> filaAtendimento = new FilaPrioridade<>();
    private final Pilha<Atendimento> historico = new Pilha<>();
    private final List<ObservadorChamada> observadores = new ArrayList<>();

    public UpaService(ClassificadorRisco classificador, PacienteRepository repositorio, Clock relogio) {
        this.classificador = classificador;
        this.repositorio = repositorio;
        this.relogio = relogio;
    }

    public Paciente cadastrarPaciente(String nome, String cpf, LocalDate dataNascimento)
            throws CpfInvalidoException, PacienteDuplicadoException {
        if (!ValidadorCpf.isValido(cpf)) {
            throw new CpfInvalidoException(cpf);
        }

        String cpfNormalizado = ValidadorCpf.normalizar(cpf);
        var anterior = repositorio.buscarPorCpf(cpfNormalizado);

        if (anterior.isPresent() && anterior.get().getStatus() != StatusPaciente.ATENDIDO) {
            throw new PacienteDuplicadoException(cpfNormalizado);
        }
        // Só geramos a senha depois de conferir o CPF. Assim, um cadastro recusado não consome um número.
        Paciente paciente = new Paciente(nome, cpfNormalizado, dataNascimento,
                geradorSenha.proxima(), LocalDateTime.now(relogio));
        repositorio.salvar(paciente);
        filaTriagem.enfileirar(paciente);
        return paciente;
    }

    public Triagem triarProximo(Enfermeiro enfermeiro, SinaisVitais sinais, String queixa) {
        // Se a classificação falhar, o paciente continua na fila de triagem.
        Prioridade prioridade = classificador.classificar(sinais);
        Paciente paciente = filaTriagem.desenfileirar();

        Triagem triagem = new Triagem(enfermeiro, sinais, prioridade, queixa,
                LocalDateTime.now(relogio));
        paciente.registrarTriagem(triagem);
        filaAtendimento.enfileirar(paciente);
        return triagem;
    }

    public Atendimento chamarProximo(Medico medico) {
        Paciente paciente = filaAtendimento.desenfileirar();
        paciente.marcarComoAtendido();

        Atendimento atendimento = new Atendimento(paciente, medico, LocalDateTime.now(relogio));
        historico.empilhar(atendimento);

        for (ObservadorChamada observador : observadores) {
            observador.aoChamarPaciente(atendimento);
        }
        return atendimento;
    }

    public void adicionarObservador(ObservadorChamada observador) {
        observadores.add(observador);
    }

    // ================================================================ RF04 — CONSULTAS DE FILA

    public List<Paciente> listarFilaTriagem() {
        return filaTriagem.paraLista();
    }

    public List<Paciente> listarFilaAtendimento() {
        return filaAtendimento.paraLista();
    }

    // ================================================================ RF05 — BUSCAS

    /**
     * Busca por CPF (sequencial, dentro do repositório). JÁ IMPLEMENTADO como modelo.
     */
    public Paciente buscarPorCpf(String cpf) throws PacienteNaoEncontradoException {
        String cpfNormalizado = ValidadorCpf.normalizar(cpf);
        return repositorio.buscarPorCpf(cpfNormalizado)
                .orElseThrow(() -> new PacienteNaoEncontradoException(cpf));
    }

    public Paciente buscarPorSenha(int senha) throws PacienteNaoEncontradoException {
        List<Paciente> pacientes = repositorio.listarTodos();
        int indice = Busca.buscaBinaria(pacientes, senha, Paciente::getSenha);

        if (indice == -1) {
            throw new PacienteNaoEncontradoException("senha " + senha);
        }
        return pacientes.get(indice);
    }

    // ================================================================ RF06 — RELATÓRIOS

    /**
     * Últimos n atendimentos, do mais recente para o mais antigo (vem da PILHA). JÁ IMPLEMENTADO.
     */
    public List<Atendimento> ultimosAtendimentos(int n) {
        List<Atendimento> todos = historico.paraLista();
        return todos.subList(0, Math.min(n, todos.size()));
    }

    public List<Atendimento> relatorioPorTempoEspera() {
        return Ordenacao.mergeSort(historico.paraLista(),
                Comparator.comparingLong(Atendimento::getTempoEsperaMin).reversed());
    }

    public Map<Prioridade, Integer> contarPorPrioridade() {
        Map<Prioridade, Integer> contagem = new EnumMap<>(Prioridade.class);
        for (Prioridade prioridade : Prioridade.values()) {
            contagem.put(prioridade, 0);
        }

        for (Paciente paciente : repositorio.listarTodos()) {
            Prioridade prioridade = paciente.getPrioridade();
            if (prioridade != null) {
                contagem.put(prioridade, contagem.get(prioridade) + 1);
            }
        }
        return contagem;
    }

    public double tempoMedioEsperaMin() {
        if (historico.estaVazia()) {
            return 0.0;
        }

        long totalMinutos = 0;
        for (Atendimento atendimento : historico.paraLista()) {
            totalMinutos += atendimento.getTempoEsperaMin();
        }
        return (double) totalMinutos / historico.tamanho();
    }
    public Paciente proximoParaTriagem() {
        return filaTriagem.espiar();
    }

    public String getNomeProtocolo() {
        return classificador.getNomeProtocolo();
    }
}