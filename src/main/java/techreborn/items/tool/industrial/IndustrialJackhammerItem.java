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

package techreborn.items.tool.industrial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import reborncore.common.util.ChatUtils;
import reborncore.common.util.ItemUtils;
import reborncore.common.util.StringUtils;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyTier;
import techreborn.config.TechRebornConfig;
import techreborn.items.tool.JackhammerItem;
import techreborn.utils.MessageIDs;
import techreborn.utils.ToolsUtil;

import javax.annotation.Nullable;
import java.util.List;

public class IndustrialJackhammerItem extends JackhammerItem {

	public IndustrialJackhammerItem() {
		super(ToolMaterials.DIAMOND, TechRebornConfig.industrialJackhammerCharge, EnergyTier.INSANE, TechRebornConfig.industrialJackhammerCost);
	}

	// Cycle Inactive, Active 3*3 and Active 5*5
	private void switchAOE(ItemStack stack, int cost, boolean isClient, int messageId) {
		ItemUtils.checkActive(stack, cost, isClient, messageId);
		if (!ItemUtils.isActive(stack)) {
			ItemUtils.switchActive(stack, cost, isClient, messageId);
			stack.getOrCreateTag().putBoolean("AOE5", false);
			if (isClient) {
				ChatUtils.sendNoSpamMessages(messageId, new LiteralText(Formatting.GRAY + StringUtils.t("techreborn.message.setTo") + " " + Formatting.GOLD + "3*3"));
			}
		} else {
			if (isAOE5(stack)) {
				ItemUtils.switchActive(stack, cost, isClient, messageId);
				stack.getOrCreateTag().putBoolean("AOE5", false);
			} else {
				stack.getOrCreateTag().putBoolean("AOE5", true);
				if (isClient) {
					ChatUtils.sendNoSpamMessages(messageId, new LiteralText(Formatting.GRAY + StringUtils.t("techreborn.message.setTo") + " " + Formatting.GOLD + "5*5"));
				}
			}
		}
	}

	private boolean shouldBreak(World worldIn, BlockPos originalPos, BlockPos pos) {
		if (originalPos.equals(pos)) {
			return false;
		}
		BlockState blockState = worldIn.getBlockState(pos);
		if (blockState.getMaterial() == Material.AIR) {
			return false;
		}
		if (blockState.getMaterial().isLiquid()) {
			return false;
		}
		if (blockState.getBlock() instanceof OreBlock) {
			return false;
		}
		if (blockState.getBlock() instanceof RedstoneOreBlock) {
			return false;
		}
		return (Items.IRON_PICKAXE.isEffectiveOn(blockState));
	}

	private boolean isAOE5(ItemStack stack) {
		return !stack.isEmpty() && stack.getTag() != null && stack.getTag().getBoolean("AOE5");
	}

	// JackhammerItem
	@Override
	public boolean postMine(ItemStack stack, World worldIn, BlockState stateIn, BlockPos pos, LivingEntity entityLiving) {
		if (!ItemUtils.isActive(stack)) {
			return super.postMine(stack, worldIn, stateIn, pos, entityLiving);
		}
		int radius = isAOE5(stack) ? 2 : 1;
		for (BlockPos additionalPos : ToolsUtil.getAOEMiningBlocks(worldIn, pos, entityLiving, radius)) {
			if (shouldBreak(worldIn, pos, additionalPos)) {
				ToolsUtil.breakBlock(stack, worldIn, additionalPos, entityLiving, cost);
			}
		}

		return super.postMine(stack, worldIn, stateIn, pos, entityLiving);
	}

	// PickaxeItem
	@Override
	public float getMiningSpeed(ItemStack stack, BlockState state) {
		if (state.getMaterial() == Material.STONE && Energy.of(stack).getEnergy() >= cost) {
			// x4 diamond mining speed
			return 32.0F;
		} else {
			return 0.5F;
		}
	}

	// Item
	@Override
	public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
		final ItemStack stack = player.getStackInHand(hand);
		if (player.isSneaking()) {
			switchAOE(stack, cost, world.isClient, MessageIDs.poweredToolID);
			return new TypedActionResult<>(ActionResult.SUCCESS, stack);
		}
		return new TypedActionResult<>(ActionResult.PASS, stack);
	}

	@Override
	public void usageTick(World world, LivingEntity entity, ItemStack stack, int i) {
		ItemUtils.checkActive(stack, cost, entity.world.isClient, MessageIDs.poweredToolID);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
		ItemUtils.buildActiveTooltip(stack, tooltip);
		if (ItemUtils.isActive(stack)) {
			if (isAOE5(stack)) {
				tooltip.add(new LiteralText("5*5").formatted(Formatting.RED));
			} else {
				tooltip.add(new LiteralText("3*3").formatted(Formatting.RED));
			}
		}
	}
}
