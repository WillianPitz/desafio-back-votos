package br.com.willpitz.persistence.repository;

import br.com.willpitz.persistence.entity.PollEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PollRepository extends ReactiveCrudRepository<PollEntity, UUID> {

}
