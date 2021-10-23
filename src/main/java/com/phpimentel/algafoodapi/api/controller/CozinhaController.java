package com.phpimentel.algafoodapi.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.phpimentel.algafoodapi.domain.model.Cozinha;
import com.phpimentel.algafoodapi.domain.repository.CozinhaRepository;
import com.phpimentel.algafoodapi.domain.service.CadastroCozinhaService;

@RestController
@RequestMapping("/cozinhas")
public class CozinhaController {
	
	@Autowired
	private CozinhaRepository cozinhaRepository;
	
	@Autowired
	private CadastroCozinhaService cadastroCozinhaService;

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Cozinha> listar() {
		return this.cozinhaRepository.findAll();
	}
	
	@GetMapping("/{id}")
	public Cozinha buscar(@PathVariable Long id) {
		return this.cadastroCozinhaService.buscarOuFalhar(id);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Cozinha adicionar(@RequestBody @Valid Cozinha cozinha) {
		return this.cadastroCozinhaService.salvar(cozinha);
	}
	
	@PutMapping("/{id}")
	public Cozinha atualizar(@PathVariable Long id, @RequestBody @Valid Cozinha cozinha) {
		Cozinha cozinhaAtual = this.cadastroCozinhaService.buscarOuFalhar(id);
		
		BeanUtils.copyProperties(cozinha, cozinhaAtual, "id");
		
		return this.cadastroCozinhaService.salvar(cozinhaAtual);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long id) {
		this.cadastroCozinhaService.excluir(id);
	}
}
