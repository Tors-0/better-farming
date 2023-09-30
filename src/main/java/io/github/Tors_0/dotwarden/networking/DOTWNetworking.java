package io.github.Tors_0.dotwarden.networking;

import net.minecraft.util.Identifier;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;
import io.github.Tors_0.dotwarden.DOTWarden;
import io.github.Tors_0.dotwarden.extensions.PlayerExtensions;

public class DOTWNetworking {
    public static final Identifier POWERLEVEL_PACKET_ID = new Identifier(DOTWarden.ID,"sync_power");
    public static final Identifier SYNC_SACRIFICE_ID = new Identifier(DOTWarden.ID,"sync_sacrifice");
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(POWERLEVEL_PACKET_ID, (client, handler, buf, responseSender) -> {
            int x = buf.readInt();
            client.execute(() -> {
                assert client.player != null;
                ((PlayerExtensions)client.player).dotwarden$setPowerLevel(x);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(SYNC_SACRIFICE_ID, (client, handler, buf, responseSender) -> {
            boolean b = buf.readBoolean();
            client.execute(() -> {
                assert client.player != null;
                ((PlayerExtensions)client.player).dotwarden$setSacrifice(b);
            });
        });
    }
}
