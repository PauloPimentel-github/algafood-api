package com.phpimentel.algafoodapi.core.jackson;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.phpimentel.algafoodapi.api.model.mixin.CidadeMixin;
import com.phpimentel.algafoodapi.api.model.mixin.CozinhaMixin;
import com.phpimentel.algafoodapi.domain.model.Cidade;
import com.phpimentel.algafoodapi.domain.model.Cozinha;

@Component
public class JacksonMixinModule extends SimpleModule {

	private static final long serialVersionUID = 1L;
	
	public JacksonMixinModule() {
		setMixInAnnotation(Cidade.class, CidadeMixin.class);
	    setMixInAnnotation(Cozinha.class, CozinhaMixin.class);
	}

}
