package br.ce.wcaquino.servicos;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZeroException;

public class Calculadora {

    public int somar(int a, int b) {
        System.out.println("Estou executando o método somar");
        return a + b;
    }

    public int subtrai(int a, int b) {
        return a - b;
    }

    public int multiplica(int a, int b) {
        return a * b;
    }

    public int divide(int a, int b) throws NaoPodeDividirPorZeroException {
        if(b == 0){
            throw new NaoPodeDividirPorZeroException();
        }
        return a / b;
    }

    public void imprime(){
        System.out.println("Passei aqui");
    }
}
