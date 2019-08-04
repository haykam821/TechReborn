/*
 * This file is part of TechReborn, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018 TechReborn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package techreborn.blockentity.machine.tier3;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import reborncore.client.containerBuilder.IContainerProvider;
import reborncore.client.containerBuilder.builder.BuiltContainer;
import reborncore.client.containerBuilder.builder.ContainerBuilder;
import reborncore.common.registration.RebornRegister;
import reborncore.common.registration.config.ConfigRegistry;
import techreborn.TechReborn;
import techreborn.init.TRBlockEntities;
import techreborn.blockentity.TechStorageBaseBlockEntity;

@RebornRegister(TechReborn.MOD_ID)
public class QuantumChestBlockEntity extends TechStorageBaseBlockEntity implements IContainerProvider {

	@ConfigRegistry(config = "machines", category = "quantum_chest", key = "QuantumChestMaxStorage", comment = "Maximum amount of items a Quantum Chest can store")
	public static int maxStorage = Integer.MAX_VALUE;

	public QuantumChestBlockEntity() {
		this(TRBlockEntities.QUANTUM_CHEST);
	}

	public QuantumChestBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType, "QuantumChestBlockEntity", maxStorage);
	}

	@Override
	public BuiltContainer createContainer(int syncID, final PlayerEntity player) {
		return new ContainerBuilder("quantumchest").player(player.inventory).inventory().hotbar().addInventory()
			.blockEntity(this).slot(0, 80, 24).outputSlot(1, 80, 64).addInventory().create(this, syncID);
	}

}