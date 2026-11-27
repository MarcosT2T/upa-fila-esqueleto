package br.ceub.poo.upa.modelo;

import br.ceub.poo.upa.excecao.SinaisVitaisInvalidosException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * TESTES ESCRITOS PELO GRUPO — este arquivo tem 1 exemplo.
 * TODO [Etapa 8]: um teste para CADA regra de validação (FC, PAS, PAD, PAD >= PAS, SpO2, dor)
 * e um teste mostrando que valores válidos NÃO lançam exceção (assertDoesNotThrow).
 */
class SinaisVitaisTest {

    @Test
    void temperaturaImpossivelLancaExcecao() {
        assertThrows(SinaisVitaisInvalidosException.class,
                () -> new SinaisVitais(50.0, 80, 120, 80, 98, 0, true));
    }
    @Test
    void rejeitaFrequenciaCardiacaAbaixoDoMinimo() {
        assertThrows(SinaisVitaisInvalidosException.class,
                () -> new SinaisVitais(36.5, 19, 120, 80, 98, 0, true));
    }

    @Test
    void rejeitaPressaoSistolicaAcimaDoMaximo() {
        assertThrows(SinaisVitaisInvalidosException.class,
                () -> new SinaisVitais(36.5, 80, 301, 80, 98, 0, true));
    }

    @Test
    void rejeitaPressaoDiastolicaAbaixoDoMinimo() {
        assertThrows(SinaisVitaisInvalidosException.class,
                () -> new SinaisVitais(36.5, 80, 120, 19, 98, 0, true));
    }

    @Test
    void rejeitaPressaoDiastolicaIgualASistolica() {
        // As duas pressões estão nas faixas permitidas, mas a diastólica precisa ser menor.
        assertThrows(SinaisVitaisInvalidosException.class,
                () -> new SinaisVitais(36.5, 80, 120, 120, 98, 0, true));
    }

    @Test
    void rejeitaSaturacaoAcimaDeCem() {
        assertThrows(SinaisVitaisInvalidosException.class,
                () -> new SinaisVitais(36.5, 80, 120, 80, 101, 0, true));
    }

    @Test
    void rejeitaDorAcimaDeDez() {
        assertThrows(SinaisVitaisInvalidosException.class,
                () -> new SinaisVitais(36.5, 80, 120, 80, 98, 11, true));
    }

    @Test
    void aceitaSinaisValidosComDorNoLimiteSuperior() {
        assertDoesNotThrow(() -> {
            new SinaisVitais(36.5, 80, 120, 80, 98, 10, true);
        });
    }
}
