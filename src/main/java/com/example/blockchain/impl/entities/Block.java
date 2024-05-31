package com.example.blockchain.impl.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.security.MessageDigest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Класс блока
 */
@Getter
@Setter
@ToString
public class Block {
    /**
     * Последовательность id блоков
     */
    private static final AtomicInteger BLOCK_INDEX = new AtomicInteger(0);

    public Block(
            List<Transaction> transactions,
            int proof,
            String previousHash
    ){
        index = BLOCK_INDEX.getAndIncrement();
        timestamp = Instant.now();
        this.transactions = new ArrayList<>(transactions);
        this.proof = proof;
        this.previousHash = previousHash;
    }

    /**
     * Индекс блока
     */
    private int index;
    /**
     * Время создания блока
     */
    private final Instant timestamp;
    /**
     * Транзакции блока
     */
    private final List<Transaction> transactions;
    /**
     * Затрачено на блок
     */
    private int proof;
    /**
     * Хэш предыдущего блока
     */
    private final String previousHash;
}
