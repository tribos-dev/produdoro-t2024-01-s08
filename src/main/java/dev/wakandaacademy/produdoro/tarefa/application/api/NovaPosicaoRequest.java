package dev.wakandaacademy.produdoro.tarefa.application.api;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;

@Getter
public class NovaPosicaoRequest {
	@NotEmpty(message = "Digite a nova posição da tarefa.")
	private int novaPosicao;
}