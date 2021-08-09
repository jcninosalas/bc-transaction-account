package com.bootcamp.transactions.controller;

import com.bootcamp.transactions.bean.RequestTransactionBean;
import com.bootcamp.transactions.service.TransactionService;
import com.bootcamp.transactions.service.impl.TransactionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/transaction")
public class TransactionController
{
    @Autowired
    private TransactionService transactionService;

    @PostMapping("")
    public Mono<ResponseEntity<Map<String, Object>>> createTransaction(
            @RequestBody RequestTransactionBean bean, @RequestParam String type){
        Map<String, Object> response = new HashMap<>();
        return transactionService.createTransaction(bean, type).flatMap(transactionEntity -> {
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Se creo la transacción");
            response.put("body", transactionEntity);

            return Mono.just(ResponseEntity.ok().body(response));
        }).defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
