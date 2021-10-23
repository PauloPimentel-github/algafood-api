package com.phpimentel.algafoodapi.domain.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.phpimentel.algafoodapi.domain.exception.RestauranteNaoEncontradoException;
import com.phpimentel.algafoodapi.domain.model.Cozinha;
import com.phpimentel.algafoodapi.domain.model.Restaurante;
import com.phpimentel.algafoodapi.domain.repository.RestauranteRepository;

@Service
public class CadastroRestauranteService {
	
	@Autowired
	private RestauranteRepository restauranteRepository;
	
	@Autowired
	private CadastroCozinhaService cadastroCozinhaService;
	
	@Transactional
	public Restaurante salvar(Restaurante restaurante) {
		Long cozinhaId = restaurante.getCozinha().getId();
	    
	    Cozinha cozinha = this.cadastroCozinhaService.buscarOuFalhar(cozinhaId);
	    
	    restaurante.setCozinha(cozinha);
	    
	    return restauranteRepository.save(restaurante);
	}
	
	public Restaurante buscarOuFalhar(Long restauranteId) {
	    return restauranteRepository.findById(restauranteId)
	        .orElseThrow(() -> new RestauranteNaoEncontradoException(restauranteId));
	}
}
