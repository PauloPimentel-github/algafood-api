package com.phpimentel.algafoodapi.domain.repository;

import java.math.BigDecimal;
import java.util.List;

import com.phpimentel.algafoodapi.domain.model.Restaurante;

public interface RestauranteRepositoryQueries {
	
	List<Restaurante> find(String nome, BigDecimal taxaFreteInicial, BigDecimal taxaFreteFinal);
	
	List<Restaurante> findComFreteGratis(String nome);
}
