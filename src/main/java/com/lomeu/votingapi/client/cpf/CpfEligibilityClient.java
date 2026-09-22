package com.lomeu.votingapi.client.cpf;

public interface CpfEligibilityClient {

    CpfEligibilityStatus check(String cpf);
}
