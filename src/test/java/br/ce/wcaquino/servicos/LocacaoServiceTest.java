package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.CoreMatchers;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import java.util.Date;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class LocacaoServiceTest {

    private LocacaoService service;

    //Definição do contador
    private static int contador  = 0;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup(){
        System.out.println("Before");
        service = new LocacaoService();
        //Incremento
        contador++;
        //Impressão do contador
        System.out.println(contador);
    }

    @After
    public void tearDown(){
        System.out.println("After");
    }

    @BeforeClass
    public static void setup1(){
        System.out.println("BeforeClass");

    }

    @AfterClass
    public static void tearDown2(){
        System.out.println("AfterClass");
    }
    @Test
    public void testeLocacao() throws Exception {
        //Cenario
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme = new Filme("Filme", 2, 5.0);

        System.out.println("Teste!");

        //Ação
        Locacao locacao = service.alugarFilme(usuario, filme);

        //Verificação
        error.checkThat(locacao.getValor(), is(equalTo(5.0)));
        error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));

    }

    @Test(expected = FilmeSemEstoqueException.class)
    public void testeLocacao_FilmeSemEstoque() throws Exception {
        //Cenario
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme = new Filme("Filme", 0, 5.0);

        //Ação
        service.alugarFilme(usuario, filme);
    }

    @Test
    public void testeLocacao_UsuarioVazio() throws FilmeSemEstoqueException {
        //Cenario
        Filme filme = new Filme("Filme 2", 1, 4.0);
        //Ação
        try {
            service.alugarFilme(null, filme);
            Assert.fail();
        } catch (LocadoraException e) {
            Assert.assertThat(e.getMessage(), is("Usuario vazio"));
        }

    }

    @Test
    public void testeLocacao_FilmeVazio() throws FilmeSemEstoqueException, LocadoraException {
        //Cenario
        Usuario usuario = new Usuario("Usuario 1");

        exception.expect(LocadoraException.class);
        exception.expectMessage("Filme vazio");
        //Ação
        service.alugarFilme(usuario,null);

    }


}
