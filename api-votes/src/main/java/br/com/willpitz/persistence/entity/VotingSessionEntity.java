package br.com.willpitz.persistence.entity;

import br.com.willpitz.domain.enums.SessionStatusEnum;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Table(name = "voting_session_entity")
@With
public class VotingSessionEntity {

    @Id
    private UUID id;
    @Default
    private Duration duration = Duration.ofMinutes(1);
    private SessionStatusEnum status;
    @Default
    private LocalDateTime openedDate = LocalDateTime.now();
    private LocalDateTime closedDate;
}
