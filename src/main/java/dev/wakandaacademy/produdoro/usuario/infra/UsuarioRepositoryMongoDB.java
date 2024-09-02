package dev.wakandaacademy.produdoro.usuario.infra;

import dev.wakandaacademy.produdoro.handler.APIException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Log4j2
public class UsuarioRepositoryMongoDB implements UsuarioRepository {
	private final UsuarioMongoSpringRepository usuarioMongoRepository;

	@Override
	public Usuario salva(Usuario usuario) {
		log.info("[inicia] UsuarioRepositoryMongoDB - salva");
		Usuario novoUsuario = usuarioMongoRepository.save(usuario);
		log.info("[inicia] UsuarioRepositoryMongoDB - salva");
		return novoUsuario;
	}

	@Override
	public Usuario buscaUsuarioPorId(UUID idUsuario) {
		log.info("[inicia] UsuarioRepositoryMongoDB - buscaUsuarioPorId");
		Usuario usuario = usuarioMongoRepository.findByIdUsuario(idUsuario)
				.orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Usuario não encontrado!"));
		log.info("[finaliza] UsuarioRepositoryMongoDB - buscaUsuarioPorId");
		return usuario;
	}

	@Override
	public Usuario buscaUsuarioPorEmail(String emailUsuario) {
		log.info("[inicia] UsuarioRepositoryMongoDB - buscaUsuarioPorEmail");
		Usuario usuario = usuarioMongoRepository.findByEmail(emailUsuario)
				.orElseThrow(() -> APIException.build(HttpStatus.BAD_REQUEST, "Usuario não encontrado!"));
		log.info("[finaliza] UsuarioRepositoryMongoDB - buscaUsuarioPorEmail");
		return usuario;
	}
}
