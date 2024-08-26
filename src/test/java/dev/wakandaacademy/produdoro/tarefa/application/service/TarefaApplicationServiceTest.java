package dev.wakandaacademy.produdoro.tarefa.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusAtivacaoTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;

@ExtendWith(MockitoExtension.class)
class TarefaApplicationServiceTest {

	// @Autowired
	@InjectMocks
	TarefaApplicationService tarefaApplicationService;

	// @MockBean
	@Mock
	TarefaRepository tarefaRepository;
	@Mock
	UsuarioRepository usuarioRepository;

	@Test
	@DisplayName("Deve retornar Id da tarefa nova criada.")
	void deveRetornarIdTarefaNovaCriada() {
		TarefaRequest request = getTarefaRequest();
		when(tarefaRepository.salva(any())).thenReturn(new Tarefa(request));

		TarefaIdResponse response = tarefaApplicationService.criaNovaTarefa(request);

		assertNotNull(response);
		assertEquals(TarefaIdResponse.class, response.getClass());
		assertEquals(UUID.class, response.getIdTarefa().getClass());
	}

	public TarefaRequest getTarefaRequest() {
		TarefaRequest request = new TarefaRequest("tarefa 1", UUID.randomUUID(), null, null, 0);
		return request;
	}

	@Test
	@DisplayName("Deve deletar todas as tarefas do usuario.")
	void deveDeletarTodasSuasTarefas() {
		Usuario usuario = DataHelper.createUsuario();
		List<Tarefa> tarefas = DataHelper.createListTarefa();
		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
		when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
		when(tarefaRepository.buscaTarefaPorUsuario(any())).thenReturn(tarefas);
		tarefaApplicationService.deletaTodasTarefas(usuario.getEmail(), usuario.getIdUsuario());
		verify(tarefaRepository, times(1)).deletaTodasTarefas(tarefas);
	}

//	@Test
//	@DisplayName("Não deve deletar tarefas do usuario.")
//	void naoDeveDeletarTarefas() {
//		Usuario usuario = DataHelper.createUsuario();
//		List<Tarefa> tarefas = DataHelper.createListTarefa();
//		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
//		when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
//		when(tarefaRepository.buscaTarefaPorUsuario(any())).thenReturn(tarefas);
//		// Simulando uma condição onde as tarefas não podem ser deletadas
//		doThrow(new RuntimeException("Erro ao deletar tarefas")).when(tarefaRepository).deletaTodasTarefas(tarefas);
//		assertThrows(RuntimeException.class, () -> {
//			tarefaApplicationService.deletaTodasTarefas(usuario.getEmail(), usuario.getIdUsuario());
//		});
//		verify(tarefaRepository, times(1)).deletaTodasTarefas(tarefas);
//	}

	@Test
	@DisplayName("Deve definir a tarefa do usuário como ativa.")
	void deveDefinirTarefaComoAtiva() {
		Usuario usuario = DataHelper.createUsuario();
		Tarefa tarefa = DataHelper.createTarefa();
		Tarefa tarefaAtiva = getTarefaAtiva(usuario);
		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
		when(tarefaRepository.buscaTarefaPorId(tarefa.getIdTarefa())).thenReturn(Optional.of(tarefa));
		when(tarefaRepository.buscaTarefaJaAtiva(usuario.getIdUsuario())).thenReturn(Optional.ofNullable(tarefaAtiva));
		tarefaApplicationService.defineTarefaComoAtiva(tarefa.getIdTarefa(), String.valueOf(usuario.getEmail()));
		verify(tarefaRepository, times(1)).salva(tarefa);
		verify(tarefaRepository, times(1)).buscaTarefaJaAtiva(usuario.getIdUsuario());
		verify(tarefaRepository, times(1)).salva(tarefa);
	}

	private static Tarefa getTarefaAtiva(Usuario usuario) {
		return Tarefa.builder().contagemPomodoro(1).idTarefa(UUID.fromString("4c70c27a-446c-4506-b666-1067085d8d85"))
				.idUsuario(usuario.getIdUsuario()).descricao("Descricao da tarefa")
				.statusAtivacao(StatusAtivacaoTarefa.ATIVA).build();
	}

	@Test
	@DisplayName("Não deve definir a tarefa do usuário como ativa.")
	void naoDeveDefinirTarefaComoAtiva() {
		Usuario usuario = DataHelper.createUsuario();
		UUID idTarefaInvalido = UUID.randomUUID();
		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
		when(tarefaRepository.buscaTarefaPorId(idTarefaInvalido))
				.thenThrow(APIException.build(HttpStatus.NOT_FOUND, "Id da Tarefa inválido!"));
		APIException e = assertThrows(APIException.class, () -> {
			tarefaApplicationService.defineTarefaComoAtiva(idTarefaInvalido, String.valueOf(usuario.getEmail()));
		});
		assertEquals(HttpStatus.NOT_FOUND, e.getStatusException());
		verify(tarefaRepository, never()).buscaTarefaJaAtiva(usuario.getIdUsuario());
		verify(tarefaRepository, never()).salva(any(Tarefa.class));
	}

}
