package br.ce.wcaquino.servicos;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZeroException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CalculadoraTest {

    private Calculadora calc;

    @Before
    public void setup(){
        calc = new Calculadora();
    }
    @Test
    public void deveSomarDoisValores(){
        //Cenario
        int a = 5;
        int b = 3;
        //Ação
        int resultado = calc.somar(a,b);
        //Verificação
        Assert.assertEquals(8,resultado);
    }

    @Test
    public void deveSubtrairDoisValores(){
        //Cenario
        int a = 5;
        int b = 3;
        //Ação
        int resultado = calc.subtrai(a,b);
        //Verificação
        Assert.assertEquals(2,resultado);
    }

    @Test
    public void deveMultiplicarDoisValores(){
        //Cenario
        int a = 5;
        int b = 2;
        //Ação
        int resultado = calc.multiplica(a,b);
        //Verificação
        Assert.assertEquals(10,resultado);
    }

    @Test
    public void deveDividirDoisValores() throws NaoPodeDividirPorZeroException {
        //Cenario
        int a = 12;
        int b = 3;
        //Ação
        int resultado = calc.divide(a,b);
        //Verificação
        Assert.assertEquals(4,resultado);
    }

    @Test(expected = NaoPodeDividirPorZeroException.class)
    public void deveLancarExcecaoAoDividirPorZero() throws NaoPodeDividirPorZeroException {
        int a = 10;
        int b = 0;

        calc.divide(a,b);

    }
}
