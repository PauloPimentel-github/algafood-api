package com.phpimentel.algafoodapi.api.exceptionhandler;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.PropertyBindingException;
import com.phpimentel.algafoodapi.core.validation.ValidacaoException;
import com.phpimentel.algafoodapi.domain.exception.EntidadeEmUsoException;
import com.phpimentel.algafoodapi.domain.exception.EntidadeNaoEncontradaException;
import com.phpimentel.algafoodapi.domain.exception.NegocioException;

/**
 * @author phpimentel
 * @date 28/03/2021
 * Classe responsável por tratar exceptions
 * Padronizando o formato de exceptions no corpo de resposta com a (RFC 7807)
 */
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
	
	private static final String MSG_ERRO_GENERICA_USUARIO_FINAL = "Ocorreu um erro interno inesperado no sistema. "
			+ "Tente novamente e se o problema persistir, entre em contato "
			+ "com o administrador do sistema.";
	
	@Autowired
	private MessageSource messageSource;
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
	        HttpHeaders headers, HttpStatus status, WebRequest request) {
		
	    return this.handleValidationInternal(exception, exception.getBindingResult(), headers, status, request);
	}      

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleUncaught(Exception exception, WebRequest request) {
	    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;		
	    ProblemType problemType = ProblemType.ERRO_DE_SISTEMA;
	    String detail = MSG_ERRO_GENERICA_USUARIO_FINAL;

	    // Importante colocar o printStackTrace (pelo menos por enquanto, que não estamos
	    // fazendo logging) para mostrar a stacktrace no console
	    // Se não fizer isso, você não vai ver a stacktrace de exceptions que seriam importantes
	    // para você durante, especialmente na fase de desenvolvimento
	    exception.printStackTrace();
	    
	    Problem problem = createProblemBuilder(status, problemType, detail).build();

	    return handleExceptionInternal(exception, problem, new HttpHeaders(), status, request);
	}        
	
	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException exception, 
	        HttpHeaders headers, HttpStatus status, WebRequest request) {
	    
	    ProblemType problemType = ProblemType.RECURSO_NAO_ENCONTRADO;
	    String detail = String.format("O recurso %s, que você tentou acessar, é inexistente.", 
	    		exception.getRequestURL());
	    
	    Problem problem = createProblemBuilder(status, problemType, detail)
	    		.userMessage(MSG_ERRO_GENERICA_USUARIO_FINAL)
	    		.build();
	    
	    return handleExceptionInternal(exception, problem, headers, status, request);
	}    
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exception,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		//ExceptionUtils método do pacote commons-lang3
		Throwable rootCause = ExceptionUtils.getRootCause(exception);

		if (rootCause instanceof InvalidFormatException) {
			return handleInvalidFormat((InvalidFormatException) rootCause, headers, status, request);
		} else if (rootCause instanceof PropertyBindingException) {
			return handlePropertyBinding((PropertyBindingException) rootCause, headers, status, request); 
		}

		ProblemType problemType = ProblemType.MENSAGEM_INCOMPREENSIVEL;
		String detail = "O corpo da requisição está inválido. Verifique erro de sintaxe.";

		Problem problem = this.createProblemBuilder(status, problemType, detail)
				.userMessage(MSG_ERRO_GENERICA_USUARIO_FINAL)
				.build();

		return handleExceptionInternal(exception, problem, new HttpHeaders(), status, request);
	}
	
	@Override
	protected ResponseEntity<Object> handleTypeMismatch(org.springframework.beans.TypeMismatchException exception,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		if (exception instanceof MethodArgumentTypeMismatchException) {
	        return handleMethodArgumentTypeMismatch(
	                (MethodArgumentTypeMismatchException) exception, headers, status, request);
	    }

	    return super.handleTypeMismatch(exception, headers, status, request);
	}
	
	private ResponseEntity<Object> handleMethodArgumentTypeMismatch(
	        MethodArgumentTypeMismatchException exception, HttpHeaders headers,
	        HttpStatus status, WebRequest request) {

	    ProblemType problemType = ProblemType.PARAMETRO_INVALIDO;

	    String detail = String.format("O parâmetro de URL '%s' recebeu o valor '%s', "
	            + "que é de um tipo inválido. Corrija e informe um valor compatível com o tipo %s.",
	            exception.getName(), exception.getValue(), exception.getRequiredType().getSimpleName());

	    Problem problem = this.createProblemBuilder(status, problemType, detail)
	    		.userMessage(MSG_ERRO_GENERICA_USUARIO_FINAL)
	    		.build();

	    return handleExceptionInternal(exception, problem, headers, status, request);
	}

	private ResponseEntity<Object> handleInvalidFormat(InvalidFormatException exception,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		String path = this.joinPath(exception.getPath());
		
		ProblemType problemType = ProblemType.MENSAGEM_INCOMPREENSIVEL;
		
		String detail = String.format("A propriedade '%s' recebeu o valor '%s', "
				+ "que é de um tipo inválido. Corrija e informe um valor compatível com o tipo %s.",
				path, exception.getValue(), exception.getTargetType().getSimpleName());

		Problem problem = this.createProblemBuilder(status, problemType, detail)
				.userMessage(MSG_ERRO_GENERICA_USUARIO_FINAL)
				.build();
		
		return handleExceptionInternal(exception, problem, headers, status, request);
	}
	
	private ResponseEntity<Object> handlePropertyBinding(PropertyBindingException ex,
	        HttpHeaders headers, HttpStatus status, WebRequest request) {

	    String path = joinPath(ex.getPath());
	    
	    ProblemType problemType = ProblemType.MENSAGEM_INCOMPREENSIVEL;
	    String detail = String.format("A propriedade '%s' não existe. "
	            + "Corrija ou remova essa propriedade e tente novamente.", path);

	    Problem problem = this.createProblemBuilder(status, problemType, detail)
	    		.userMessage(MSG_ERRO_GENERICA_USUARIO_FINAL)
	    		.build();
	    
	    return handleExceptionInternal(ex, problem, headers, status, request);
	}    

	@ExceptionHandler(EntidadeNaoEncontradaException.class)
	public ResponseEntity<?> handleEntidadeNaoEncontrada(
			EntidadeNaoEncontradaException exception, WebRequest request) {
		
		HttpStatus status = HttpStatus.NOT_FOUND;
		ProblemType problemType = ProblemType.ENTIDADE_NAO_ENCONTRADA;
		String detail = exception.getMessage();
			
		Problem problem = this.createProblemBuilder(status, problemType, detail)
				.userMessage(detail)
				.build();
				
		return handleExceptionInternal(exception, problem, new HttpHeaders(), status, request);
		
	}
	
	@ExceptionHandler(EntidadeEmUsoException.class)
	public ResponseEntity<?> handleEntidadeEmUso(
			EntidadeEmUsoException exception, WebRequest request) {
		
		HttpStatus status = HttpStatus.CONFLICT;
	    ProblemType problemType = ProblemType.ENTIDADE_EM_USO;
	    String detail = exception.getMessage();
	    
	    Problem problem = this.createProblemBuilder(status, problemType, detail)
	    		.userMessage(detail)
	    		.build();
	    
		return handleExceptionInternal(exception, problem, new HttpHeaders(), status, request);
	}
	
	@ExceptionHandler(NegocioException.class)
	public ResponseEntity<?> handleNegocio(
			NegocioException exception, WebRequest request) {
		
		HttpStatus status = HttpStatus.BAD_REQUEST;
	    ProblemType problemType = ProblemType.ERRO_NEGOCIO;
	    String detail = exception.getMessage();
	    
	    Problem problem = this.createProblemBuilder(status, problemType, detail)
	    		.userMessage(detail)
	    		.build();
		
		return handleExceptionInternal(exception, problem, new HttpHeaders(), status, request);
	}
	
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		
		if (Objects.isNull(body)) {
			body = Problem.builder()
					.timestamp(OffsetDateTime.now())
					.title(status.getReasonPhrase())
					.status(status.value())
					.userMessage(MSG_ERRO_GENERICA_USUARIO_FINAL)
					.build();
			
		} else if (body instanceof String) {
			body = Problem.builder()
					.timestamp(OffsetDateTime.now())
					.title((String) body)
					.status(status.value())
					.userMessage(MSG_ERRO_GENERICA_USUARIO_FINAL)
					.build();
		}

		return super.handleExceptionInternal(exception, body, headers, status, request);
	}
	
	@ExceptionHandler({ ValidacaoException.class })
	public ResponseEntity<Object> handleValidacaoException(ValidacaoException ex, WebRequest request) {
	    return handleValidationInternal(ex, ex.getBindingResult(), new HttpHeaders(), 
	            HttpStatus.BAD_REQUEST, request);
	}  
	
	private ResponseEntity<Object> handleValidationInternal(Exception exception, BindingResult bindingResult, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		        
		    ProblemType problemType = ProblemType.DADOS_INVALIDOS;
		    String detail = "Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente.";
		    
		    List<ProblemObject> problemObjects = bindingResult.getAllErrors().stream()
		            .map(objectError -> {
		                String message = messageSource.getMessage(objectError, LocaleContextHolder.getLocale());
		                
		                String name = objectError.getObjectName();
		                
		                if (objectError instanceof FieldError) {
		                    name = ((FieldError) objectError).getField();
		                }
		                
		                return ProblemObject.builder()
		                    .name(name)
		                    .userMessage(message)
		                    .build();
		            })
		            .collect(Collectors.toList());
		    
		    Problem problem = createProblemBuilder(status, problemType, detail)
		        .userMessage(detail)
		        .objects(problemObjects)
		        .build();
		    
		    return handleExceptionInternal(exception, problem, headers, status, request);
		}
	
	private Problem.ProblemBuilder createProblemBuilder(HttpStatus status, 
			ProblemType problemType, String detail) {
		
		return Problem.builder()
				.timestamp(OffsetDateTime.now())
				.status(status.value())
				.type(problemType.getUri())
				.title(problemType.getTitle())
				.detail(detail);
	}
	
	private String joinPath(List<Reference> references) {
	    return references.stream()
	        .map(ref -> ref.getFieldName())
	        .collect(Collectors.joining("."));
	}  
	
}
