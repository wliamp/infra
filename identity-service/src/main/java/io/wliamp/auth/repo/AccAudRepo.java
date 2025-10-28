package io.wliamp.auth.repo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import io.wliamp.auth.entity.AccAud;

public interface AccAudRepo extends ReactiveCrudRepository<AccAud, Void> {}
