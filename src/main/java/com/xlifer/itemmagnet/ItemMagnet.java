package com.xlifer.itemmagnet;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;

public class ItemMagnet implements ModInitializer {
	/** Items closer than this (in blocks) get pulled. */
	public static final double RADIUS = 16.0;
	/** Maximum pull speed in blocks per tick. */
	private static final double MAX_SPEED = 0.6;

	/** Players who turned the magnet off with /magnet (resets on server restart). */
	private static final Set<UUID> DISABLED = new HashSet<>();

	@Override
	public void onInitialize() {
		ServerTickEvents.END_SERVER_TICK.register(ItemMagnet::tick);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(Commands.literal("magnet").executes(ctx -> {
					ServerPlayer player = ctx.getSource().getPlayerOrException();
					// remove() returns true if it was off, so now it's on; otherwise turn it off.
					boolean nowOn = DISABLED.remove(player.getUUID());
					if (!nowOn) {
						DISABLED.add(player.getUUID());
					}
					player.sendOverlayMessage(Component.literal("Item magnet " + (nowOn ? "ON" : "OFF")));
					return 1;
				})));
	}

	private static void tick(MinecraftServer server) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (player.isSpectator() || !player.isAlive() || DISABLED.contains(player.getUUID())) {
				continue;
			}

			Vec3 target = player.position().add(0, 0.5, 0);
			for (ItemEntity item : player.level().getEntitiesOfClass(ItemEntity.class,
					player.getBoundingBox().inflate(RADIUS),
					e -> e.isAlive() && !e.hasPickUpDelay())) {
				Vec3 toPlayer = target.subtract(item.position());
				double distance = toPlayer.length();
				if (distance > RADIUS || distance < 0.3) {
					continue;
				}
				// Never overshoot: close items slow down so they land right on the player.
				item.setDeltaMovement(toPlayer.scale(Math.min(MAX_SPEED, distance) / distance));
				item.syncVelocity = true; // sync the new velocity to clients
			}
		}
	}
}
