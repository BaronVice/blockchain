package com.example.blockchain.impl;

import com.example.blockchain.impl.entities.Transaction;
import com.example.blockchain.impl.entities.messages.ChainMessage;
import com.example.blockchain.impl.entities.messages.MineMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/blockchain/api")
public class BlockchainController {
    private final BlockchainService blockchainService;

    /**
     * Запрос на майнинг
     * @param request параметры запроса (из которого берется адрес для отправки блока)
     * @return сообщение в формате MineMessage
     * @see MineMessage
     */
    @GetMapping("/mine")
    public ResponseEntity<?> mine(
            HttpServletRequest request
    ){
        return ResponseEntity.ok(blockchainService.mine(request.getRemoteAddr()));
    }

    /**
     * Запрос на получение цепочки
     * @return сообщение в формате ChainMessage
     * @see ChainMessage
     */
    @GetMapping("/chain")
    public ResponseEntity<?> chain(){
        return ResponseEntity.ok(blockchainService.chain());
    }

    /**
     * Запрос на разрешение консенсуса
     * @return сообщение в формате ChainMessage
     * @see ChainMessage
     */
    @GetMapping("/consensus")
    public ResponseEntity<?> consensus(){
        return ResponseEntity.ok(blockchainService.consensus());
    }

    /**
     * Запрос на обработку транзакции
     * @param transaction параметры транзакции
     */
    @PostMapping("/transaction")
    public ResponseEntity<?> transaction(
            @RequestBody Transaction transaction
    ){
        blockchainService.transaction(transaction);
        return ResponseEntity.ok().build();
    }

    /**
     * Запрос на регистрацию узлов
     * @param nodes узлы для регистрации
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody List<String> nodes
    ){
        blockchainService.register(nodes);
        return ResponseEntity.ok().build();
    }
}
