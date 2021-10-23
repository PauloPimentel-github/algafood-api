package com.phpimentel.algafoodapi.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.phpimentel.algafoodapi.domain.model.Restaurante;

@Repository
public interface RestauranteRepository 
				extends CustomJpaRepository<Restaurante, Long>, 
						RestauranteRepositoryQueries,
						JpaSpecificationExecutor<Restaurante> {

	@Query("from Restaurante as r join fetch r.cozinha")
	List<Restaurante> findAll();
}
