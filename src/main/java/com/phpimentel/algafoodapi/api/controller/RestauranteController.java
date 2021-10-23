package com.phpimentel.algafoodapi.api.controller;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phpimentel.algafoodapi.api.assembler.RestauranteDTOAssembler;
import com.phpimentel.algafoodapi.api.assembler.RestauranteInputDisassembler;
import com.phpimentel.algafoodapi.api.dto.RestauranteDTO;
import com.phpimentel.algafoodapi.api.dto.input.RestauranteInput;
import com.phpimentel.algafoodapi.core.validation.ValidacaoException;
import com.phpimentel.algafoodapi.domain.exception.CozinhaNaoEncontradaException;
import com.phpimentel.algafoodapi.domain.exception.NegocioException;
import com.phpimentel.algafoodapi.domain.model.Restaurante;
import com.phpimentel.algafoodapi.domain.repository.RestauranteRepository;
import com.phpimentel.algafoodapi.domain.service.CadastroRestauranteService;

@RestController
@RequestMapping("/restaurantes")
public class RestauranteController {
	
	@Autowired
	private RestauranteRepository restauranteRepository;
	
	@Autowired
	private CadastroRestauranteService cadastroRestauranteService;
	
	@Autowired
	private SmartValidator smartValidator;
	
	@Autowired
	private RestauranteDTOAssembler restauranteDTOAssembler;
	
	@Autowired
	private RestauranteInputDisassembler restauranteInputDisassembler;

	@GetMapping
	public List<RestauranteDTO> listar() {
		return this.restauranteDTOAssembler.toCollectionDTO(this.restauranteRepository.findAll());
	}
	
	@GetMapping("/{id}")
	public RestauranteDTO buscar(@PathVariable Long id) {
		Restaurante restaurante = this.cadastroRestauranteService.buscarOuFalhar(id);
		
		return this.restauranteDTOAssembler.toDTO(restaurante);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RestauranteDTO adicionar(@RequestBody @Valid RestauranteInput restauranteInput) {
		try {
			Restaurante restaurante = this.restauranteInputDisassembler.toDomainObject(restauranteInput);
			
			return this.restauranteDTOAssembler.toDTO(this.cadastroRestauranteService.salvar(restaurante));
		} catch (CozinhaNaoEncontradaException e) {
			throw new NegocioException(e.getMessage(), e);
		}
	}
	
	@PutMapping("/{restauranteId}")
	public RestauranteDTO atualizar(@PathVariable Long restauranteId, 
			@RequestBody @Valid RestauranteInput restauranteInput) {
	    try {
//	    	Restaurante restaurante = this.restauranteInputDisassembler.toDomainObject(restauranteInput);
	    	
	    	Restaurante restauranteAtual = this.cadastroRestauranteService.buscarOuFalhar(restauranteId);
	    	
	    	this.restauranteInputDisassembler.copyTodomainObject(restauranteInput, restauranteAtual);
		    
//		    BeanUtils.copyProperties(restaurante, restauranteAtual, 
//		            "id", "formasPagamento", "endereco", "dataCadastro", "produtos");
		    
	    	return this.restauranteDTOAssembler.toDTO(this.cadastroRestauranteService.salvar(restauranteAtual));
	    } catch (CozinhaNaoEncontradaException e) {
			throw new NegocioException(e.getMessage(), e);
		}
	}
	
//	@PatchMapping("/{id}")
//	public Restaurante atualizarParcial(@PathVariable Long id, 
//			@RequestBody Map<String, Object> values, HttpServletRequest request) {
//		
//		Restaurante restauranteAtual = this.cadastroRestauranteService.buscarOuFalhar(id);
//	    
//	    this.merge(values, restauranteAtual, request);
//	    
//	    this.validate(restauranteAtual, "restaurante");
//	    
//	    return this.atualizar(id, restauranteAtual);
//	}

	private void validate(Restaurante restaurante, String objectName) {
		BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(restaurante, objectName);
		
		this.smartValidator.validate(restaurante, bindingResult);
		
		if (bindingResult.hasErrors()) {
			throw new ValidacaoException(bindingResult);
		}
	}

	private void merge(Map<String, Object> values, Restaurante restauranteDestino, HttpServletRequest request) {
		
		ServletServerHttpRequest serverHttpRequest = new ServletServerHttpRequest(request);
		
		try {
			
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, true);
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
			
			Restaurante restauranteOrigem = objectMapper.convertValue(values, Restaurante.class);
			
			values.forEach((nomePropriedade, valorPropriedade) -> {
				Field field = ReflectionUtils.findField(Restaurante.class, nomePropriedade);
				field.setAccessible(true);
				
				Object novoValor = ReflectionUtils.getField(field, restauranteOrigem);
				
				ReflectionUtils.setField(field, restauranteDestino, novoValor);
			});
			
		} catch (IllegalArgumentException e) {
			Throwable rootCause = ExceptionUtils.getRootCause(e);
			throw new HttpMessageNotReadableException(e.getMessage(), rootCause, serverHttpRequest);
		}
	}
}
