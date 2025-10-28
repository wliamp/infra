package io.wliamp.auth.service.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import io.wliamp.auth.entity.Scope;
import io.wliamp.auth.repo.ScopeRepo;

@Service
@RequiredArgsConstructor
public class ScopeService {
    private final ScopeRepo scopeRepo;

    public Flux<Scope> getScopesByAccountId(Long accId) {
        return scopeRepo.findByAccId(accId);
    }
}
