package com.bootcamp.transactions.service;

import com.bootcamp.transactions.bean.RequestTransactionBean;
import com.bootcamp.transactions.entity.TransactionEntity;
import reactor.core.publisher.Mono;

public interface TransactionService
{
    Mono<TransactionEntity> createTransaction(RequestTransactionBean bean, String type);
}
