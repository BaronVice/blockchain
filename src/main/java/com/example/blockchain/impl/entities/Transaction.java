package com.example.blockchain.impl.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Транзакция
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class Transaction {
    /**
     * Отправитель
     */
    private String sender;
    /**
     * Получатель
     */
    private String recipient;
    /**
     * Количество для отправки
     */
    private int amount;
}
