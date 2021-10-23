package com.phpimentel.algafoodapi.api.dto.input;

import java.math.BigDecimal;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import com.phpimentel.algafoodapi.core.validation.Multiplo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RestauranteInput {
	
	@NotBlank
	private String nome;
	
	@NotNull
	@PositiveOrZero
	@Multiplo(numero = 5)
	private BigDecimal taxaFrete;
	
	@Valid
	@NotNull
	private CozinhaIdInput cozinha;
}
