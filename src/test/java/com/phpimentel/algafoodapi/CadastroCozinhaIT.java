package com.phpimentel.algafoodapi;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import com.phpimentel.algafoodapi.domain.exception.EntidadeEmUsoException;
import com.phpimentel.algafoodapi.domain.exception.EntidadeNaoEncontradaException;
import com.phpimentel.algafoodapi.domain.model.Cozinha;
import com.phpimentel.algafoodapi.domain.service.CadastroCozinhaService;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CadastroCozinhaIT {
	
	@LocalServerPort
	private int port;
	
//	@Autowired
//	private CadastroCozinhaService cadastroCozinhaService;

	/* Testes de Integração */
//	@Test
//	public void testarCadastroCozinhaComSucesso() {
//		//cenário
//		Cozinha cozinha = new Cozinha();
//		cozinha.setNome("Chinesa");
//		
//		//ação
//		cozinha = this.cadastroCozinhaService.salvar(cozinha);
//		
//		//validação
//		assertThat(cozinha).isNotNull();
//		assertThat(cozinha.getId()).isNotNull();
//	}
//	
//	@Test(expected = ConstraintViolationException.class)
//	public void testarCadastroCozinhaSemNomeException() {
//		//cenário
//		Cozinha cozinha = new Cozinha();
//		cozinha.setNome(null);
//		
//		//ação
//		cozinha = this.cadastroCozinhaService.salvar(cozinha);
//	}
//	
//	@Test(expected = EntidadeEmUsoException.class)
//	public void testarExcluirCozinhaEmUsoException() {
//		//cenário
//		Cozinha cozinha = this.cadastroCozinhaService.buscarOuFalhar(1L);
//		
//		//ação
//		this.cadastroCozinhaService.excluir(cozinha.getId());
//	}
//	
//	@Test(expected = EntidadeNaoEncontradaException.class)
//	public void testarExcluirCozinhaInexistenteException() {
//		//cenário
//		Long cozinhaId = 100L;
//		
//		//ação
//		this.cadastroCozinhaService.excluir(cozinhaId);
//	}
	
	/* Testes de API */
	
	@Test
	public void testarRetornarStatus200_QuandoConsultarCozinhas() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		
		RestAssured.given()
			.basePath("/cozinhas")
			.port(port)
			.accept(ContentType.JSON)
		.when()
			.get()
		.then()
			.statusCode(HttpStatus.OK.value());
	}
	
	
}
