package money.ultradecentralised.api.model;

import com.bloxbean.cardano.yaci.core.model.TransactionOutput;
import com.bloxbean.cardano.yaci.store.common.domain.AddressUtxo;
import com.bloxbean.cardano.yaci.store.utxo.storage.impl.model.AddressUtxoEntity;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record AddressAmount(String address, String unit, BigInteger amount) {

    public static List<AddressAmount> fromAddressUtxoEntity(List<AddressUtxoEntity> utxos, Predicate<AddressAmount> filter) {
        var addressAmounts = utxos.stream()
                .flatMap(utxoEntity -> utxoEntity.getAmounts().stream().map(amt -> new AddressAmount(utxoEntity.getOwnerAddr(), amt.getUnit(), amt.getQuantity())))
                .toList();
        return reduce(addressAmounts, filter);
    }


    public static List<AddressAmount> fromAddressUtxo(List<AddressUtxo> utxos, Predicate<AddressAmount> filter) {
        var addressAmounts = utxos.stream()
                .flatMap(utxoEntity -> utxoEntity.getAmounts().stream().map(amt -> new AddressAmount(utxoEntity.getOwnerAddr(), amt.getUnit(), amt.getQuantity())))
                .toList();
        return reduce(addressAmounts, filter);
    }

    public static List<AddressAmount> fromTransactionOutput(List<TransactionOutput> utxos, Predicate<AddressAmount> filter) {
        var addressAmounts = utxos.stream()
                .flatMap(utxoEntity -> utxoEntity.getAmounts().stream().map(amt -> new AddressAmount(utxoEntity.getAddress(), amt.getUnit(), amt.getQuantity())))
                .toList();
        return reduce(addressAmounts, filter);
    }

    private static List<AddressAmount> reduce(List<AddressAmount> addressAmounts, Predicate<AddressAmount> filter) {
        return addressAmounts
                .stream()
                .collect(Collectors.groupingBy(addressAmount -> new ImmutablePair<>(addressAmount.address(), addressAmount.unit()), Collectors.reducing(AddressAmount::add)))
                .values()
                .stream()
                .flatMap(Optional::stream)
                .filter(filter)
                .toList();
    }


    public AddressAmount add(AddressAmount other) {
        if (this.address.equals(other.address) && this.unit.equals(other.unit)) {
            return new AddressAmount(this.address, this.unit, this.amount.add(other.amount));
        } else {
            throw new IllegalArgumentException("Cannot sum AddressAmounts with different address or unit");
        }
    }

    public static List<AddressAmount> getVolume(List<AddressAmount> inputAmounts, List<AddressAmount> outputAmounts) {
        return Stream.concat(inputAmounts.stream()
                                .map(inputAmount -> outputAmounts.stream()
                                        .filter(outputAmount -> AssetType.fromUnit(outputAmount.unit()).equals(AssetType.fromUnit(inputAmount.unit())))
                                        .findFirst()
                                        .map(outputAmount -> new AddressAmount(inputAmount.address(), inputAmount.unit(), inputAmount.amount().subtract(outputAmount.amount)))
                                        .orElse(inputAmount)),
                        outputAmounts.stream().filter(outputAmount -> inputAmounts.stream()
                                .noneMatch(inputAmount -> AssetType.fromUnit(inputAmount.unit()).equals(AssetType.fromUnit(outputAmount.unit())))))
                .toList();
    }

}
