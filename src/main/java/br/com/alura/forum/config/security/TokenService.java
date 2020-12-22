package br.com.alura.forum.config.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.alura.forum.modelo.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenService {
	
	@Value("${forum.jwt.expiration}") //pega pelo application properties
	private String expiration;
	
	@Value("${forum.jwt.secret}") //pega pelo application properties, é a chave q a lib usa para criptografar e descriptografar os tokens
	private String secret;

	public String gerarToken(Authentication authentication) {
		
		Usuario logado = (Usuario) authentication.getPrincipal();
		Date hoje = new Date();
		Date dataExpiracao = new Date(hoje .getTime() + Long.parseLong(expiration)); //soma a data atual com os milisegundos setados no app props
		
		return Jwts.builder()
				.setIssuer("API do fórum da alura") //quem foi a aplicação que gerou esse token?
				.setSubject(logado.getId().toString()) //esse token pertence a quem? (passar ID do usuario)
				.setIssuedAt(hoje) //quando foi emitido o token?
				.setExpiration(dataExpiracao) //quando vai expirar?
				.signWith(SignatureAlgorithm.HS256, secret)
				.compact();
	}

	public boolean isTokenValido(String token) {
		
		try {
			Jwts.parser().setSigningKey(secret).parseClaimsJws(token); //ao fazer o parse, se nao estiver valido ele estoura exception
			return true;
		} catch (Exception e) {
			return false;
		}		
		
	}

	public Long getIdUsuario(String token) {
		Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
		return Long.parseLong(claims.getSubject());
	}	
	
}
