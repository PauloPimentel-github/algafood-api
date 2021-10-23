package com.phpimentel.algafoodapi.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.phpimentel.algafoodapi.domain.exception.EstadoNaoEncontradoException;
import com.phpimentel.algafoodapi.domain.exception.NegocioException;
import com.phpimentel.algafoodapi.domain.model.Cidade;
import com.phpimentel.algafoodapi.domain.repository.CidadeRepository;
import com.phpimentel.algafoodapi.domain.service.CadastroCidadeService;

@RestController
@RequestMapping("/cidades")
public class CidadeController {

	@Autowired
	private CidadeRepository cidadeRepository;
	
	@Autowired
	private CadastroCidadeService cadastroCidadeService;
	
	@GetMapping
	public List<Cidade> listar() {
		return this.cidadeRepository.findAll();
	}
	
	@GetMapping("/{id}")
	public Cidade buscar(@PathVariable Long id) {
		return this.cadastroCidadeService.buscarOuFalhar(id);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Cidade adicionar(@RequestBody @Valid Cidade cidade) {
		try { 
			return this.cadastroCidadeService.salvar(cidade);
		} catch (EstadoNaoEncontradoException e) {
			throw new NegocioException(e.getMessage(), e);
		}
	}
	
	@PutMapping("/{id}")
	public Cidade atualizar(@PathVariable Long id, @RequestBody @Valid Cidade cidade) {
	    try {
	    	Cidade cidadeAtual = this.cadastroCidadeService.buscarOuFalhar(id);
	    	
	    	BeanUtils.copyProperties(cidade, cidadeAtual, "id");
	    	
	    	return this.cadastroCidadeService.salvar(cidadeAtual);
		} catch (EstadoNaoEncontradoException e) {
			throw new NegocioException(e.getMessage(), e);
		}
	}
	
	@DeleteMapping("/{id}")
	public void remover(@PathVariable Long id) {
		this.cadastroCidadeService.excluir(id);
	}
	
}
