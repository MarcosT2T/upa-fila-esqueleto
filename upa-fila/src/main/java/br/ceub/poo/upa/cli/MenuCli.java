package br.ceub.poo.upa.cli;

import br.ceub.poo.upa.excecao.FilaVaziaException;
import br.ceub.poo.upa.excecao.UpaException;
import br.ceub.poo.upa.modelo.Atendimento;
import br.ceub.poo.upa.modelo.Enfermeiro;
import br.ceub.poo.upa.modelo.Medico;
import br.ceub.poo.upa.modelo.Paciente;
import br.ceub.poo.upa.modelo.Prioridade;
import br.ceub.poo.upa.modelo.SinaisVitais;
import br.ceub.poo.upa.modelo.Triagem;
import br.ceub.poo.upa.servico.UpaService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Interface de linha de comando (CLI). Responsabilidade ÚNICA: conversar com o usuário
 * (ler e mostrar). Toda regra de negócio é delegada ao {@link UpaService}.
 *
 * <p>O laço principal já trata as exceções de forma centralizada:</p>
 * <ul>
 *   <li>{@link UpaException} → erro de negócio, mostrado ao usuário;</li>
 *   <li>{@link FilaVaziaException} → fila vazia;</li>
 *   <li>{@link UnsupportedOperationException} → TODO ainda não implementado.</li>
 * </ul>
 */
public class MenuCli {

    private final UpaService servico;
    private final Console console;
    private final Enfermeiro enfermeiroPlantao;
    private final List<Medico> medicosPlantao;

    public MenuCli(UpaService servico, Console console, Enfermeiro enfermeiroPlantao, List<Medico> medicosPlantao) {
        this.servico = servico;
        this.console = console;
        this.enfermeiroPlantao = enfermeiroPlantao;
        this.medicosPlantao = medicosPlantao;
    }

    public void iniciar() {
        int opcao;
        do {
            exibirMenu();
            opcao = console.lerInteiro("Opção", 0, 7);
            try {
                switch (opcao) {
                    case 1 -> cadastrarPaciente();
                    case 2 -> realizarTriagem();
                    case 3 -> chamarProximo();
                    case 4 -> exibirFilas();
                    case 5 -> buscarPaciente();
                    case 6 -> exibirRelatorios();
                    case 7 -> exibirEstatisticas();
                    case 0 -> System.out.println("\nEncerrando o sistema. Bom plantão!");
                    default -> System.out.println("Opção inválida.");
                }
            } catch (UpaException e) {
                System.out.println("\n  [ERRO] " + e.getMessage());
            } catch (FilaVaziaException e) {
                System.out.println("\n  [INFO] " + e.getMessage());
            } catch (UnsupportedOperationException e) {
                System.out.println("\n  [TODO] Ainda não implementado -> " + e.getMessage());
            }
            if (opcao != 0) {
                console.pausar();
            }
        } while (opcao != 0);
    }

    private void exibirMenu() {
        System.out.println();
        System.out.println("=================================================");
        System.out.println("   UPA 24h — GESTÃO DE FILA DE PACIENTES");
        System.out.println("   Protocolo: " + servico.getNomeProtocolo());
        System.out.println("=================================================");
        System.out.println("  1. Cadastrar paciente (recepção)");
        System.out.println("  2. Realizar triagem do próximo paciente");
        System.out.println("  3. Chamar próximo paciente (médico)");
        System.out.println("  4. Visualizar filas");
        System.out.println("  5. Buscar paciente (CPF ou senha)");
        System.out.println("  6. Relatórios de atendimento");
        System.out.println("  7. Estatísticas do plantão");
        System.out.println("  0. Sair");
        System.out.println("-------------------------------------------------");
    }

    // ---------------------------------------------------------------- 1 (JÁ IMPLEMENTADO: modelo)

    private void cadastrarPaciente() throws UpaException {
        System.out.println("\n--- CADASTRO DE PACIENTE ---");
        String nome = console.lerTexto("Nome completo");
        String cpf = console.lerTexto("CPF");
        LocalDate nascimento = console.lerData("Data de nascimento");

        Paciente paciente = servico.cadastrarPaciente(nome, cpf, nascimento);

        System.out.println("\n  [OK] Paciente cadastrado!");
        System.out.println("    Senha: " + paciente.getSenhaFormatada()
                + " | Chegada: " + paciente.getHorarioChegada().format(Console.FORMATO_HORA));
        System.out.println("    Posição na fila de triagem: " + servico.listarFilaTriagem().size());
    }

    // ---------------------------------------------------------------- 2

