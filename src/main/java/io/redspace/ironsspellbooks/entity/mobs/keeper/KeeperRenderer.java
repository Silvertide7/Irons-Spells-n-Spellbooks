package io.redspace.ironsspellbooks.entity.mobs.keeper;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class KeeperRenderer extends AbstractSpellCastingMobRenderer {

    //public static ModelLayerLocation PYROMANCER_MODEL_LAYER = new ModelLayerLocation(new ResourceLocation(irons_spellbooks.MODID, "pyromancer"), "body");
    //public static ModelLayerLocation PYROMANCER_INNER_ARMOR = new ModelLayerLocation(new ResourceLocation(irons_spellbooks.MODID, "pyromancer"), "inner_armor");
    //public static ModelLayerLocation PYROMANCER_OUTER_ARMOR = new ModelLayerLocation(new ResourceLocation(irons_spellbooks.MODID, "pyromancer"), "outer_armor");

    public KeeperRenderer(EntityRendererProvider.Context context) {
        super(context, new KeeperModel());
        this.shadowRadius = 0.65f;

    }
    //TODO: cleanup/propagate these changes to everyone else
    @Override
    public void renderEarly(AbstractSpellCastingMob animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float partialTicks) {
        poseStack.scale(1.3f, 1.3f, 1.3f);
        super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, partialTicks);
    }

//    @Override
//    public void render(AbstractSpellCastingMob animatable, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
//        super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
//    }
//    @Override
//    public void render(GeoModel model, AbstractSpellCastingMob animatable, float partialTick, RenderType type, PoseStack poseStack, MultiBufferSource bufferSource, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
//        RenderSystem.disableCull();
////        type = RenderType.eyes(modelProvider.getTextureResource(animatable));
////        buffer = bufferSource.getBuffer(type);
//        //poseStack.scale(1.3f, 1.3f, 1.3f);
//        super.render(model, animatable, partialTick, RenderType.entityTranslucentCull(modelProvider.getTextureResource(animatable)), poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
//    }

    @Override
    public RenderType getRenderType(AbstractSpellCastingMob animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }
}