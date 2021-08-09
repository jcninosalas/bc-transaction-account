package com.bootcamp.transactions.service.impl;

import com.bootcamp.transactions.bean.RequestTransactionBean;
import com.bootcamp.transactions.entity.Movement;
import com.bootcamp.transactions.entity.TransactionEntity;
import com.bootcamp.transactions.repository.TransactionRepository;
import com.bootcamp.transactions.service.TransactionService;
import com.bootcamp.transactions.util.Webclients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private Webclients webclients;

    @Override
    public Mono<TransactionEntity> createTransaction(RequestTransactionBean bean, String type) {
        return webclients.getAccountClient(bean.getNumberAccount(), bean.getDocumentNumber())
                .flatMap(responseAccountClient -> {
                    log.info("Response : {}", responseAccountClient.toString() );
                    List<Map<String, Object>> account = (List<Map<String, Object>>) responseAccountClient.getBody().get("account");
                    Map<String, Object> accountOnly = new HashMap<>();

                    for(Map<String, Object> only : account){
                        if(only.get("accountNumber").equals(bean.getNumberAccount()))
                            accountOnly = only;
                    }

                    Movement move = new Movement(type, bean.getAmount(), new Date());

                    switch ((String) accountOnly.get("type")){
                        case "Current":
                            return this.createTransactionCurrent(type, bean, accountOnly, move);

                        case "Saving":
                            return this.createTransactionSaving(type, bean, accountOnly, move);

                        case "Fixed":
                            return this.createTransactionFixed(type, bean, accountOnly, move);

                        default: return Mono.empty();
                    }
                });
    }

    public Mono<TransactionEntity> createTransactionCurrent(String type, RequestTransactionBean bean,
                                                            Map<String, Object> accountOnly, Movement move){
        switch (type){
            case "Retiro":
                if((Integer) accountOnly.get("amountTotal") >= bean.getAmount().intValue()){
                    return transactionRepository.findByDocumentNumberAndAccountNumber(
                            bean.getDocumentNumber(), bean.getNumberAccount()
                    ).switchIfEmpty(transactionRepository.save(new TransactionEntity(
                            bean.getDocumentNumber(), bean.getNumberAccount(),
                            (Double) accountOnly.get("amountTotal") - bean.getAmount().intValue(),
                            new Date(), Arrays.asList(move))));
                }
                else{
                    return transactionRepository.findByDocumentNumberAndAccountNumber(
                            bean.getDocumentNumber(), bean.getNumberAccount()
                    ).flatMap(transactionEntity -> {
                        if(transactionEntity.getAmount() >= bean.getAmount().intValue()){
                            transactionEntity.setModifiedAt(new Date());
                            transactionEntity.setAmount(transactionEntity.getAmount() - bean.getAmount().intValue());
                            transactionEntity.getMovements().add(move);

                            return transactionRepository.save(transactionEntity);
                        }

                        return Mono.empty();
                    });
                }

            case "Deposito":
                return transactionRepository.findByDocumentNumberAndAccountNumber(
                        bean.getDocumentNumber(), bean.getNumberAccount()
                ).flatMap(transactionEntity -> {
                    transactionEntity.setModifiedAt(new Date());
                    transactionEntity.setAmount(transactionEntity.getAmount() + bean.getAmount().intValue());
                    transactionEntity.getMovements().add(move);

                    return transactionRepository.save(transactionEntity);
                }).switchIfEmpty(transactionRepository.save(new TransactionEntity(
                        bean.getDocumentNumber(), bean.getNumberAccount(),
                        (Integer) accountOnly.get("amountTotal") + bean.getAmount().doubleValue(),
                        new Date(), Arrays.asList(move))));

            default:
                return Mono.empty();
        }
    }

    public Mono<TransactionEntity> createTransactionSaving(String type, RequestTransactionBean bean,
                                                           Map<String, Object> accountOnly, Movement move){
        switch (type){
            case "Retiro":
                if((Integer) accountOnly.get("amountTotal") >= bean.getAmount().intValue()){
                    return transactionRepository.findByDocumentNumberAndAccountNumber(
                            bean.getDocumentNumber(), bean.getNumberAccount()
                    ).switchIfEmpty(transactionRepository.save(new TransactionEntity(
                            bean.getDocumentNumber(), bean.getNumberAccount(),
                            (Double) accountOnly.get("amountTotal") - bean.getAmount().intValue(),
                            new Date(), Arrays.asList(move),
                            (Integer) accountOnly.get("limitMovement") - 1)));
                }
                else{
                    return transactionRepository.findByDocumentNumberAndAccountNumber(
                            bean.getDocumentNumber(), bean.getNumberAccount()
                    ).flatMap(transactionEntity -> {
                        if(transactionEntity.getAmount() >= bean.getAmount().intValue() &&
                                transactionEntity.getLimitMovement() > 0){
                            transactionEntity.setModifiedAt(new Date());
                            transactionEntity.setAmount(transactionEntity.getAmount() - bean.getAmount().intValue());
                            transactionEntity.getMovements().add(move);
                            transactionEntity.setLimitMovement(transactionEntity.getLimitMovement() - 1);

                            return transactionRepository.save(transactionEntity);
                        }

                        return Mono.empty();
                    });
                }
            case "Deposito":
                return transactionRepository.findByDocumentNumberAndAccountNumber(
                        bean.getDocumentNumber(), bean.getNumberAccount()
                ).flatMap(transactionEntity -> {
                    if(transactionEntity.getLimitMovement() > 0){
                        transactionEntity.setModifiedAt(new Date());
                        transactionEntity.setAmount(transactionEntity.getAmount() + bean.getAmount().intValue());
                        transactionEntity.getMovements().add(move);
                        transactionEntity.setLimitMovement(transactionEntity.getLimitMovement() - 1);

                        return transactionRepository.save(transactionEntity);
                    }

                    return transactionRepository.findById(transactionEntity.get_id());

                }).switchIfEmpty(transactionRepository.save(new TransactionEntity(
                        bean.getDocumentNumber(), bean.getNumberAccount(),
                        (Integer) accountOnly.get("amountTotal") + bean.getAmount().doubleValue(),
                        new Date(), Arrays.asList(move),
                        (Integer) accountOnly.get("limitMovement") - 1))
                );

            default:
                return Mono.empty();
        }
    }

    public Mono<TransactionEntity> createTransactionFixed(String type, RequestTransactionBean bean,
                                                          Map<String, Object> accountOnly, Movement move){
        switch (type){
            case "Retiro":
                if((Integer) accountOnly.get("amountTotal") >= bean.getAmount().intValue()){
                    return transactionRepository.findByDocumentNumberAndAccountNumber(
                            bean.getDocumentNumber(), bean.getNumberAccount()
                    ).switchIfEmpty(transactionRepository.save(new TransactionEntity(
                            bean.getDocumentNumber(), bean.getNumberAccount(),
                            (Integer) accountOnly.get("amountTotal") - bean.getAmount().doubleValue(),
                            new Date(), Arrays.asList(move))));
                }

            case "Deposito":
                return transactionRepository.findByDocumentNumberAndAccountNumber(
                            bean.getDocumentNumber(), bean.getNumberAccount()
                    ).switchIfEmpty(transactionRepository.save(new TransactionEntity(
                            bean.getDocumentNumber(), bean.getNumberAccount(),
                            (Integer) accountOnly.get("amountTotal") + bean.getAmount().doubleValue(),
                            new Date(), Arrays.asList(move))));

            default:
                return Mono.empty();
        }
    }
}
