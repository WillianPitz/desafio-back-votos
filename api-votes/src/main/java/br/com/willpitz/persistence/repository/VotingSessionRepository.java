package br.com.willpitz.persistence.repository;

import br.com.willpitz.persistence.entity.VotingSessionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VotingSessionRepository extends ReactiveCrudRepository<VotingSessionEntity, UUID> {

}
