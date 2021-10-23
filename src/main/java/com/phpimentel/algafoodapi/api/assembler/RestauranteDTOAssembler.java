package com.phpimentel.algafoodapi.api.assembler;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.phpimentel.algafoodapi.api.dto.RestauranteDTO;
import com.phpimentel.algafoodapi.domain.model.Restaurante;

@Component
public class RestauranteDTOAssembler {
	
	@Autowired
	private ModelMapper modelMapper;

	public RestauranteDTO toDTO(Restaurante restaurante) {
		return this.modelMapper.map(restaurante, RestauranteDTO.class);
	}
	
	public List<RestauranteDTO> toCollectionDTO(List<Restaurante> restaurantes) {
		return restaurantes.stream()
				.map(restaurante -> toDTO(restaurante))
				.collect(Collectors.toList());
	}
}
