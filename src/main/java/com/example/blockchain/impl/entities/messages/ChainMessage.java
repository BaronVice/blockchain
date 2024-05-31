package com.example.blockchain.impl.entities.messages;

import com.example.blockchain.impl.entities.Block;

import java.util.List;

/**
 * Ответ при chain запросе
 * @param message сообщение
 * @param chain цепь
 */
public record ChainMessage(
        String message,
        List<Block> chain
) {
}
