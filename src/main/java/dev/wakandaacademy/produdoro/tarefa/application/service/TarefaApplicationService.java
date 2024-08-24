package dev.wakandaacademy.produdoro.tarefa.application.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import dev.wakandaacademy.produdoro.handler.APIException;
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
		Tarefa tarefaCriada = tarefaRepository.salva(new Tarefa(tarefaRequest));
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
	public void deletaTodasTarefas(String emailUsuario, UUID idUsuario) {
		log.info("[inicia] TarefaApplicationService - deletaTodasTarefas");
		Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(emailUsuario);
		log.info("[usuarioPorEmail] {}", usuarioPorEmail);
		Usuario usuario = usuarioRepository.buscaUsuarioPorId(idUsuario);
		usuario.pertenceAoUsuario(usuarioPorEmail);
		List<Tarefa> tarefasUsuario = tarefaRepository.buscaTarefaPorUsuario(usuario.getIdUsuario());
		if (tarefasUsuario.isEmpty()) {
			throw APIException.build(HttpStatus.BAD_REQUEST, "Usuário não possui tarefa(as) cadastrada(as)");
		}
		tarefaRepository.deletaTodasTarefas(tarefasUsuario);
		log.info("[finaliza] TarefaApplicationService - deletaTodasTarefas");

	}

	@Override
	public void defineTarefaComoAtiva(UUID idTarefa, String usuarioEmail) {
		log.info("[inicia] TarefaApplicationService - defineTarefaComoAtiva");
		Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuarioEmail);
		Tarefa tarefa = validarTarefa(idTarefa, usuarioPorEmail);
		Optional<Tarefa> tarefaJaAtiva = 
				tarefaRepository.buscaTarefaJaAtiva(usuarioPorEmail.getIdUsuario());
		tarefaJaAtiva.ifPresent(tarefaAtiva -> {
			tarefaAtiva.defineComoInativa();
			tarefaRepository.salva(tarefaAtiva);
		});
		tarefa.defineComoAtiva();
		tarefaRepository.salva(tarefa);	
		log.info("[finaliza] TarefaApplicationService - defineTarefaComoAtiva");

	}
	

	private Tarefa validarTarefa(UUID idTarefa, Usuario usuarioEmail) {
		Tarefa tarefa = tarefaRepository.buscaTarefaPorId(idTarefa)
				.orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Id Da Tarefa Inválido"));
		tarefa.pertenceAoUsuario(usuarioEmail);
		return tarefa;

	}


}
