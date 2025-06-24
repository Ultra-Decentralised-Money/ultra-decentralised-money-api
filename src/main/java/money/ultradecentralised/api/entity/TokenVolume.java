package money.ultradecentralised.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "token_volume")
@IdClass(TokenVolumeId.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenVolume {

    @Id
    private String txHash;

    @Id
    private String address;

    @Id
    private String unit;

    private Long volume;

    private Long slot;

}
