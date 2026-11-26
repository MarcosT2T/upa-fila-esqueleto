package br.ceub.poo.upa.triagem;

import br.ceub.poo.upa.modelo.Prioridade;
import br.ceub.poo.upa.modelo.SinaisVitais;

/**
 * Implementação DIDÁTICA e simplificada do Protocolo de Manchester.
 * (Não usar em ambiente clínico real!)
 *
 * TODO [Etapa 4]: implementar classificar() com as regras abaixo.
 * As regras são avaliadas DA MAIS GRAVE PARA A MENOS GRAVE; a PRIMEIRA que for
 * verdadeira define a cor (basta UMA condição da linha para enquadrar).
 *
 *  VERMELHO: inconsciente  OU  SpO2 < 90  OU  FC < 40  OU  FC > 150  OU  PAS < 80
 *  LARANJA : SpO2 entre 90 e 93  OU  temperatura >= 40.0  OU  dor >= 8
 *            OU  FC > 130  OU  PAS >= 180
 *  AMARELO : temperatura >= 38.5  OU  dor >= 5  OU  FC > 110  OU  PAS >= 160
 *  VERDE   : temperatura >= 37.8  OU  dor >= 1
 *  AZUL    : nenhuma das anteriores
 *
 *  (PAS = pressão arterial sistólica; FC = frequência cardíaca)
 *
 * Operadores exigidos aqui: relacionais (<, >, >=) e lógicos (||, !).
 */
public class ClassificadorManchesterSimplificado implements ClassificadorRisco {

    @Override
    public Prioridade classificar(SinaisVitais sinais) {
        int saturacao = sinais.getSaturacaoO2();
        int frequencia = sinais.getFrequenciaCardiaca();
        int pressao = sinais.getPressaoSistolica();
        double temperatura = sinais.getTemperatura();
        int dor = sinais.getDor();

        if (!sinais.isConsciente() || saturacao < 90
                || frequencia < 40 || frequencia > 150 || pressao < 80) {
            return Prioridade.VERMELHO;
        }

        if (saturacao <= 93 || temperatura >= 40.0 || dor >= 8
                || frequencia > 130 || pressao >= 180) {
            return Prioridade.LARANJA;
        }

        if (temperatura >= 38.5 || dor >= 5
                || frequencia > 110 || pressao >= 160) {
            return Prioridade.AMARELO;
        }

        if (temperatura >= 37.8 || dor >= 1) {
            return Prioridade.VERDE;
        }

        return Prioridade.AZUL;
    }

    @Override
    public String getNomeProtocolo() {
        return "Manchester (simplificado, didático)";
    }
}
