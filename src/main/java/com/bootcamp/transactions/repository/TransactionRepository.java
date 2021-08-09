package com.bootcamp.transactions.repository;

import com.bootcamp.transactions.entity.TransactionEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface TransactionRepository extends ReactiveMongoRepository<TransactionEntity, String> {
    public Mono<TransactionEntity> findByDocumentNumberAndAccountNumber(String documentNumber,
                                                                        String numberAccount);
}
