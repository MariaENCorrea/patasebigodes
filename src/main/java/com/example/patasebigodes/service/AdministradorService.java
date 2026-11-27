
package com.example.patasebigodes.service;

import com.example.patasebigodes.model.Administrador;
import com.example.patasebigodes.repository.AdministradorRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.patasebigodes.service.AdministradorService;

import java.util.Optional;

@Service
public class AdministradorService {

    private final AdministradorRepository administradorRepository;
    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public AdministradorService(
            AdministradorRepository administradorRepository) {
        this.administradorRepository = administradorRepository;
    }

    public Optional<Administrador> autenticar(String email, String senha) {
        return administradorRepository.findByEmail(email.trim())
                .filter(Administrador::isAtivo)
                .filter(administrador ->
                        passwordEncoder.matches(
                                senha,
                                administrador.getSenha()
                        ));
    }

    public boolean existeAdministrador() {
        return administradorRepository.count() > 0;
    }

    public Administrador criarPrimeiroAdministrador(
            String nome,
            String email,
            String senha) {

        if (existeAdministrador()) {
            throw new IllegalStateException(
                    "Já existe um administrador cadastrado."
            );
        }

        Administrador administrador = new Administrador();

        administrador.setNome(nome.trim());
        administrador.setEmail(email.trim().toLowerCase());
        administrador.setSenha(passwordEncoder.encode(senha));
        administrador.setAtivo(true);

        return administradorRepository.save(administrador);
    }

    public String criptografarSenha(String senha) {
        return passwordEncoder.encode(senha);
    }
}
