package io.wliamp.auth.service.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import io.wliamp.auth.entity.AccScope;
import io.wliamp.auth.repo.AccScopeRepo;
import io.wliamp.auth.repo.ScopeRepo;

@Service
@RequiredArgsConstructor
public class AccScopeService {
    private final AccScopeRepo accScopeRepo;

    private final ScopeRepo scopeRepo;

    public Flux<AccScope> addNewAccount(Long accId) {
        return scopeRepo
                .findByStatusTrue()
                .map(scope ->
                        AccScope.builder().accId(accId).scopeId(scope.getId()).build())
                .collectList()
                .flatMapMany(auds -> Flux.fromIterable(auds).flatMap(accScopeRepo::save));
    }
}
