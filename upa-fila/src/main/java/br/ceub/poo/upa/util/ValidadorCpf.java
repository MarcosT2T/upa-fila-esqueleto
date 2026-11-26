package br.ceub.poo.upa.util;

/**
 * Validação de CPF pelos DÍGITOS VERIFICADORES (algoritmo oficial da Receita Federal).
 * <p>Classe utilitária: construtor privado + métodos {@code static}.</p>
 */
public final class ValidadorCpf {

    private ValidadorCpf() {
    }

    /** Remove tudo o que não for dígito. "529.982.247-25" → "52998224725". JÁ IMPLEMENTADO. */
    public static String normalizar(String cpf) {
        return cpf == null ? "" : cpf.replaceAll("\\D", "");
    }

    /** "52998224725" → "529.982.247-25". JÁ IMPLEMENTADO. Pré-condição: 11 dígitos. */
    public static String formatar(String cpfNormalizado) {
        return cpfNormalizado.substring(0, 3) + "." + cpfNormalizado.substring(3, 6) + "."
                + cpfNormalizado.substring(6, 9) + "-" + cpfNormalizado.substring(9);
    }

    /** Verifica o tamanho, rejeita sequências repetidas e confere os dois dígitos verificadores. */
    public static boolean isValido(String cpf) {
        String digitos = normalizar(cpf);

        if (digitos.length() != 11 || digitos.chars().distinct().count() == 1) {
            return false;
        }

        for (int posicaoVerificador = 9; posicaoVerificador <= 10; posicaoVerificador++) {
            int soma = 0;

            for (int i = 0; i < posicaoVerificador; i++) {
                int digito = digitos.charAt(i) - '0';
                int peso = posicaoVerificador + 1 - i;
                soma += digito * peso;
            }

            int resto = soma % 11;
            int esperado = resto < 2 ? 0 : 11 - resto;
            int informado = digitos.charAt(posicaoVerificador) - '0';

            if (esperado != informado) {
                return false;
            }
        }

        return true;
    }
}
