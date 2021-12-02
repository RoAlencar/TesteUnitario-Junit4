package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Usuario;
import org.junit.Assert;
import org.junit.Test;

public class AssertTest {

    @Test
    public void test(){
        Assert.assertTrue(true);
        Assert.assertFalse(false);

        Assert.assertEquals("Erro na comparação",1,1);
        Assert.assertEquals(0.51234, 0.512,0.001);
        Assert.assertEquals(Math.PI, 3.14,0.01);

        int i = 5;
        Integer i2 = 5;
        Assert.assertEquals(Integer.valueOf(i),i2);
        Assert.assertEquals(i,i2, i2.intValue());

        Assert.assertEquals("bola","bola");
        Assert.assertNotEquals("bola", "casa");
        Assert.assertTrue("bola".equalsIgnoreCase("Bola"));
        Assert.assertTrue("bola".startsWith("bo"));

        Usuario id = new Usuario("Usuario 1");
        Usuario id2 = new Usuario("Usuario 1");
        Usuario id3 = id2;
        Usuario id4 = null;

        //Necessario criar um EQUALS na entidade
        Assert.assertEquals(id,id2);

        Assert.assertSame(id3,id2);
        Assert.assertNotSame(id,id2);

        Assert.assertNull(id4);
        Assert.assertNotNull(id2);
    }
}
