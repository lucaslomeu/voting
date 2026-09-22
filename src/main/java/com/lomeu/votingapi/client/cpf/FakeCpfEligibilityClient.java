package com.lomeu.votingapi.client.cpf;

import com.lomeu.votingapi.exception.BusinessException;
import com.lomeu.votingapi.exception.ErrorCode;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class FakeCpfEligibilityClient implements CpfEligibilityClient {

    private final Random random;

    public FakeCpfEligibilityClient() {
        this(new Random());
    }

    public FakeCpfEligibilityClient(Random random) {
        this.random = random;
    }

    @Override
    public CpfEligibilityStatus check(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            throw new BusinessException(ErrorCode.CPF_NOT_FOUND);
        }
        return random.nextBoolean()
                ? CpfEligibilityStatus.ABLE_TO_VOTE
                : CpfEligibilityStatus.UNABLE_TO_VOTE;
    }
}
