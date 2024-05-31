package com.example.blockchain.impl.entities.messages;

import com.example.blockchain.impl.entities.Transaction;

import java.util.List;

/**
 * Ответ при mine запросе
 * @param message сообщение
 * @param index индекс полученного блока
 * @param transactions транзакции полученного блока
 * @param proof затраченные ресурсы на получение блока
 * @param previousHash хэш предыдущего блока
 */
public record MineMessage(
        String message,
        int index,
        List<Transaction> transactions,
        int proof,
        String previousHash
) {
}
