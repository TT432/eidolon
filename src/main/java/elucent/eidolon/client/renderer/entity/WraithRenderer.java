package elucent.eidolon.client.renderer.entity;

import elucent.eidolon.Eidolon;
import elucent.eidolon.client.models.entity.WraithModel;
import elucent.eidolon.entity.WraithEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class WraithRenderer extends MobRenderer<WraithEntity, WraithModel> {
    public WraithRenderer(EntityRendererProvider.Context rendererManager, WraithModel entityModelIn, float shadowSizeIn) {
        super(rendererManager, entityModelIn, shadowSizeIn);
    }

    @Override
    public ResourceLocation getTextureLocation(WraithEntity entity) {
        return new ResourceLocation(Eidolon.MODID, "textures/entity/wraith.png");
    }
}
