/**
 * Lancada quando dados fornecidos pelo usuario sao invalidos,
 * como valores numericos incorretos, campos obrigatorios vazios
 * ou operacoes de ponto (entrada/saida) fora de ordem.
 */
public class DadosInvalidosException extends SistemaException {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
