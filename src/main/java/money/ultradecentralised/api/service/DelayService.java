package money.ultradecentralised.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardanofoundation.conversions.CardanoConverters;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class DelayService {

    private final CardanoConverters cardanoConverters;

    public boolean isBefore(Long slotFrom, Long slotTo, Duration duration) {
        var from = cardanoConverters.slot().slotToTime(slotFrom);
        var to = cardanoConverters.slot().slotToTime(slotTo);
        return from.plus(duration).isBefore(to);
    }

    public boolean isAfter(Long slotFrom, Long slotTo, Duration duration) {
        var from = cardanoConverters.slot().slotToTime(slotFrom);
        var to = cardanoConverters.slot().slotToTime(slotTo);
        return from.plus(duration).isAfter(to);
    }

}
