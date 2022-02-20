package br.ce.wcaquino.servicos;

import br.ce.wcaquino.builders.LocacaoBuilder;
import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.ce.wcaquino.builders.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("jdk.internal.reflect.*")
@PrepareForTest({LocacaoService.class})
public class LocacaoServiceTest {

    @InjectMocks
    private LocacaoService service;

    @Mock
    private SPCService spc;
    @Mock
    private LocacaoDAO dao;
    @Mock
    private EmailService email;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        service = PowerMockito.spy(service);
    }

    @Test
    public void deveAlugarFilme() throws Exception {

        //Cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = asList(umFilme().comValor(5.0).agora());

        // PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(19,2,2022));

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,19);
        calendar.set(Calendar.MONTH,Calendar.FEBRUARY);
        calendar.set(Calendar.YEAR,2022);
        PowerMockito.mockStatic(Calendar.class);
        PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);

        //Ação
        Locacao locacao = service.alugarFilme(usuario, filmes);

        //Verificação
        error.checkThat(locacao.getValor(), is(equalTo(5.0)));
        //error.checkThat(locacao.getDataLocacao(), ehHoje());
        // error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(19,2,2022)), is( true));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(21,2,2022)), is( true));
    }

    @Test(expected = FilmeSemEstoqueException.class)
    public void naoDeveAlguarFilmeSemEstoque() throws Exception {
        //Cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = asList(umFilmeSemEstoque().agora());

        //Ação
        service.alugarFilme(usuario, filmes);
    }

    @Test
    public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
        //Cenario
        List<Filme> filmes = asList(umFilme().agora())  ;
        //Ação
        try {
            service.alugarFilme(null, filmes);
            fail();
        } catch (LocadoraException e) {
            assertThat(e.getMessage(), is("Usuario vazio"));
        }

    }

    @Test
    public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
        //Cenario
        Usuario usuario = umUsuario().agora();

        exception.expect(LocadoraException.class);
        exception.expectMessage("Filme vazio");
        //Ação
        service.alugarFilme(usuario,null);

    }

    @Test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
        //Cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = asList(umFilme().agora());

        //PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(29,4,2017));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,29);
        calendar.set(Calendar.MONTH,Calendar.APRIL);
        calendar.set(Calendar.YEAR,2017);
        PowerMockito.mockStatic(Calendar.class);
        PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
        //Ação
        Locacao retorno = service.alugarFilme(usuario,filmes);

        //Verificação
        assertThat(retorno.getDataRetorno(), caiNumaSegunda());
        // PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();
        PowerMockito.verifyStatic(Mockito.times(2));
        Calendar.getInstance();
    }

    @Test
    public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
        //Cenario
        Usuario usuario = umUsuario().agora();
        Usuario usuario2 = umUsuario().comNome("Jorge").agora();
        List<Filme> filmes = asList(umFilme().agora());

        when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);

        //acao
        try {
            service.alugarFilme(usuario, filmes);
            //verificacao
            fail();
        } catch (LocadoraException e) {
            e.printStackTrace();
            assertThat(e.getMessage(), is("Usuario Negativado"));
        }

        verify(spc).possuiNegativacao(usuario);
    }

    @Test
    public void deveEnviarEmailParaLocacoesAtrasadas(){
        //Cenario
        Usuario usuario = umUsuario().agora();
        Usuario usuario2 = umUsuario().comNome("Jorge").agora();
        Usuario usuario3 = umUsuario().comNome("Brian").agora();
        List<Locacao> locacoes = asList(
                umLocacao()
                        .atrasada()
                        .comUsuario(usuario)
                        .agora(),
                umLocacao()
                        .comUsuario(usuario2)
                        .agora(),
                umLocacao()
                        .atrasada()
                        .comUsuario(usuario3)
                        .agora(),
                umLocacao()
                        .atrasada()
                        .comUsuario(usuario3)
                        .agora());

        when(dao.obterLocacoesPendentes()).thenReturn(locacoes);

        //Ação
        service.notificarAtrasos();

        //verificacao
        verify(email, times(3)).notificarAtraso(Mockito.any(Usuario.class));
        verify(email).notificarAtraso(usuario);
        verify(email, atLeastOnce()).notificarAtraso(usuario3);
        verify(email, never()).notificarAtraso(usuario2);
        verifyNoMoreInteractions(email);
    }

    @Test
    public void deveTratarErrornoSPC() throws Exception {
        //Cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes= Arrays.asList(umFilme().agora());

        when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha Catastrófica"));
        //Verificação
        exception.expect(LocadoraException.class);
        exception.expectMessage("Problemas no SPC, tente novamente!");
        //Ação
        service.alugarFilme(usuario, filmes);
    }

    @Test
    public void deveProrrogarUmaLocacao(){
        //Cenario
        Locacao locacao = LocacaoBuilder.umLocacao().agora();

        //Ação
        service.prorrogarLocacao(locacao,3);

        //Verificação
        ArgumentCaptor<Locacao> argCpt = ArgumentCaptor.forClass(Locacao.class);
        Mockito.verify(dao).salvar(argCpt.capture());
        Locacao locacaoRetornada = argCpt.getValue();

        error.checkThat(locacaoRetornada.getValor(),is(12.0));
        error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
        error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeComDiferencaDias(3));
    }

    @Test
    public void deveAlugarFilme_SemCalcularValor() throws Exception {
        //Cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        PowerMockito.doReturn(1.0).when(service,"calcularValorLocacao",filmes);
        //Ação
        Locacao locacao = service.alugarFilme(usuario, filmes);
        //Verificação
        Assert.assertThat(locacao.getValor(), is(1.0));
        PowerMockito.verifyPrivate(service).invoke("calcularValorLocacao", filmes);
    }

    @Test
    public void deveCalcularValorLocacao() throws Exception {
        //Cenario
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        //Ação
       Double valor = (Double) Whitebox.invokeMethod(service,"calcularValorLocacao", filmes);

        //Verificação
        Assert.assertThat(valor,is(4.0));
    }
}
