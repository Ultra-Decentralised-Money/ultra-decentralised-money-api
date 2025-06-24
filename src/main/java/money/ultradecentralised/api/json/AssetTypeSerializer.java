package money.ultradecentralised.api.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import money.ultradecentralised.api.model.AssetType;

import java.io.IOException;

public class AssetTypeSerializer extends StdSerializer<AssetType> {

    public AssetTypeSerializer() {
        this(null);
    }

    public AssetTypeSerializer(Class<AssetType> t) {
        super(t);
    }

    @Override
    public void serialize(AssetType assetType, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (assetType.isAda()) {
            jsonGenerator.writeString("ADA");
        } else {
            jsonGenerator.writeString(assetType.unsafeHumanAssetName());
        }
    }

}
