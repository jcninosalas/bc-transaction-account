package com.bootcamp.transactions.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Document(collection = "transaction")
@Getter
@Setter
@NoArgsConstructor
public class TransactionEntity
{
    private String _id;
    private String documentNumber;
    private String accountNumber;
    private Double amount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private Date createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private Date modifiedAt;

    private List<Movement> movements;

    public TransactionEntity(String documentNumber, String accountNumber, Double amount, Date createdAt, List<Movement> movements) {
        this.documentNumber = documentNumber;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.createdAt = createdAt;
        this.movements = movements;
    }
}
