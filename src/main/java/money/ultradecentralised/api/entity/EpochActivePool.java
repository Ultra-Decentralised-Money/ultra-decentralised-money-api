package money.ultradecentralised.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "epoch_active_pool")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpochActivePool {

    @Id
    private Integer epochNumber;

    private Integer activePoolCount;

    private Long updatedAtSlot;

}
