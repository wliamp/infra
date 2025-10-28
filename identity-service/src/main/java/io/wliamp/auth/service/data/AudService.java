package io.wliamp.auth.service.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import io.wliamp.auth.entity.Aud;
import io.wliamp.auth.repo.AudRepo;

@Service
@RequiredArgsConstructor
public class AudService {
    private final AudRepo audRepo;

    public Flux<Aud> getAudiencesByAccountId(Long accId) {
        return audRepo.findByAccId(accId);
    }
}
