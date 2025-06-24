package money.ultradecentralised.api.service;

import com.bloxbean.cardano.yaci.store.events.RollbackEvent;
import com.bloxbean.cardano.yaci.store.events.TransactionEvent;
import com.bloxbean.cardano.yaci.store.utxo.storage.impl.model.UtxoId;
import com.bloxbean.cardano.yaci.store.utxo.storage.impl.repository.UtxoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import money.ultradecentralised.api.entity.TokenVolume;
import money.ultradecentralised.api.model.AddressAmount;
import money.ultradecentralised.api.repository.TokenVolumeRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenVolumeEventListener {

    private static final List<String> SUPPORTED_ASSETS = List.of(
            "c48cbb3d5e57ed56e276bc45f99ab39abe94e6cd7ac39fb402da47ad0014df105553444d"
    );

    private final UtxoRepository utxoRepository;

    private final TokenVolumeRepository tokenVolumeRepository;


    @EventListener
    @Transactional
    public void handleTransactionEvent(TransactionEvent transactionEvent) {

        var slot = transactionEvent.getMetadata().getSlot();

        transactionEvent.getTransactions()
                .forEach(transaction -> {

                    var txHash = transaction.getTxHash();

                    var inputUtxos = transaction.getBody()
                            .getInputs()
                            .stream().flatMap(txInput -> utxoRepository.findById(UtxoId.builder()
                                            .txHash(txInput.getTransactionId())
                                            .outputIndex(txInput.getIndex())
                                            .build())
                                    .stream())
                            .toList();

                    var inputAddressAmounts = AddressAmount.fromAddressUtxoEntity(inputUtxos, addressAmount -> SUPPORTED_ASSETS.contains(addressAmount.unit()));

                    var outputBalances = AddressAmount.fromTransactionOutput(transaction.getBody().getOutputs(), addressAmount -> SUPPORTED_ASSETS.contains(addressAmount.unit()));

                    var volume = AddressAmount.getVolume(inputAddressAmounts, outputBalances);

                    volume.forEach(addressAmount -> tokenVolumeRepository.save(TokenVolume.builder()
                            .txHash(txHash)
                            .address(addressAmount.address())
                            .unit(addressAmount.unit())
                            .volume(addressAmount.amount().longValue())
                            .slot(slot)
                            .build()));


                });
    }

    @EventListener
    @Transactional
    public void handleRollback(RollbackEvent rollbackEvent) {
        if (rollbackEvent.getRollbackTo() != null && rollbackEvent.getRollbackTo().getSlot() >= 0) {
            log.info("processing valid rollback: {}", rollbackEvent);
            tokenVolumeRepository.deleteBySlotGreaterThan(rollbackEvent.getRollbackTo().getSlot());
        }
    }

}
