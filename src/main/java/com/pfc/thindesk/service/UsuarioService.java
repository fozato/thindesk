package com.pfc.thindesk.service;

import com.pfc.thindesk.entity.Usuario;
import com.pfc.thindesk.repository.UsuarioRepository;
import java.util.Locale;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Usuarios do sistema guardados no MongoDB (colecao "usuarios"), com senha em BCrypt.
 * Tambem e o UserDetailsService usado pelo Spring Security no login.
 */
@Service
public class UsuarioService implements UserDetailsService {

    public static final int SENHA_MIN = 8;
    private static final String USERNAME_REGEX = "^[a-z0-9._-]{3,30}$";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(normalizar(username))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado"));
        return User.withUsername(usuario.getUsername())
                .password(usuario.getSenha())
                .roles(usuario.getRole())
                .build();
    }

    /**
     * Cadastra um novo usuario. Lanca IllegalArgumentException com a mensagem
     * a ser exibida na tela quando os dados forem invalidos.
     */
    public Usuario cadastrar(String username, String senha, String confirmacao) {
        String nome = normalizar(username);
        if (!nome.matches(USERNAME_REGEX)) {
            throw new IllegalArgumentException(
                    "O usuário deve ter de 3 a 30 caracteres: letras, números, ponto, hífen ou sublinhado.");
        }
        if (senha == null || senha.length() < SENHA_MIN) {
            throw new IllegalArgumentException("A senha deve ter pelo menos " + SENHA_MIN + " caracteres.");
        }
        if (!senha.equals(confirmacao)) {
            throw new IllegalArgumentException("As senhas não conferem.");
        }
        if (usuarioRepository.existsByUsername(nome)) {
            throw new IllegalArgumentException("Esse nome de usuário já está em uso.");
        }
        try {
            return usuarioRepository.save(new Usuario(nome, passwordEncoder.encode(senha)));
        } catch (DuplicateKeyException e) {
            // Dois cadastros simultaneos com o mesmo nome: o indice unico barra o segundo
            throw new IllegalArgumentException("Esse nome de usuário já está em uso.");
        }
    }

    private static String normalizar(String username) {
        return username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
    }
}
