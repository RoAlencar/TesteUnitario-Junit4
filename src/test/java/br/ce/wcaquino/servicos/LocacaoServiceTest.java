package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import java.util.Date;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class LocacaoServiceTest {

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testeLocacao() throws Exception {
        //Cenario
        LocacaoService service = new LocacaoService();
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme = new Filme("Filme", 2, 5.0);

        //Ação
        Locacao locacao = service.alugarFilme(usuario, filme);

        //Verificação
        error.checkThat(locacao.getValor(), is(equalTo(5.0)));
        error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));

    }

    @Test(expected= FilmeSemEstoqueException.class)
    public void testeLocacao_FilmeSemEstoque() throws Exception {
        //Cenario
        LocacaoService service = new LocacaoService();
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme = new Filme("Filme", 0, 5.0);

        //Ação
        service.alugarFilme(usuario, filme);
    }

    @Test
    public void testeLocacao_FilmeSemEstoque2(){
        //Cenario
        LocacaoService service = new LocacaoService();
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme = new Filme("Filme", 0, 5.0);

        //Ação
        try {
            service.alugarFilme(usuario, filme);
            Assert.fail("Deveria ter lançado uma exceção");
        } catch (Exception e) {
            assertThat(e.getMessage(),is("Filme sem estoque"));
        }
    }

    @Test
    public void testeLocacao_FilmeSemEstoque3() throws Exception {
        //Cenario
        LocacaoService service = new LocacaoService();
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme = new Filme("Filme", 0, 5.0);

        exception.expect(Exception.class);
        exception.expectMessage("Filme sem estoque");

        //Ação
        service.alugarFilme(usuario, filme);


    }

}
