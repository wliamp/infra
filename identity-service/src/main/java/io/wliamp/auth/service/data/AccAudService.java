package io.wliamp.auth.service.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import io.wliamp.auth.entity.AccAud;
import io.wliamp.auth.repo.AccAudRepo;
import io.wliamp.auth.repo.AudRepo;

@Service
@RequiredArgsConstructor
public class AccAudService {
    private final AccAudRepo accAudRepo;

    private final AudRepo audRepo;

    public Flux<AccAud> addNewAccount(Long accId) {
        return audRepo.findByStatusTrue()
                .map(aud -> AccAud.builder().accId(accId).audId(aud.getId()).build())
                .collectList()
                .flatMapMany(auds -> Flux.fromIterable(auds).flatMap(accAudRepo::save));
    }
}
