package io.wliamp.auth.service.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import io.wliamp.auth.entity.Acc;
import io.wliamp.auth.repo.AccRepo;

import static io.wliamp.auth.entity.Acc.*;
import static io.wliamp.auth.util.Generator.*;

@Service
@RequiredArgsConstructor
public class AccService {
    private final AccRepo accRepo;

    public Mono<Acc> getAccountByCred(String cred) {
        return accRepo.findByCred(cred);
    }

    public Mono<Acc> updateCred(String oldCred, String newCred) {
        return accRepo.findByCred(oldCred).flatMap(acc -> {
            acc.setCred(newCred);
            return accRepo.save(acc);
        });
    }

    public Mono<Long> addNewAccount(String cred) {
        return accRepo.save(
                        builder().code(generateCode(8)).cred(cred).build())
                .map(Acc::getId);
    }
}
