package br.com.fiap.soat.repository;

import br.com.fiap.soat.entity.UsuarioJpa;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioJpa, Long> {
  
  Optional<UsuarioJpa> findByEmail(String email);
  
}
