package dev.wakandaacademy.produdoro.tarefa.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.NovaPosicaoRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.StatusUsuario;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;

@ExtendWith(MockitoExtension.class)
class TarefaApplicationServiceTest {

    // @Autowired
    @InjectMocks
    TarefaApplicationService tarefaApplicationService;

    // @MockBean
    @Mock
    TarefaRepository tarefaRepository;

    // @MockBean
    @Mock
    UsuarioRepository usuarioRepository;

    @Test
    void deveRetornarIdTarefaNovaCriada() {
        TarefaRequest request = getTarefaRequest();
        when(tarefaRepository.salva(any())).thenReturn(new Tarefa(request, 0));

        TarefaIdResponse response = tarefaApplicationService.criaNovaTarefa(request);

        assertNotNull(response);
        assertEquals(TarefaIdResponse.class, response.getClass());
        assertEquals(UUID.class, response.getIdTarefa().getClass());
    }

    @Test
    void deveRetornarTarefaConcluida() {
        Usuario usuario = DataHelper.createUsuario();
        UUID idTarefa = UUID.randomUUID();
        Tarefa tarefa = Tarefa.builder()
                .idTarefa(UUID.randomUUID())
                .status(StatusTarefa.A_FAZER)
                .idUsuario(usuario.getIdUsuario())
                .build();
        when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(tarefa.getIdTarefa())).thenReturn(Optional.of(tarefa));
        when(tarefaRepository.salva(tarefa)).thenReturn(tarefa);
        tarefaApplicationService.concluiTarefa(usuario.getEmail(), tarefa.getIdTarefa());
        assertEquals(StatusTarefa.CONCLUIDA, tarefa.getStatus());
    }

    @Test
    void deveIncrementarPomodoroUmaTarefa() {
        Usuario usuario = DataHelper.createUsuario();
        Tarefa tarefa = DataHelper.createTarefa();

        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));

        tarefaApplicationService.incrementaPomodoro(usuario.getEmail(), tarefa.getIdTarefa());

        verify(tarefaRepository, times(1)).salva(tarefa);
    }

    public TarefaRequest getTarefaRequest() {
        TarefaRequest request = new TarefaRequest("tarefa 1", UUID.randomUUID(), null, null, 0);
        return request;
    }
    
    @Test
    void deveModificarOrdemTarefa() {
        Usuario usuario = DataHelper.createUsuario();
        Tarefa tarefa = DataHelper.createTarefa();
        NovaPosicaoRequest novaPosicaoRequest = getNovaPosicaoRequest();
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));

        tarefaApplicationService.modificaOrdemTarefa(usuario.getEmail(), novaPosicaoRequest, tarefa.getIdTarefa());
        
        verify(tarefaRepository, times(1)).salva(tarefa);
    }
    
    public NovaPosicaoRequest getNovaPosicaoRequest() {
    	NovaPosicaoRequest novaPosicaoRequest = new NovaPosicaoRequest(1);
    	return novaPosicaoRequest;
    }
    
    
    @Test
    void erroAoModificarOrdemTarefaComIdTarefaInvalido() {
        Usuario usuario = DataHelper.createUsuario();
        UUID idInvalido = UUID.randomUUID();
        NovaPosicaoRequest novaPosicaoRequest = getNovaPosicaoRequest();
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.empty());

        APIException ex = assertThrows(APIException.class, 
	            () -> tarefaApplicationService.modificaOrdemTarefa(usuario.getEmail(), novaPosicaoRequest, idInvalido));
        
        assertEquals("Tarefa não encontrada!", ex.getMessage());
	    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusException());
    }
    
    @Test
    void erroAoModificarOrdemTarefaQuandoTarefaNaoPertenceAoUsuario() {
        Usuario usuario = getCreateUsuario();
        Tarefa tarefa = DataHelper.createTarefa();
        NovaPosicaoRequest novaPosicaoRequest = getNovaPosicaoRequest();
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));

        APIException ex = assertThrows(APIException.class, 
	            () -> tarefaApplicationService.modificaOrdemTarefa(usuario.getEmail(), novaPosicaoRequest, tarefa.getIdTarefa()));
        
        assertEquals("Usuário não é dono da Tarefa solicitada!", ex.getMessage());
	    assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusException());
    }
    
    public Usuario getCreateUsuario() {
        return Usuario.builder().email("teste@email.com").status(StatusUsuario.PAUSA_LONGA).idUsuario(UUID.randomUUID()).build();
    }
}
