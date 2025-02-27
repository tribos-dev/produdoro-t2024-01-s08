package dev.wakandaacademy.produdoro.tarefa.application.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.wakandaacademy.produdoro.config.security.service.TokenService;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.service.TarefaService;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
@RequiredArgsConstructor
public class TarefaRestController implements TarefaAPI {
    private final TarefaService tarefaService;
    private final TokenService tokenService;

    public TarefaIdResponse postNovaTarefa(TarefaRequest tarefaRequest) {
        log.info("[inicia]  TarefaRestController - postNovaTarefa  ");
        TarefaIdResponse tarefaCriada = tarefaService.criaNovaTarefa(tarefaRequest);
        log.info("[finaliza]  TarefaRestController - postNovaTarefa");
        return tarefaCriada;
    }

    @Override
    public TarefaDetalhadoResponse detalhaTarefa(String token, UUID idTarefa) {
        log.info("[inicia] TarefaRestController - detalhaTarefa");
        String usuario = getUsuarioByToken(token);
        Tarefa tarefa = tarefaService.detalhaTarefa(usuario, idTarefa);
        log.info("[finaliza] TarefaRestController - detalhaTarefa");
        return new TarefaDetalhadoResponse(tarefa);
    }

    @Override
    public void editaTarefa(String token, TarefaAlteracaoRequest tarefaAlteracaoRequest, UUID idTarefa) {
        log.info("[inicia] TarefaRestController - editaTarefa");
        String email = getUsuarioByToken(token);
        tarefaService.editaTarefa(email, idTarefa, tarefaAlteracaoRequest);
        log.info("[finish] TarefaRestController - editaTarefa");
    }
	
	@Override
	public void deletaTodasSuasTarefas(String token, UUID idUsuario) {
		log.info("[inicia] TarefaRestController - deletaTodasSuasTarefas");
		String emailUsuario = getUsuarioByToken(token);
		tarefaService.deletaTodasTarefas(emailUsuario, idUsuario);
		log.info("[finaliza] TarefaRestController - deletaTodasSuasTarefas");
	}

	@Override
	public void deletaTarefasConcluidas(String token, UUID idUsuario) {
		log.info("[inicia] TarefaRestController - deletaTarefasConcluidas");
		String email = getUsuarioByToken(token);
		tarefaService.deletaTarefasConcluidas(email, idUsuario);
		log.info("[finaliza] TarefaRestController - deletaTarefasConcluidas");
	}

    @Override
    public void concluiTarefa(String token, UUID idTarefa) {
        log.info("[inicia] TarefaRestController - concluiTarefa");
        String email = getUsuarioByToken(token);
        tarefaService.concluiTarefa(email, idTarefa);
        log.info("[Finish] TarefaRestController - concluiTarefa");
    }

    @Override
    public void imcrementaPomodoro(String token, UUID idTarefa) {
        log.info("[start] - TarefaRestController - imcrementaPomodoro");
        String usuario = getUsuarioByToken(token);
        tarefaService.incrementaPomodoro(usuario, idTarefa);
        log.info("[finish] - TarefaRestController - imcrementaPomodoro");

    }

    private String getUsuarioByToken(String token) {
        log.debug("[token] {}", token);
        String usuario = tokenService.getUsuarioByBearerToken(token)
                .orElseThrow(() -> APIException.build(HttpStatus.UNAUTHORIZED, token));
        log.info("[usuario] {}", usuario);
        return usuario;
    }

	@Override
	public void modificaOrdemTarefa(String token, NovaPosicaoRequest novaPosicaoRequest, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - modificaOrdemTarefa");
		String emailUsuario = getUsuarioByToken(token);
		tarefaService.modificaOrdemTarefa(emailUsuario, novaPosicaoRequest, idTarefa);
		log.info("[finaliza] TarefaRestController - modificaOrdemTarefa");
	}

	@Override
	public void defineTarefaComoAtiva(String token, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - defineTarefaComoAtiva");
		String usuarioEmail = getUsuarioByToken(token);
		tarefaService.defineTarefaComoAtiva(idTarefa, usuarioEmail);
		log.info("[finaliza] TarefaRestController - defineTarefaComoAtiva");
		
	}

	@Override
	public List<TarefaDetalhadaListResponse> listaTodasTarefasDoUsuario(String token, UUID idUsuario) {
		log.info("[start] - TarefaRestController - listaTodasTarefasDoUsuario");
		String email = getUsuarioByToken(token);
		List<TarefaDetalhadaListResponse> tarefas = tarefaService.listaTodasTarefasDoUsuario(email, idUsuario);
		log.info("[finish] - TarefaRestController - listaTodasTarefasDoUsuario");
		return tarefas;
	}
}
