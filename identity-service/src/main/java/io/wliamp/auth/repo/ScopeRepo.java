package io.wliamp.auth.repo;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import io.wliamp.auth.entity.Scope;

public interface ScopeRepo extends ReactiveCrudRepository<Scope, Long> {
    //    @Query("""
    //            SELECT *
    //            FROM scopes
    //            WHERE status = true
    //            """)
    Flux<Scope> findByStatusTrue();

    @Query(
            """
            SELECT s.*
            FROM scopes s
            JOIN account_scope sa ON s.id = sa.scope_id
            JOIN accounts a ON sa.account_id = a.id
            WHERE a.id = :accId
            """)
    Flux<Scope> findByAccId(@Param("accId") Long accId);
}
