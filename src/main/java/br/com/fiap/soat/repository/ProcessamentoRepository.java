package br.com.fiap.soat.repository;

import br.com.fiap.soat.entity.ProcessamentoJpa;
import br.com.fiap.soat.entity.UsuarioJpa;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessamentoRepository extends JpaRepository<ProcessamentoJpa, Long> {
  
  List<ProcessamentoJpa> findByUsuarioOrderByIdDesc(UsuarioJpa usuario);

  Optional<ProcessamentoJpa> findByJobId(String jobId);
}