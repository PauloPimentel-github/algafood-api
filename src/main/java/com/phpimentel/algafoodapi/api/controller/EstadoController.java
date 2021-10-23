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

import com.phpimentel.algafoodapi.domain.model.Estado;
import com.phpimentel.algafoodapi.domain.repository.EstadoRepository;
import com.phpimentel.algafoodapi.domain.service.CadastroEstadoService;

@RestController
@RequestMapping("/estados")
public class EstadoController {

	@Autowired
	private EstadoRepository estadoRepository;
	
	@Autowired
	private CadastroEstadoService cadastroEstadoService;
	
	@GetMapping
	public List<Estado> listar() {
		return this.estadoRepository.findAll();
	}
	
	@GetMapping("/{id}")
	public Estado buscar(@PathVariable Long id) {
		return this.cadastroEstadoService.buscarOuFalhar(id);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Estado adicionar(@RequestBody @Valid Estado estado) {
		return this.cadastroEstadoService.salvar(estado);
	}
	
	@PutMapping("/{id}")
	public Estado atualizar(@PathVariable Long id,
			@RequestBody @Valid Estado estado) {
		Estado estadoAtual = this.cadastroEstadoService.buscarOuFalhar(id);
	    
	    BeanUtils.copyProperties(estado, estadoAtual, "id");
	    
	    return this.cadastroEstadoService.salvar(estadoAtual);
	}
	
	@DeleteMapping("/{id}")
	public void remover(@PathVariable Long id) {
		this.cadastroEstadoService.excluir(id);
	}
}
