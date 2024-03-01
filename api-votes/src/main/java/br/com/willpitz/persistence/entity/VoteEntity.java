package br.com.willpitz.persistence.entity;

import br.com.willpitz.domain.enums.VoteEnum;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Builder
@Table(name = "vote_entity")
public class VoteEntity {

    @Id
    private UUID id;
    private UUID votingSessionId;
    private String cpf;
    private VoteEnum voteEnum;

}
