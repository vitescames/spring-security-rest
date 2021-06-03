package br.com.alura.forum.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.alura.forum.repository.UsuarioRepository;

@EnableWebSecurity
@Configuration
public class SecurityConfigurations extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private AutenticacaoService autenticacaoService;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Override
	@Bean
	protected AuthenticationManager authenticationManager() throws Exception {
		// o spring nao sabe instanciar AuthenticationManager só com o Autowired no controller, entao aqui no configuration devolvemos o respectivo bean
		return super.authenticationManager();
	}
	
	//Configurar parte de autenticação do AuthenticationManager
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		//no autenticacaoService eu defino como quero trazer o usuario, logo após isso, o spring valida a senha descriptografando com o algoritmo que definir
		auth.userDetailsService(autenticacaoService).passwordEncoder(new BCryptPasswordEncoder());
	}
	
	//Configurações de autorização (perfil de acesso), URL's que eu quero deixar publicas ou ter um controle de acesso
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		.antMatchers(HttpMethod.GET, "/topicos").permitAll()
		.antMatchers(HttpMethod.GET, "/").permitAll()
		.antMatchers(HttpMethod.GET, "/topicos/*").permitAll()
		.antMatchers(HttpMethod.POST, "/auth").permitAll()
		.antMatchers(HttpMethod.GET, "/actuator/**").permitAll() //nao é seguro deixar o actuator como permitAll, mas pra teste deixamos assim
		.anyRequest().authenticated()
		.and().csrf().disable() //desabilitar validação de Cross-site request forgery (ataque hacker) pois a aplicação será stetaless
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //ao fazer autenticação, nao criar sessão
		.and().addFilterBefore(new AutenticacaoViaTokenFilter(tokenService, usuarioRepository), UsernamePasswordAuthenticationFilter.class); //antes de fazer a autenticacao do spring, usar nosso filtro
	}
	
	//Configurações de recursos estáticos (js, css, imagens, etc)
	@Override
	public void configure(WebSecurity web) throws Exception {
		 web.ignoring()
	        .antMatchers("/**.html", "/v2/api-docs", "/webjars/**", "/configuration/**", "/swagger-resources/**");
	}

}
