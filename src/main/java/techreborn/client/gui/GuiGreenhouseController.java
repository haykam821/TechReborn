/*
 * This file is part of TechReborn, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2020 TechReborn
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

package techreborn.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import reborncore.client.containerBuilder.builder.BuiltContainer;
import reborncore.client.gui.builder.GuiBase;
import reborncore.client.gui.builder.widget.GuiButtonExtended;
import reborncore.client.multiblock.Multiblock;
import reborncore.common.util.StringUtils;
import techreborn.blockentity.machine.tier1.GreenhouseControllerBlockEntity;
import techreborn.init.TRContent;

import java.util.Arrays;
import java.util.List;

public class GuiGreenhouseController extends GuiBase<BuiltContainer> {
	
	GreenhouseControllerBlockEntity blockEntity;
	
	public GuiGreenhouseController(int syncID, final PlayerEntity player, final GreenhouseControllerBlockEntity blockEntity) {
		super(player, blockEntity, blockEntity.createContainer(syncID, player));
		this.blockEntity = blockEntity;
	}
	
	@Override
	protected void drawBackground(final float f, final int mouseX, final int mouseY) {
		super.drawBackground(f, mouseX, mouseY);
		final GuiBase.Layer layer = GuiBase.Layer.BACKGROUND;
		
		drawSlot(8, 72, layer);
		
		int gridYPos = 22;
		drawSlot(30, gridYPos, layer);
		drawSlot(48, gridYPos, layer);
		drawSlot(30, gridYPos + 18, layer);
		drawSlot(48, gridYPos + 18, layer);
		drawSlot(30, gridYPos + 36, layer);
		drawSlot(48, gridYPos + 36, layer);
		
		if (!blockEntity.getMultiBlock()) {
			getMinecraft().getTextureManager().bindTexture(new Identifier("techreborn", "textures/item/part/digital_display.png"));
			blit(x + 68, y + 22, 0, 0, 16, 16, 16, 16);
			if (isPointInRect(68, 22, 16, 16, mouseX, mouseY)) {
				List<String> list = Arrays.asList(StringUtils.t("techreborn.tooltip.greenhouse.upgrade_available").split("\\r?\\n"));
				RenderSystem.pushMatrix();
				renderTooltip(list, mouseX, mouseY);
				RenderSystem.popMatrix();
			}
		}
		
	}
	
	@Override
	protected void drawForeground(final int mouseX, final int mouseY) {
		super.drawForeground(mouseX, mouseY);
		final GuiBase.Layer layer = GuiBase.Layer.FOREGROUND;
		
		addHologramButton(90, 24, 212, layer).clickHandler(this::onClick);
		builder.drawHologramButton(this, 90, 24, mouseX, mouseY, layer);
		
		if (!blockEntity.getMultiBlock()) {
			if (isPointInRect(68, 22, 16, 16, mouseX, mouseY)) {
				List<String> list = Arrays.asList(StringUtils.t("techreborn.tooltip.greenhouse.upgrade_available").split("\\r?\\n"));
				RenderSystem.pushMatrix();
				renderTooltip(list, mouseX - getGuiLeft(), mouseY - getGuiTop());
				RenderSystem.popMatrix();
			}
		}
		
		builder.drawMultiEnergyBar(this, 9, 19, (int) blockEntity.getEnergy(), (int) blockEntity.getMaxPower(), mouseX, mouseY, 0, layer);
	}
	
	public void onClick(GuiButtonExtended button, Double x, Double y) {
		if (hideGuiElements()) return;
		if (blockEntity.renderMultiblock == null) {
			final Multiblock multiblock = new Multiblock();
			BlockState lamp = TRContent.Machine.LAMP_INCANDESCENT.block.getDefaultState().with(Properties.FACING, Direction.DOWN);
			BlockState crop = Blocks.CACTUS.getDefaultState();

			this.addComponent(-3, 3, -3, lamp, multiblock);
			this.addComponent(-3, 3, 0, lamp, multiblock);
			this.addComponent(-3, 3, 3, lamp, multiblock);
			this.addComponent(0, 3, -3, lamp, multiblock);
			this.addComponent(0, 3, 0, lamp, multiblock);
			this.addComponent(0, 3, 3, lamp, multiblock);
			this.addComponent(3, 3, -3, lamp, multiblock);
			this.addComponent(3, 3, 0, lamp, multiblock);
			this.addComponent(3, 3, 3, lamp, multiblock);

			for (int i = -4; i <= 4; i++) {
				for (int j = -4; j <= 4; j++) {
					this.addComponent(i, 0, j, crop, multiblock);
				}
			}

			blockEntity.renderMultiblock = multiblock;
		} else {
			blockEntity.renderMultiblock = null;
		}
	}
	
	public void addComponent(final int x, final int y, final int z, final BlockState blockState, final Multiblock multiblock) {
		multiblock.addComponent(new BlockPos(
						x - Direction.byId(this.blockEntity.getFacingInt()).getOffsetX() * 5,
						y,
						z - Direction.byId(this.blockEntity.getFacingInt()).getOffsetZ() * 5),
				blockState
		);
	}
	
}