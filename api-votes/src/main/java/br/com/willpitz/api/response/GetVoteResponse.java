package br.com.willpitz.api.response;

import br.com.willpitz.domain.enums.VoteEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetVoteResponse {

    private UUID id;
    private UUID votingSessionId;
    private String cpf;
    private VoteEnum voteEnum;
}
