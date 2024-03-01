package br.com.willpitz.api.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PostCreateVotingSessionResponse {

    private UUID id;
}
