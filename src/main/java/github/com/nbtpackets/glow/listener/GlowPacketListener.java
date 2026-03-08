package github.com.nbtpackets.glow.listener;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import org.bukkit.entity.Player;
import github.com.nbtpackets.glow.manager.GlowManager;
import github.com.nbtpackets.glow.manager.GlowUpdater;
import github.com.nbtpackets.manager.ClanManager;
import github.com.nbtpackets.player.PlayerFind;

public class GlowPacketListener implements PacketListener {

    private final ClanManager clanManager;

    public GlowPacketListener(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.ENTITY_EQUIPMENT) return;
        WrapperPlayServerEntityEquipment wrapper = new WrapperPlayServerEntityEquipment(event);
        Player receiver = PlayerFind.uuid(event.getUser().getUUID()).orElse(null);
        if (receiver == null) return;
        Player sender = findPlayerByEntityId(wrapper.getEntityId());
        if (sender == null || sender.equals(receiver)) return;
        if (!GlowManager.isEnabled(sender)) return;
        if (!clanManager.isSameClan(sender.getUniqueId(), receiver.getUniqueId())) return;

        clanManager.getPlayerClan(receiver.getUniqueId())
                .map(clan -> clan.effectiveColorFor(sender.getUniqueId()))
                .ifPresent(color -> {
                    wrapper.setEquipment(GlowUpdater.buildGlowEquipment(sender, color));
                    event.markForReEncode(true);
                });
    }

    private Player findPlayerByEntityId(int entityId) {
        return PlayerFind.all()
                .matching(p -> p.getEntityId() == entityId)
                .first()
                .orElse(null);
    }
}