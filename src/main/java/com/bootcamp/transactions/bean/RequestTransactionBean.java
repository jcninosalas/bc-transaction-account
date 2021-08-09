package com.bootcamp.transactions.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class RequestTransactionBean
{
    private String numberAccount;
    private String documentNumber;
    private BigDecimal amount;
}
