package com.phpimentel.algafoodapi.api.model.mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.phpimentel.algafoodapi.domain.model.Estado;

public abstract class CidadeMixin {

	@JsonIgnoreProperties(value = "nome", allowGetters = true)
    private Estado estado;
}
