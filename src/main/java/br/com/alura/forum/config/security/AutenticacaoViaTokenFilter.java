package br.com.alura.forum.config.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.alura.forum.modelo.Usuario;
import br.com.alura.forum.repository.UsuarioRepository;

public class AutenticacaoViaTokenFilter extends OncePerRequestFilter{
	
	//em classe de filter (nao gerenciada pelo spring), nao da pra injetar dependencia, entao usamos o construtor
	private TokenService tokenService;
	
	private UsuarioRepository repo;
	
	public AutenticacaoViaTokenFilter(TokenService tokenService, UsuarioRepository repo) {
		this.tokenService = tokenService;
		this.repo = repo;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String token = recuperarToken(request);
		
		boolean valido = tokenService.isTokenValido(token);
		if(valido) {
			 autenticarCliente(token);
		}
		
		filterChain.doFilter(request, response); //seguir o fluxo 
	}

	private void autenticarCliente(String token) {
		Long idUsuario = tokenService.getIdUsuario(token); 
		Usuario usuario = repo.findById(idUsuario).get();
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()); //a senha passada é nula pois em teoria, o usuario ja passou uma vez, aqui só vamos dizer pro Spring autentica-lo
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private String recuperarToken(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		
		if(token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
			return null;
		}
		
		return token.substring(7, token.length());
	}

}
