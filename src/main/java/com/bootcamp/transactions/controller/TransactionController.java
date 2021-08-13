package com.bootcamp.transactions.controller;

import com.bootcamp.transactions.bean.RequestTransactionBean;
import com.bootcamp.transactions.dto.BaseResponseDto;
import com.bootcamp.transactions.entity.TransactionEntity;
import com.bootcamp.transactions.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
public class TransactionController
{
    @Autowired
    private TransactionService transactionService;

    @PostMapping("")
    public Mono<ResponseEntity<BaseResponseDto<TransactionEntity>>> createTransaction(
            @RequestBody RequestTransactionBean bean, @RequestParam String type){

        return transactionService.createTransaction(bean, type)
                .flatMap(transactionEntity -> {
            BaseResponseDto<TransactionEntity> response = new BaseResponseDto<TransactionEntity>(HttpStatus.CREATED, "Transaction created", transactionEntity);
            return Mono.just(ResponseEntity.created(URI.create("/transactions")).body(response));
        }).defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
