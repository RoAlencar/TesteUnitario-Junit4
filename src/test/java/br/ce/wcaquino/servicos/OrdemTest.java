package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Usar caso necessario que um teste precise do resultado de outro.
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING) //Executa em ordem alfabetica
public class OrdemTest {

    public static int contador = 0;

    @Test
    public void inicio(){
        contador = 1;
    }

    @Test
    public void verifica(){
        Assert.assertEquals(1,contador);
    }
}
