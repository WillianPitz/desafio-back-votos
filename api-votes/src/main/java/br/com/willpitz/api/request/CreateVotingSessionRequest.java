package br.com.willpitz.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Duration;
import java.util.UUID;

@Data
public class CreateVotingSessionRequest {

    @NotNull(message = "pollId is required")
    @Schema(description = "Id da pauta")
    private UUID pollId;
    @NotNull(message = "duration is required")
    @Schema(description = "Duração da sessão de votação", example = "PT1M")
    private Duration duration;

}
