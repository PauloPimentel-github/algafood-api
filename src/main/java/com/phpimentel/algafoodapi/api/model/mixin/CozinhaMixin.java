package com.phpimentel.algafoodapi.api.model.mixin;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phpimentel.algafoodapi.domain.model.Restaurante;

public abstract class CozinhaMixin {

	@JsonIgnore
    private List<Restaurante> restaurantes;
}
