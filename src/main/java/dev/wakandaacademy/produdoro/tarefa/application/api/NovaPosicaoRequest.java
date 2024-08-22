package dev.wakandaacademy.produdoro.tarefa.application.api;

import javax.validation.constraints.PositiveOrZero;

import lombok.Getter;

@Getter
public class NovaPosicaoRequest {
	@PositiveOrZero(message = "Posição deve ser maior ou igual a zero.")
	private int novaPosicao;
}