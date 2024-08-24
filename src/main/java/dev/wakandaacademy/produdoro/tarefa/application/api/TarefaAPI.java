package dev.wakandaacademy.produdoro.tarefa.application.api;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/tarefa")
public interface TarefaAPI {
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	TarefaIdResponse postNovaTarefa(@RequestBody @Valid TarefaRequest tarefaRequest);

	@GetMapping("/{idTarefa}")
	@ResponseStatus(code = HttpStatus.OK)
	TarefaDetalhadoResponse detalhaTarefa(@RequestHeader(name = "Authorization", required = true) String token,
			@PathVariable UUID idTarefa);

	@DeleteMapping("/usuario/{idUsuario}/limpar-todas-as-tarefas")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	void deletaTodasSuasTarefas(@RequestHeader(name = "Authorization", required = true) String token,
			@PathVariable UUID idUsuario);

	@PatchMapping("/{idTarefa}/ativa")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	void defineTarefaComoAtiva(@RequestHeader(name = "Authorization", required = true) String token,
			@PathVariable UUID idTarefa);

}
