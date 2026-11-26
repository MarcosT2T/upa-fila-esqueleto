package br.ceub.poo.upa.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Paciente da UPA.
 *
 * <p>CONCEITOS: herança ({@code extends Pessoa}), interface ({@code implements Comparable}),
 * composição (TEM uma {@link Triagem}), encapsulamento (o status só muda por métodos
 * que respeitam o fluxo: AGUARDANDO_TRIAGEM → AGUARDANDO_ATENDIMENTO → ATENDIDO).</p>
 *
 * TODO [Etapa 3]: implementar getIdentificacao(), getSenhaFormatada(),
 *                 registrarTriagem(), marcarComoAtendido() e getPrioridade().
 * TODO [Etapa 4]: implementar compareTo() — é ele que define a ORDEM da fila!
 */
public class Paciente extends Pessoa implements Comparable<Paciente> {

    private final int senha;
    private final LocalDateTime horarioChegada;
    private StatusPaciente status;
    private Triagem triagem; // null até o paciente ser triado

    public Paciente(String nome, String cpf, LocalDate dataNascimento,
                    int senha, LocalDateTime horarioChegada) {
        super(nome, cpf, dataNascimento);
        this.senha = senha;
        this.horarioChegada = horarioChegada;
        this.status = StatusPaciente.AGUARDANDO_TRIAGEM;
    }

    public int getSenha() { return senha; }
    public LocalDateTime getHorarioChegada() { return horarioChegada; }
    public StatusPaciente getStatus() { return status; }
    public Triagem getTriagem() { return triagem; }

    /**
     * Senha com prefixo "P" e 3 dígitos. Ex.: senha 7 → "P007"; senha 125 → "P125".
     * Dica: String.format("P%03d", senha)
     */
    public String getSenhaFormatada() {
        return String.format("P%03d", senha);
    }

    public void registrarTriagem(Triagem triagem) {
        if (status != StatusPaciente.AGUARDANDO_TRIAGEM) {
            throw new IllegalStateException("Paciente já foi triado");
        }
        this.triagem = triagem;
        status = StatusPaciente.AGUARDANDO_ATENDIMENTO;
    }

    public void marcarComoAtendido() {
        if (status != StatusPaciente.AGUARDANDO_ATENDIMENTO) {
            throw new IllegalStateException("Paciente não está aguardando atendimento");
        }
        status = StatusPaciente.ATENDIDO;
    }

    public Prioridade getPrioridade() {
        return triagem == null ? null : triagem.getPrioridade();
    }

    @Override
    public String getIdentificacao() {
        return "Paciente " + getNome() + " (senha " + getSenhaFormatada() + ")";
    }

    /**
     * Define a ORDEM NATURAL dos pacientes na fila de atendimento.
     *
     * Regras, nesta ordem:
     *   1º) Prioridade mais grave primeiro  → compare prioridade.ordinal() (VERMELHO=0 vem antes).
     *   2º) Empate de prioridade → quem CHEGOU antes vem primeiro (horarioChegada).
     *   3º) Empate de horário → menor senha primeiro.
     *
     * Contrato: retorna NEGATIVO se "this" deve vir ANTES de "outro", POSITIVO se
     * deve vir DEPOIS e ZERO se são equivalentes.
     * Dica: Integer.compare(a, b)  e  horarioChegada.compareTo(outro.horarioChegada)
     * Pré-condição: os dois pacientes já foram triados (getPrioridade() != null).
     */
    @Override
    public int compareTo(Paciente outro) {
        int comparacao = Integer.compare(
                getPrioridade().ordinal(),
                outro.getPrioridade().ordinal()
        );
        if (comparacao != 0) {
            return comparacao;
        }

        comparacao = horarioChegada.compareTo(outro.horarioChegada);
        if (comparacao != 0) {
            return comparacao;
        }

        return Integer.compare(senha, outro.senha);
    }
}
