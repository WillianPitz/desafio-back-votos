package br.com.willpitz.messaging.messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoteResultMessage {

    private UUID votingSessionId;
    private Integer totalVotes;
    private Integer votesAgainst;
    private Integer votesInFavour;
}
