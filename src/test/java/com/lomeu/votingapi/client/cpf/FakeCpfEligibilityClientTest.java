package com.lomeu.votingapi.client.cpf;

import com.lomeu.votingapi.exception.BusinessException;
import com.lomeu.votingapi.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FakeCpfEligibilityClientTest {

    @Test
    void returnsConfiguredEligibilityStatus() {
        Random random = mock(Random.class);
        when(random.nextBoolean()).thenReturn(true);

        assertThat(new FakeCpfEligibilityClient(random).check("12345678901"))
                .isEqualTo(CpfEligibilityStatus.ABLE_TO_VOTE);
    }

    @Test
    void rejectsInvalidCpfAsNotFound() {
        assertThatThrownBy(() -> new FakeCpfEligibilityClient(new Random()).check("invalid"))
                .isInstanceOf(BusinessException.class)
                .extracting(exception -> ((BusinessException) exception).errorCode())
                .isEqualTo(ErrorCode.CPF_NOT_FOUND);
    }
}
