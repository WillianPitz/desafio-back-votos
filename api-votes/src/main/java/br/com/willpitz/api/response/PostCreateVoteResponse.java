package br.com.willpitz.api.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PostCreateVoteResponse {

    private UUID id;
}
