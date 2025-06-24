package money.ultradecentralised.api.service;

import com.bloxbean.cardano.client.transaction.spec.Value;
import com.bloxbean.cardano.yaci.store.utxo.storage.impl.model.AddressUtxoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import money.ultradecentralised.api.model.AddressAmount;
import money.ultradecentralised.api.model.AssetType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmountService {

    private static final Value ZERO = Value.builder().build();

    public Map<String, Value> getAddressBalances(List<AddressUtxoEntity> utxos, Predicate<AssetType> amountFilter) {
        return utxos.stream()
                .flatMap(input -> input.getAmounts()
                        .stream()
                        .map(amt -> new AddressAmount(input.getOwnerAddr(), amt.getUnit(), amt.getQuantity()))
                        .filter(addressVolume -> amountFilter.test(AssetType.fromUnit(addressVolume.unit()))))
                .collect(Collectors.groupingBy(AddressAmount::address, Collectors.collectingAndThen(Collectors.toList(),
                        addressVolumes -> addressVolumes.stream()
                                .reduce(ZERO, (value, addressVolume) -> {
                                    var unit = AssetType.fromUnit(addressVolume.unit());
                                    if (unit.isAda()) {
                                        return Value.fromCoin(addressVolume.amount());
                                    } else {
                                        return Value.from(unit.policyId(), "0x" + unit.assetName(), addressVolume.amount());
                                    }
                                }, Value::add))));
    }

    public List<AddressAmount> calculateAddressVolumes(List<AddressUtxoEntity> inputs, List<AddressUtxoEntity> outputs, Predicate<AssetType> filter) {

        var inputValues = getAddressBalances(inputs, filter);

        var outputValues = getAddressBalances(outputs, filter);

//        inputValues.entrySet()
//                .stream()
//                .map(addressValue-> {
//                    if (outputValues.containsKey(addressValue.getKey())) {
//
//                    } else {
//                        return addressValue;
//                    }
//                });

        return List.of();
    }

    public List<AddressAmount> calculateAddressAmounts(List<AddressUtxoEntity> utxos, Predicate<AddressAmount> filter) {
        return utxos.stream()
                .flatMap(input -> input.getAmounts()
                        .stream()
                        .map(amount -> new AddressAmount(input.getOwnerAddr(), amount.getUnit(), amount.getQuantity())))
                .collect(Collectors.groupingBy(addressAmount -> new ImmutablePair<>(addressAmount.address(), addressAmount.unit()), Collectors.reducing(AddressAmount::add)))
                .entrySet()
                .stream()
                .flatMap(entry -> entry.getValue().stream())
                .filter(filter)
                .toList();
    }



}
