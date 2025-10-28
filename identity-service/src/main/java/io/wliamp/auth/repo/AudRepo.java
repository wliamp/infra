package io.wliamp.auth.repo;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import io.wliamp.auth.entity.Aud;

public interface AudRepo extends ReactiveCrudRepository<Aud, Long> {
    //    @Query("""
    //            SELECT *
    //            FROM audiences
    //            WHERE status = true
    //            """)
    Flux<Aud> findByStatusTrue();

    @Query(
            """
            SELECT aud.*
            FROM audiences aud
            JOIN account_audience aa ON aud.id = aa.audience_id
            JOIN accounts a ON aa.account_id = a.id
            WHERE a.id = :accId
            """)
    Flux<Aud> findByAccId(@Param("accId") Long accId);
}
