package com.example.blockchain.impl;

import com.example.blockchain.impl.entities.Block;
import com.example.blockchain.impl.entities.Blockchain;
import com.example.blockchain.impl.entities.Transaction;
import com.example.blockchain.impl.entities.messages.ChainMessage;
import com.example.blockchain.impl.entities.messages.MineMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для обработки запросов
 */
@Service
@RequiredArgsConstructor
public class BlockchainService {
    /**
     * Цепочка
     */
    private final Blockchain blockchain;

    /**
     * Майнинг блока
     * @param remoteAddr адрес получателя блока
     * @return сгенерированный блок в формате MineMessage
     * @see MineMessage
     */
    public MineMessage mine(String remoteAddr) {
        Block lastBlock = blockchain.lastBlock();
        int lastProof = lastBlock.getProof();
        int proof = blockchain.proofOfWork(lastProof);

        blockchain.newTransaction("0", remoteAddr, 1);

        String lastHash = Blockchain.hash(lastBlock.toString());
        Block block = blockchain.newBlock(lastProof, lastHash);
        return new MineMessage(
                "Forged",
                block.getIndex(),
                block.getTransactions(),
                block.getProof(),
                block.getPreviousHash()
        );
    }

    /**
     * Получение цепочки
     * @return цепочку в формате ChainMessage
     * @see ChainMessage
     */
    public ChainMessage chain() {
        return new ChainMessage(
                "OK",
                blockchain.getChain()
        );
    }

    /**
     * Разрешение консенсуса
     * @return результат в формате ChainMessage. Значение message зависит от результата разрешения конфликта
     * (разрешен - "Replaced", иначе - "Authoritative")
     * @see ChainMessage
     */
    public ChainMessage consensus() {
        if (blockchain.resolveConflicts()){
            return new ChainMessage(
                    "Replaced",
                    blockchain.getChain()
            );
        } else {
            return new ChainMessage(
                    "Authoritative",
                    blockchain.getChain()
            );
        }
    }

    /**
     * Обработка транзакции
     * @param transaction параметры транзакции
     * @see Transaction
     */
    public void transaction(Transaction transaction) {
        blockchain.newTransaction(
                transaction.getSender(),
                transaction.getRecipient(),
                transaction.getAmount()
        );
    }

    /**
     * Регистрация узлов
     * @param nodes адреса узлов для регистрации
     */
    public void register(List<String> nodes) {
        for (String node : nodes){
            blockchain.registerNode(node);
        }
    }
}
