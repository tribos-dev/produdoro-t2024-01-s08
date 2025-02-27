package dev.wakandaacademy.produdoro.usuario.application.service;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.StatusUsuario;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioApplicationServiceTest {

    @InjectMocks
    UsuarioApplicationService usuarioApplicationService;

    @Mock
    UsuarioRepository usuarioRepository;

    @Test
    void deveMudarStatusParaPausaCurta_QuandoStatusEstiverDiferenteDePausaCurta() {
        // Dado
        Usuario usuario = DataHelper.createUsuario1();
        // Quando
        when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
        usuarioApplicationService.mudaStatusParaPausaCurta(usuario.getEmail(), usuario.getIdUsuario());
        // Então
        assertEquals(StatusUsuario.PAUSA_CURTA, usuario.getStatus());
        verify(usuarioRepository, times(1)).salva(usuario);
    }

    @Test
    void naoDeveMudarStatusParaPausaCurta_QuandoPassarIdUsuarioInvalido() {
        Usuario usuario = DataHelper.createUsuario1();
        UUID idUsuario = UUID.fromString("ce138189-3651-4c12-950e-24fe7b7a4417");
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        APIException e = assertThrows(APIException.class,
                () -> usuarioApplicationService.mudaStatusParaPausaCurta(usuario.getEmail(), idUsuario));
        assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusException());
    }

    @Test
    void deveNaoMudarStatusParaPausaCurta_QuandoStatusEstiverEmPausaCurta() {
        Usuario usuario = DataHelper.createUsuario1();
        when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
        usuarioApplicationService.mudaStatusParaPausaCurta(usuario.getEmail(), usuario.getIdUsuario());
        APIException exception = assertThrows(APIException.class, usuario::verificaSeJaEstaEmPausaCurta);
        assertEquals("Usuário já está em Pausa Curta.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusException());
    }

    @Test
    void deveMudarParaPausaLonga() {
        // dado
        Usuario usuario = DataHelper.createUsuario();
        // quando
        when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
        usuarioApplicationService.mudaStatusParaPausaLonga(usuario.getEmail(), usuario.getIdUsuario());
        // entao
        assertEquals(StatusUsuario.PAUSA_LONGA, usuario.getStatus());
        verify(usuarioRepository, times(1)).salva(usuario);
    }

    @Test
    void naoDeveMudarParaPausaLonga() {
        // dado
        Usuario usuario = DataHelper.createUsuario();
        UUID idUsuarioInvalido = UUID.fromString("2198e376-264c-4806-8a0a-73ce75f9960f");
        // quando
        when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(usuario);
        APIException ex = assertThrows(APIException.class,
                () -> usuarioApplicationService.mudaStatusParaPausaLonga(usuario.getEmail(), idUsuarioInvalido));
        // entao
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusException());
    }

    @Test
    void deveMudarParaFoco_QuandoStatusEstiverDiferenteDeFoco() {
        //dado
        Usuario usuario = DataHelper.createUsuario();
        // quando
        when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
        usuarioApplicationService.mudaStatusParaFoco(usuario.getEmail(), usuario.getIdUsuario());
        // entao
        assertEquals(StatusUsuario.FOCO, usuario.getStatus());
        verify(usuarioRepository, times(1)).salva(usuario);
    }

    @Test
    void naoDeveMudarStatusParaFoco_QuandoPassarIdUsuarioInvalido() {
        Usuario usuario = DataHelper.createUsuario();
        UUID idUsuario = UUID.fromString("ce138189-3651-4c12-950e-24fe7b7a4417");
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        APIException e = assertThrows(APIException.class,
                () -> usuarioApplicationService.mudaStatusParaFoco(usuario.getEmail(), idUsuario));
        assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusException());
    }
}