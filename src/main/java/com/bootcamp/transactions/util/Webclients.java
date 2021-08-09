package com.bootcamp.transactions.util;

import com.bootcamp.transactions.bean.ResponseAccountClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class Webclients
{
    String endpointCustomerP = "http://localhost:8090/";
    String pathSendAccount = "account/p-customer/";

    private final WebClient client;

    public Webclients(WebClient.Builder builder) {
        this.client = builder.baseUrl(endpointCustomerP).build();
    }

    public Mono<ResponseAccountClient> getAccountClient(String accountNumber, String documentNumber){
        return this.client.get().uri(pathSendAccount.concat(accountNumber).concat("/").concat(documentNumber))
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,
                        clientResponse -> Mono.empty())
                .bodyToMono(ResponseAccountClient.class);
    }
}
