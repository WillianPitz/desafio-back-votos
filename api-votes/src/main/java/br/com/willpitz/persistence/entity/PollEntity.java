package br.com.willpitz.persistence.entity;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Table(name = "poll_entity")
@With
public class PollEntity {

    @Id
    private UUID id;

    private UUID votingSessionId;
    private String name;
    private String description;
    @Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    private Integer totalVotes;
    private Integer votesAgainst;
    private Integer votesInFavour;
}
