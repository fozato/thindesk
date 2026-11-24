package com.pfc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Os usuarios vem do MongoDB via UsuarioService (UserDetailsService).
    // CSRF fica ligado (padrao): o Thymeleaf injeta o token em todo form com th:action.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/registro", "/error",
                        "/css/**", "/dist/**", "/plugins/**", "/js/**", "/images/**").permitAll() // Login, cadastro e arquivos estáticos
                .anyRequest().authenticated() // Exigir autenticação para qualquer outra página
            )
            .formLogin(form -> form
                .loginPage("/login") // Página de login personalizada
                .defaultSuccessUrl("/", true) // Redirecionar para a home após login bem-sucedido
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // Logout via POST (com token CSRF)
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
        return http.build();
    }
}
