package com.formable.model;

import java.util.List;

public record RedlineRequest(
    String templateId,
    RedlineRequestStatus status,
    List<RedlineMember> members,
    boolean testMode,
    RedlineRoundParty currentRound) {}
