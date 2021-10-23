package com.phpimentel.algafoodapi.api.controller;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cache")
public class CacheController {

	@Cacheable("hello")
	@GetMapping("/hello")
	public String hello() {
		System.out.println("Sem cache");
		return "Hello World";
	}
	
	@CacheEvict("hello")
	@GetMapping("/cancel")
	public String cancel() {
		System.out.println("Limpando o cache");
		return "Cache Cancelado";
	}
}
