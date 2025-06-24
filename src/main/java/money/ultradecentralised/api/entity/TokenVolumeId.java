package money.ultradecentralised.api.entity;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class TokenVolumeId {

    private String txHash;

    private String address;

    private String unit;

}