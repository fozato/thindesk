package com.pfc.thindesk.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pfc.thindesk.entity.Usuario;
import com.pfc.thindesk.repository.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Regras de cadastro e login de usuarios, sem precisar do MongoDB (repositorio simulado). */
class UsuarioServiceTest {

    private UsuarioRepository repository;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private UsuarioService service;

    @BeforeEach
    void preparar() {
        repository = mock(UsuarioRepository.class);
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        service = new UsuarioService(repository, encoder);
    }

    @Test
    @DisplayName("Cadastro válido guarda o nome em minúsculas e a senha em BCrypt")
    void cadastroValido() {
        Usuario salvo = service.cadastrar("  Maria.Silva ", "senhaForte1", "senhaForte1");

        assertThat(salvo.getUsername()).isEqualTo("maria.silva");
        assertThat(salvo.getRole()).isEqualTo("USER");
        assertThat(salvo.getSenha())
                .isNotEqualTo("senhaForte1")
                .startsWith("$2a$");
        assertThat(encoder.matches("senhaForte1", salvo.getSenha())).isTrue();
        verify(repository).save(any(Usuario.class));
    }

    @ParameterizedTest(name = "nome inválido: \"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = {"ab", "maria silva", "joão", "usuario@email", "nome-com-mais-de-trinta-caracteres"})
    void nomeInvalido(String username) {
        assertThatThrownBy(() -> service.cadastrar(username, "senhaForte1", "senhaForte1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("3 a 30 caracteres");
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Senha com menos de 8 caracteres é recusada")
    void senhaCurta() {
        assertThatThrownBy(() -> service.cadastrar("maria", "1234567", "1234567"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pelo menos " + UsuarioService.SENHA_MIN);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Senha e confirmação diferentes são recusadas")
    void senhasDiferentes() {
        assertThatThrownBy(() -> service.cadastrar("maria", "senhaForte1", "senhaForte2"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("As senhas não conferem.");
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Nome de usuário já cadastrado é recusado")
    void usuarioExistente() {
        when(repository.existsByUsername("maria")).thenReturn(true);

        assertThatThrownBy(() -> service.cadastrar("Maria", "senhaForte1", "senhaForte1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("já está em uso");
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Cadastro simultâneo barrado pelo índice único vira mensagem amigável")
    void indiceUnico() {
        when(repository.save(any(Usuario.class))).thenThrow(new DuplicateKeyException("duplicado"));

        assertThatThrownBy(() -> service.cadastrar("maria", "senhaForte1", "senhaForte1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("já está em uso");
    }

    @Test
    @DisplayName("Login encontra o usuário ignorando maiúsculas e espaços")
    void loginEncontraUsuario() {
        Usuario usuario = new Usuario("maria", encoder.encode("senhaForte1"));
        when(repository.findByUsername("maria")).thenReturn(Optional.of(usuario));

        UserDetails detalhes = service.loadUserByUsername(" MARIA ");

        assertThat(detalhes.getUsername()).isEqualTo("maria");
        assertThat(detalhes.getPassword()).isEqualTo(usuario.getSenha());
        assertThat(detalhes.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("Login com usuário inexistente é recusado")
    void loginUsuarioInexistente() {
        when(repository.findByUsername("fantasma")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("fantasma"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
