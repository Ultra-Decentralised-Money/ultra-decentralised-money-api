package money.ultradecentralised.api.repository;

import money.ultradecentralised.api.entity.TokenVolume;
import money.ultradecentralised.api.entity.TokenVolumeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenVolumeRepository extends JpaRepository<TokenVolume, TokenVolumeId> {

    Long deleteBySlotGreaterThan(Long slot);
}
