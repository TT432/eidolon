package elucent.eidolon.codex;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import elucent.eidolon.Eidolon;
import elucent.eidolon.ritual.Ritual;
import elucent.eidolon.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RitualPage extends Page {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(Eidolon.MODID, "textures/gui/codex_ritual_page.png");
    Ritual ritual;
    ItemStack center;
    RitualIngredient[] inputs;

    public static class RitualIngredient {
        public ItemStack stack;
        public boolean isFocus;

        public RitualIngredient(ItemStack stack, boolean isFocus) {
            this.stack = stack;
            this.isFocus = isFocus;
        }
    }

    public RitualPage(Ritual ritual, ItemStack center, RitualIngredient... inputs) {
        super(BACKGROUND);
        this.ritual = ritual;
        this.center = center;
        this.inputs = inputs;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(CodexGui gui, PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        float angleStep = Math.min(30, 180 / inputs.length);
        double rootAngle = 90 - (inputs.length - 1) * angleStep / 2;
        for (int i = 0; i < inputs.length; i ++) {
            double a = Math.toRadians(rootAngle + angleStep * i);
            int dx = (int)(64 + 48 * Math.cos(a));
            int dy = (int)(88 + 48 * Math.sin(a));
            if (inputs[i].isFocus) gui.blit(poseStack, x + dx - 13, y + dy - 13, 128, 0, 26, 24);
            else gui.blit(poseStack, x + dx - 8, y + dy - 8, 154, 0, 16, 16);
        }

        RenderSystem.enableBlend();
        // todo RenderSystem.shadeModel(GL11.GL_SMOOTH);
        // todo RenderSystem.alphaFunc(GL11.GL_GEQUAL, 1f / 256f);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        Tesselator tess = Tesselator.getInstance();
        RenderSystem.disableTexture();
        RenderSystem.depthMask(false);
        RenderUtil.dragon(poseStack, MultiBufferSource.immediate(tess.getBuilder()), x + 64, y + 48, 20, 20, ritual.getRed(), ritual.getGreen(), ritual.getBlue());
        tess.end();
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        for (int j = 0; j < 2; j++) {
            RenderUtil.litQuad(poseStack, MultiBufferSource.immediate(tess.getBuilder()), x + 52, y + 36, 24, 24,
                ritual.getRed(), ritual.getGreen(), ritual.getBlue(), Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(ritual.getSymbol()));
            tess.end();
        }
        // todo RenderSystem.defaultAlphaFunc();
        RenderSystem.disableBlend();
        // todo RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.depthMask(true);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderIngredients(CodexGui gui, PoseStack mStack, int x, int y, int mouseX, int mouseY) {
        float angleStep = Math.min(30, 180 / inputs.length);
        double rootAngle = 90 - (inputs.length - 1) * angleStep / 2;
        for (int i = 0; i < inputs.length; i ++) {
            double a = Math.toRadians(rootAngle + angleStep * i);
            int dx = (int)(64 + 48 * Math.cos(a));
            int dy = (int)(88 + 48 * Math.sin(a));
            drawItem(gui, mStack, inputs[i].stack, x + dx - 8, y + dy - 8, mouseX, mouseY);
        }
        drawItem(gui, mStack, center,x + 56, y + 80, mouseX, mouseY);
    }
}
