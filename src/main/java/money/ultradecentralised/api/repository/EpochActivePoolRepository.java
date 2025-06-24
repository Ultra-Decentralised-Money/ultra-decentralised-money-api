package money.ultradecentralised.api.repository;

import money.ultradecentralised.api.entity.EpochActivePool;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EpochActivePoolRepository extends JpaRepository<EpochActivePool, Integer> {

    Optional<EpochActivePool> findTopByOrderByUpdatedAtSlotDesc();

}