    private void realizarTriagem() throws UpaException {
        System.out.println("\n--- TRIAGEM (" + enfermeiroPlantao.getIdentificacao() + ") ---");
        Paciente paciente = servico.proximoParaTriagem();
        System.out.println("Próximo: " + paciente.getIdentificacao());

        String queixa = console.lerTexto("Queixa principal");
        double temperatura = console.lerDecimal("Temperatura (°C)");
        int frequencia = console.lerInteiro("Frequência cardíaca", 20, 250);
        int sistolica = console.lerInteiro("PA sistólica", 40, 300);
        int diastolica = console.lerInteiro("PA diastólica", 20, 200);
        int saturacao = console.lerInteiro("Saturação O2 (%)", 50, 100);
        int dor = console.lerInteiro("Dor", 0, 10);
        boolean consciente = console.lerSimNao("Consciente?");

        // Se os sinais forem inválidos, o paciente continua aguardando triagem.
        SinaisVitais sinais = new SinaisVitais(
                temperatura, frequencia, sistolica, diastolica, saturacao, dor, consciente);
        Triagem triagem = servico.triarProximo(enfermeiroPlantao, sinais, queixa);

        System.out.println("[OK] Classificação: " + triagem.getPrioridade());
    }

    // ---------------------------------------------------------------- 3

    private void chamarProximo() {
        System.out.println("\n--- CHAMAR PRÓXIMO PACIENTE ---");
        for (int i = 0; i < medicosPlantao.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, medicosPlantao.get(i).getIdentificacao());
        }

        int escolha = console.lerInteiro("Médico de plantão", 1, medicosPlantao.size());
        Medico medico = medicosPlantao.get(escolha - 1);

        // O serviço aciona o painel de chamada ao registrar o atendimento.
        Atendimento atendimento = servico.chamarProximo(medico);
        System.out.printf("Tempo de espera: %d min%n", atendimento.getTempoEsperaMin());
        System.out.println("Dentro do prazo: " + (atendimento.isDentroDoPrazo() ? "Sim" : "Não"));
    }
    // ---------------------------------------------------------------- 4 (JÁ IMPLEMENTADO: modelo)

    private void exibirFilas() {
        List<Paciente> triagem = servico.listarFilaTriagem();
        System.out.println("\n--- FILA DE TRIAGEM (" + triagem.size() + ") — ordem de chegada ---");
        if (triagem.isEmpty()) {
            System.out.println("  (vazia)");
        }
        for (int i = 0; i < triagem.size(); i++) {
            Paciente p = triagem.get(i);
            System.out.printf("  %2d. %s | %-25s | chegou %s%n", i + 1, p.getSenhaFormatada(),
                    p.getNome(), p.getHorarioChegada().format(Console.FORMATO_HORA));
        }

        List<Paciente> atendimento = servico.listarFilaAtendimento();
        System.out.println("\n--- FILA DE ATENDIMENTO (" + atendimento.size() + ") — por prioridade ---");
        if (atendimento.isEmpty()) {
            System.out.println("  (vazia)");
        }
        for (int i = 0; i < atendimento.size(); i++) {
            Paciente p = atendimento.get(i);
            System.out.printf("  %2d. [%-8s] %s | %-25s | chegou %s%n", i + 1, p.getPrioridade().name(),
                    p.getSenhaFormatada(), p.getNome(), p.getHorarioChegada().format(Console.FORMATO_HORA));
        }
    }

    // ---------------------------------------------------------------- 5

    private void buscarPaciente() throws UpaException {
        System.out.println("\n--- BUSCAR PACIENTE ---");
        int escolha = console.lerInteiro("Buscar por 1-CPF ou 2-Senha", 1, 2);

        Paciente paciente;
        if (escolha == 1) {
            paciente = servico.buscarPorCpf(console.lerTexto("CPF"));
        } else {
            int senha = console.lerInteiro("Número da senha", 1, Integer.MAX_VALUE);
            paciente = servico.buscarPorSenha(senha);
        }

        System.out.println(paciente.getIdentificacao());
        System.out.println("Idade: " + paciente.getIdade() + " anos");
        System.out.println("Status: " + paciente.getStatus());

        if (paciente.getTriagem() != null) {
            System.out.println("Triagem: " + paciente.getTriagem());
        }
    }
    // ---------------------------------------------------------------- 6

    private void exibirRelatorios() {
        System.out.println("\n--- RELATÓRIOS DE ATENDIMENTO ---");
        System.out.println("1. Últimos 5 atendimentos");
        System.out.println("2. Todos os atendimentos por tempo de espera");
        int escolha = console.lerInteiro("Relatório", 1, 2);

        List<Atendimento> atendimentos = escolha == 1
                ? servico.ultimosAtendimentos(5)
                : servico.relatorioPorTempoEspera();

        if (atendimentos.isEmpty()) {
            System.out.println("Nenhum atendimento registrado.");
            return;
        }

        for (Atendimento atendimento : atendimentos) {
            System.out.println(atendimento);
        }
    }
    // ---------------------------------------------------------------- 7

    private void exibirEstatisticas() {
        System.out.println("\n--- ESTATÍSTICAS DO PLANTÃO ---");
        Map<Prioridade, Integer> contagem = servico.contarPorPrioridade();

        for (Prioridade prioridade : Prioridade.values()) {
            int quantidade = contagem.get(prioridade);
            System.out.printf("%-8s | %-10s %d%n",
                    prioridade.name(), "*".repeat(quantidade), quantidade);
        }

        System.out.printf("Tempo médio de espera: %.1f min%n", servico.tempoMedioEsperaMin());
    }
}
