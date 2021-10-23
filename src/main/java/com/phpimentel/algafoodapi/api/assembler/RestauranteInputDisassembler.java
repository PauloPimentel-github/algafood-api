package com.phpimentel.algafoodapi.api.assembler;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.phpimentel.algafoodapi.api.dto.input.RestauranteInput;
import com.phpimentel.algafoodapi.domain.model.Cozinha;
import com.phpimentel.algafoodapi.domain.model.Restaurante;

@Component
public class RestauranteInputDisassembler {
	
	@Autowired
	private ModelMapper modelMapper;

	public Restaurante toDomainObject(RestauranteInput restauranteInput) {
        return this.modelMapper.map(restauranteInput, Restaurante.class);
    }
	
	public void copyTodomainObject(RestauranteInput restauranteInput, Restaurante restaurante) {
		// org.hibernate.HibernateException: identifier of an instance of 
		// com.phpimentel.algafoodapi.domain.model.Cozinha was altered from 1 to 2
		restaurante.setCozinha(new Cozinha());
		
		this.modelMapper.map(restauranteInput, restaurante);
	}
}
