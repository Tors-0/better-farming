package io.github.Tors_0.dotwarden.common.networking;

import io.github.Tors_0.dotwarden.common.DOTWarden;
import io.github.Tors_0.dotwarden.common.extensions.PlayerExtensions;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

public class DOTWNetworking {
    public static final Identifier POWERLEVEL_PACKET_ID = new Identifier(DOTWarden.ID,"sync_power");
    public static final Identifier SYNC_SACRIFICE_ID = new Identifier(DOTWarden.ID,"sync_sacrifice");
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(POWERLEVEL_PACKET_ID, (client, handler, buf, responseSender) -> {
            int x = buf.readInt();
            int y = buf.readInt();
            client.execute(() -> {
                assert client.player != null;
                ((PlayerExtensions)client.player).dotwarden$setPowerLevel(x);
                ((PlayerExtensions)client.player).dotwarden$setPower(y);
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
