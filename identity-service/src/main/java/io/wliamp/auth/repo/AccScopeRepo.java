package io.wliamp.auth.repo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import io.wliamp.auth.entity.AccScope;

public interface AccScopeRepo extends ReactiveCrudRepository<AccScope, Void> {}
