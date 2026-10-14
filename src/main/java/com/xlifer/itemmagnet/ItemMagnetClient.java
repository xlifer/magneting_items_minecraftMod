package com.xlifer.itemmagnet;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

public class ItemMagnetClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		KeyMapping toggleKey = KeyMappingHelper.registerKeyMapping(
				new KeyMapping("key.itemmagnet.toggle", InputConstants.KEY_F8, KeyMapping.Category.MISC));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toggleKey.consumeClick()) {
				if (client.player != null) {
					// The server side owns the on/off state, so the key just runs /magnet.
					client.player.connection.sendCommand("magnet");
				}
			}
		});
	}
}
