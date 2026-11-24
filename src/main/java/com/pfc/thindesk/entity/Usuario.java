package com.pfc.thindesk.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usuarios")
@Data
@NoArgsConstructor
public class Usuario {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    // Sempre o hash BCrypt, nunca a senha em texto puro
    private String senha;

    private String role = "USER";

    public Usuario(String username, String senha) {
        this.username = username;
        this.senha = senha;
    }
}
