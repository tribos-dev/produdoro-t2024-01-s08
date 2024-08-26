package dev.wakandaacademy.produdoro.tarefa.application.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.NovaPosicaoRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class TarefaApplicationService implements TarefaService {
    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest) {
        log.info("[inicia] TarefaApplicationService - criaNovaTarefa");
        int novaPosicao = tarefaRepository.novaPosicao(tarefaRequest.getIdUsuario());
        Tarefa tarefaCriada = tarefaRepository.salva(new Tarefa(tarefaRequest, novaPosicao));
        log.info("[finaliza] TarefaApplicationService - criaNovaTarefa");
        return TarefaIdResponse.builder().idTarefa(tarefaCriada.getIdTarefa()).build();
    }

    @Override
    public Tarefa detalhaTarefa(String usuario, UUID idTarefa) {
        log.info("[inicia] TarefaApplicationService - detalhaTarefa");
        Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
        log.info("[usuarioPorEmail] {}", usuarioPorEmail);
        Tarefa tarefa = tarefaRepository.buscaTarefaPorId(idTarefa)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Tarefa não encontrada!"));
        tarefa.pertenceAoUsuario(usuarioPorEmail);
        log.info("[finaliza] TarefaApplicationService - detalhaTarefa");
        return tarefa;
    }
    
	@Override
	public void modificaOrdemTarefa(String emailUsuario, NovaPosicaoRequest novaPosicaoRequest, UUID idTarefa) {
        log.info("[inicia] TarefaApplicationService - modificaOrdemTarefa");
        Tarefa tarefa = detalhaTarefa(emailUsuario, idTarefa);
        tarefaRepository.modificaOrdemTarefa(tarefa, novaPosicaoRequest);
        tarefa.editaNovaPosicao(novaPosicaoRequest.getNovaPosicao());
        tarefaRepository.salva(tarefa);
        log.info("[finaliza] TarefaApplicationService - modificaOrdemTarefa");
	}

    @Override
    public void concluiTarefa(String email, UUID idTarefa) {
        log.info("[inicia] TarefaApplicationService - concluiTarefa");
        Tarefa tarefa = detalhaTarefa(email, idTarefa);
        tarefa.concluiTarefa();
        tarefaRepository.salva(tarefa);
        log.info("[Finish] TarefaApplicationService - concluiTarefa");
    }

    @Override
    public void incrementaPomodoro(String usuario, UUID idTarefa) {
        log.info("[start] - TarefaApplicationService - incrementaPomodoro");
        Tarefa tarefa = detalhaTarefa(usuario, idTarefa);
        Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
        tarefa.incrementaPomodoro(usuarioPorEmail);
        tarefaRepository.salva(tarefa);
        tarefaRepository.processaStatusEContadorPomodoro(usuarioPorEmail);
        log.info("[finish] - TarefaApplicationService - incrementaPomodoro");
    }
}
