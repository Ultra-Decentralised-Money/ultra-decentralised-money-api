package money.ultradecentralised.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "block_leader")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockLeader {

    @Id
    private String blockHash;

    private String vrfKeyHash;

    private Long slot;

}
