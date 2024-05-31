package com.example.blockchain.impl.entities;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

/**
 * Цепочка
 */
@Getter
@Setter
@Component
public class Blockchain {
    /**
     * Цепочка
     */
    private List<Block> chain;
    /**
     * Накопленные транзакции
     */
    private final List<Transaction> currentTransaction;
    /**
     * Зарегистрированные узлы
     */
    private final Set<String> nodes;

    /**
     * Инициализация цепочки. Предполагает создание генезис-блока
     */
    public Blockchain(){
        chain = new ArrayList<>();
        currentTransaction = new ArrayList<>();
        nodes = new HashSet<>();

        chain.add(new Block(currentTransaction, 100, "0"));
    }

    /**
     * Функция хэширования
     * @param s строка для хэширования
     * @return результат хэширования через алгоритм SHA-256
     */
    @SneakyThrows
    public static String hash(String s){
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(
                s.getBytes(StandardCharsets.UTF_8));

        return new String(Hex.encode(hash));
    }

    /**
     * Подтверждение доказательства
     * @param lastProof предыдущее доказательство
     * @param proof текущее доказательство
     * @return true - правильно, false - неправильно
     */
    private static boolean validProof(int lastProof, int proof){
        String guess = new String(String.format("%d%d", lastProof, proof).getBytes(StandardCharsets.UTF_8));
        String guessHash = hash(guess);

        for (int i = 0; i < 4; i++){
            if (guessHash.toCharArray()[i] != '0') return false;
        }

        return true;
    }

    /**
     * Получение последнего блока цепочки
     * @return последний блок цепочки
     */
    public Block lastBlock(){
        return chain.get(chain.size()-1);
    }

    /**
     * Проверка на корректность цепочки. Для каждого блока кроме первого произойдет проверка через validProof()
     * @param getChain цепочка
     * @return true - корректна, иначе false
     */
    private boolean validChain(ValidChainResponse getChain) {
        List<Block> toCheck = getChain.chain();
        Block last = toCheck.get(0);

        int i = 1;
        while (i < toCheck.size()){
            Block block = toCheck.get(i);
            if (!Objects.equals(last.getPreviousHash(), hash(last.toString()))) {
                return false;
            } if (!validProof(last.getProof(), block.getProof())){
                return false;
            }

            last = block;
            i++;
        }

        return true;
    }

    /**
     * Регистрация узлов
     * @param address адресы узлов
     */
    public void registerNode(String address){
        nodes.add(address);
    }

    /**
     * Создание нового блока. При создании очищается список транзакций, происходит добавление блока в цепочку
     * @param proof доказательство вычислений
     * @param previousHash хэш предыдущего блока
     * @return сгенерированный блок
     */
    public Block newBlock(int proof, String previousHash){
        Block block = new Block(
                currentTransaction,
                proof,
                previousHash
        );

        currentTransaction.clear();
        chain.add(block);

        return block;
    }

    /**
     * Создание транзакции
     * @param sender отправитель
     * @param recipient получатель
     * @param amount количество для отправки
     */
    public void newTransaction(
            String sender,
            String recipient,
            int amount
    ){
        currentTransaction.add(
                new Transaction(sender, recipient, amount)
        );
    }

    /**
     * Реализация алгоритма Proof-of-Work
     * @param lastProof доказательство предыдущего блока
     * @return доказательство сгенерированного блока
     */
    public int proofOfWork(int lastProof){
        int proof = 0;
        while (!validProof(lastProof, proof)){
            proof += 1;
        }

        return proof;
    }

    /**
     * Функция разрешения конфликта. Используется принцип замены на самую длинную цепь. В случае если такой не найдется
     * останется собственная.
     * @return true при разрешении конфликта, иначе false
     */
    public boolean resolveConflicts(){
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OkHttpClient client = new OkHttpClient();

        for (String node : nodes){
            Request request = new Request.Builder()
                    .url("http://" + node + "api/chain")
                    .build(); // defaults to GET

            try {
                Response response = client.newCall(request).execute();
                ValidChainResponse getChain =  mapper.readValue(response.body().byteStream(), ValidChainResponse.class);
                if (getChain.chain().size() > chain.size() && validChain(getChain)){
                    chain = getChain.chain();
                    return true;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return false;
    }
}
