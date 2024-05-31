package com.example.blockchain.impl.entities;

import java.util.List;

/**
 * Обертка для получения цепочки с узлов при вызове консенсуса
 * @param chain полученная цепь
 */
public record ValidChainResponse (
        List<Block> chain
) {
}
