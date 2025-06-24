package money.ultradecentralised.api.repository;

import money.ultradecentralised.api.entity.BlockLeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BlockLeaderRepository extends JpaRepository<BlockLeader, String> {

    @Query("""
            SELECT COUNT(DISTINCT bl.vrfKeyHash)
            FROM BlockLeader bl
            WHERE bl.slot BETWEEN :slotFrom AND :slotTo
            """)
    Integer countDistinctVrfKeyHashBySlotBetween(Long slotFrom, Long slotTo);

    Long deleteBySlotGreaterThan(Long slot);

}
