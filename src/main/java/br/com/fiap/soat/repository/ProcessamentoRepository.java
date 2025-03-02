package br.com.fiap.soat.repository;

import br.com.fiap.soat.entity.ProcessamentoJpa;
import br.com.fiap.soat.entity.UsuarioJpa;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessamentoRepository extends JpaRepository<ProcessamentoJpa, Long> {

  // Busca os registros de um usuário, ordenados pelo timestamp (mais recente primeiro)
  List<ProcessamentoJpa> findByUsuarioOrderByNumeroVideoDesc(UsuarioJpa usuario);
}