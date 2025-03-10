package elucent.eidolon.item.curio;

import com.mojang.blaze3d.systems.RenderSystem;
import elucent.eidolon.Registry;
import elucent.eidolon.item.ItemBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nonnull;

public class SanguineAmuletItem extends ItemBase {
    public SanguineAmuletItem(Properties properties) {
        super(properties);
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> {
            MinecraftForge.EVENT_BUS.addListener(SanguineAmuletItem::addTooltip);
            MinecraftForge.EVENT_BUS.addListener(SanguineAmuletItem::renderTooltip);
            return null;
        });
    }

    static int getCharge(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("charge")) {
            return stack.getTag().getInt("charge");
        }
        return 0;
    }

    static void addCharge(ItemStack stack, int diff) {
        int newCharge = Mth.clamp(getCharge(stack) + diff, 0, 40);
        stack.getOrCreateTag().putInt("charge", newCharge);
    }

    static void setCharge(ItemStack stack, int charge) {
        int newCharge = Mth.clamp(charge, 0, 40);
        stack.getOrCreateTag().putInt("charge", newCharge);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag unused) {
        return new EidolonCurio(stack) {
            @Override
            public void curioTick(String type, int index, LivingEntity entity) {
                if (!entity.level.isClientSide) {
                    if (entity.tickCount % 80 == 0 &&
                        entity.getHealth() == entity.getMaxHealth() &&
                        entity instanceof Player && ((Player) entity).getFoodData().getFoodLevel() >= 18 &&
                        getCharge(stack) < 40) {
                        Player player = (Player) entity;
                        float f = player.getFoodData().getSaturationLevel() > 0 ?
                            Math.min(4 * player.getFoodData().getSaturationLevel(), 16.0F) : 4.0f;
                        player.causeFoodExhaustion(f);
                        addCharge(stack, 1);
                        // EnderPearlEntity e;
                    }
                    if (entity.tickCount % 10 == 0 &&
                        getCharge(stack) > 0 && entity.getHealth() < entity.getMaxHealth()) {
                        int taken = (int) Math.min(1, entity.getMaxHealth() - entity.getHealth());
                        addCharge(stack, -taken);
                        entity.heal(taken);
                    }
                }
            }

            @Override
            public boolean canSync(String identifier, int index, LivingEntity livingEntity) {
                return true;
            }

            @Override
            @Nonnull
            public CompoundTag writeSyncData() {
                CompoundTag nbt = new CompoundTag();
                nbt.putInt("charge", getCharge(stack));
                return nbt;
            }

            @Override
            public void readSyncData(CompoundTag compound) {
                setCharge(stack, compound.getInt("charge"));
            }

            @Override
            public boolean canRightClickEquip() {
                return true;
            }
        };
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void addTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() == Registry.SANGUINE_AMULET.get()) {
            int charge = getCharge(event.getItemStack());
            if (charge > 0) event.getToolTip().add(new TextComponent(" "));
            for (int i = 0; i < charge; i += 20) {
                event.getToolTip().add(new TextComponent(" "));
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void renderTooltip(RenderTooltipEvent.Color event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() == Registry.SANGUINE_AMULET.get()) {
            Minecraft mc = Minecraft.getInstance();
            RenderSystem.setShaderTexture(0, new ResourceLocation("minecraft", "textures/gui/icons.png"));
            int charge = getCharge(event.getItemStack());
            int rows = (charge + 19) / 20;
            for (int i = 0; i < charge; i += 20) {
                for (int j = 0; j < Mth.clamp(charge - i, 0, 20); j += 2) {
                    if (charge - (i + j) == 1) {
                        GuiComponent.blit(event.getPoseStack(), event.getX() - 1 + j / 2 * 8, event.getY() + (event.getComponents().size() - rows) * (event.getFont().lineHeight + 1) + (i / 20) * 9 + 2, 61, 0, 9, 9, 256, 256);
                    } else {
                        GuiComponent.blit(event.getPoseStack(), event.getX() - 1 + j / 2 * 8, event.getY() + (event.getComponents().size() - rows) * (event.getFont().lineHeight + 1) + (i / 20) * 9 + 2, 52, 0, 9, 9, 256, 256);
                    }
                }
            }
        }
    }
}
