package com.phpimentel.algafoodapi.domain.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.phpimentel.algafoodapi.domain.exception.CozinhaNaoEncontradaException;
import com.phpimentel.algafoodapi.domain.exception.EntidadeEmUsoException;
import com.phpimentel.algafoodapi.domain.model.Cozinha;
import com.phpimentel.algafoodapi.domain.repository.CozinhaRepository;

@Service
public class CadastroCozinhaService {

	private static final String MSG_COZINHA_EM_USO = "Cozinha de código %d não pode ser removida, pois está em uso";
	
	@Autowired
	private CozinhaRepository cozinhaRepository;
	
	@Transactional
	public Cozinha salvar(Cozinha cozinha) {
		return this.cozinhaRepository.save(cozinha);
	}
	
	@Transactional
	public void excluir(Long id) {
		try {
			this.cozinhaRepository.deleteById(id);
			
		} catch (EmptyResultDataAccessException e) {
			throw new CozinhaNaoEncontradaException(id);
			
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
					String.format(MSG_COZINHA_EM_USO, id));
		}
	}
	
	public Cozinha buscarOuFalhar(Long id) {
		return this.cozinhaRepository.findById(id)
				.orElseThrow(() -> new CozinhaNaoEncontradaException(id));
	}
}
