package br.com.willpitz.api.request;

import br.com.willpitz.domain.enums.VoteEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateVoteRequest {

    @NotNull(message = "votingSessionId is required")
    @Schema(description = "Id da sessão")
    private UUID votingSessionId;
    @NotNull(message = "cpf is required")
    @Schema(description = "Cpf do usuário")
    private String cpf;
    @NotNull(message = "vote is required")
    @Schema(description = "Voto", example = "SIM")
    private VoteEnum vote;

}
