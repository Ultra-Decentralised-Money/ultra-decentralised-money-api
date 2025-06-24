package money.ultradecentralised.api.service;

import com.bloxbean.cardano.yaci.store.events.BlockEvent;
import com.bloxbean.cardano.yaci.store.events.RollbackEvent;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import money.ultradecentralised.api.entity.BlockLeader;
import money.ultradecentralised.api.entity.EpochActivePool;
import money.ultradecentralised.api.repository.BlockLeaderRepository;
import money.ultradecentralised.api.repository.EpochActivePoolRepository;
import org.cardanofoundation.conversions.CardanoConverters;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
@Slf4j
public class BlockLeaderEventListener {

    private final BlockLeaderRepository activePoolEpochRepository;

    private final EpochActivePoolRepository epochActivePoolRepository;

    private final DelayService delayService;

    private final CardanoConverters cardanoConverters;

    private final AtomicLong latestUpdatedAtSlot = new AtomicLong(Long.MIN_VALUE);


    @PostConstruct
    public void init() {

        var latestUpdatedAtSlotOpt = epochActivePoolRepository.findTopByOrderByUpdatedAtSlotDesc()
                .map(EpochActivePool::getUpdatedAtSlot);
        log.info("INIT - Found latest updated at slot: {}", latestUpdatedAtSlotOpt);

        latestUpdatedAtSlotOpt.ifPresent(this.latestUpdatedAtSlot::set);

    }

    @EventListener
    @Transactional
    public void handleTransactionEvent(BlockEvent event) {

        var currentEpoch = event.getMetadata().getEpochNumber();
        var blockHash = event.getMetadata().getBlockHash();
        var currentSlot = event.getMetadata().getSlot();
        var slotLeader = event.getMetadata().getSlotLeader();

        activePoolEpochRepository.save(BlockLeader.builder()
                .blockHash(blockHash)
                .vrfKeyHash(slotLeader)
                .slot(currentSlot)
                .build());

        if (delayService.isBefore(latestUpdatedAtSlot.get(), currentSlot, Duration.ofDays(1))) {
            log.info("Processing active pool for epoch {}", event.getMetadata().getEpochNumber());

            List.of(2, 1, 0)
                    .forEach(minusEpoch -> {
                        var epochToProcess = currentEpoch - minusEpoch;
                        var epochSlotFrom = cardanoConverters.epoch().beginningOfEpochToAbsoluteSlot(epochToProcess);
                        var epochSlotTo = cardanoConverters.epoch().endingOfEpochToAbsoluteSlot(epochToProcess);
                        log.info("from {}, to {}", epochSlotFrom, epochSlotTo);
                        var activePoolCount = activePoolEpochRepository.countDistinctVrfKeyHashBySlotBetween(epochSlotFrom, epochSlotTo);
                        epochActivePoolRepository.save(EpochActivePool.builder()
                                .epochNumber(epochToProcess)
                                .updatedAtSlot(currentSlot)
                                .activePoolCount(activePoolCount)
                                .build());
                    });

            latestUpdatedAtSlot.set(currentSlot);

        }

    }

    @EventListener
    @Transactional
    public void handleRollback(RollbackEvent rollbackEvent) {
        if (rollbackEvent.getRollbackTo() != null && rollbackEvent.getRollbackTo().getSlot() >= 0) {
            log.info("processing valid rollback: {}", rollbackEvent);
            activePoolEpochRepository.deleteBySlotGreaterThan(rollbackEvent.getRollbackTo().getSlot());
        }
    }

}
