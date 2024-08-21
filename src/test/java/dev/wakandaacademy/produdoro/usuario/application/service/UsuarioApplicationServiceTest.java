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
    void deveMudarParaPausaLonga(){
        //dado
        Usuario usuario = DataHelper.createUsuario();
        //quando
        when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
        usuarioApplicationService.mudaStatusParaPausaLonga(usuario.getEmail(), usuario.getIdUsuario());
        //entao
        assertEquals(StatusUsuario.PAUSA_LONGA, usuario.getStatus());
        verify(usuarioRepository, times(1)).salva(usuario);
    }

    @Test
    void naoDeveMudarParaPausaLonga(){
        //dado
        Usuario usuario = DataHelper.createUsuario();
        UUID idUsuarioInvalido = UUID.fromString("2198e376-264c-4806-8a0a-73ce75f9960f");
        //quando
        when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(usuario);
        APIException ex = assertThrows(APIException.class, ()-> usuarioApplicationService.mudaStatusParaPausaLonga(usuario.getEmail(), idUsuarioInvalido));
        //entao
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusException());
    }

}