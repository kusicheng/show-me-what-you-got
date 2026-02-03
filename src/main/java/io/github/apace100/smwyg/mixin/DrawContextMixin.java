package io.github.apace100.smwyg.mixin;

import io.github.apace100.smwyg.ShowMeWhatYouGot;
import io.github.apace100.smwyg.tooltip.HorizontalLayoutTooltipComponent;
import io.github.apace100.smwyg.tooltip.ItemStackTooltipComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(DrawContext.class)
public class DrawContextMixin {

    @Unique
    private ItemStack smwyg$hoveredStack;

    @Inject(method = "drawHoverEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItemTooltip(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void smwyg$cacheHoveredStack(TextRenderer textRenderer, Style style, int x, int y, CallbackInfo ci, HoverEvent hoverEvent, HoverEvent.ItemStackContent itemStackContent) {
        ItemStack stack = itemStackContent.asStack();
        if(stack.contains(DataComponentTypes.CUSTOM_DATA)) {
            NbtComponent customDataNbt = stack.get(DataComponentTypes.CUSTOM_DATA);
            if(!customDataNbt.isEmpty() && customDataNbt.getNbt().getBoolean(ShowMeWhatYouGot.HIDE_STACK_NBT)) {
                return;
            }
        }
        smwyg$hoveredStack = stack;
    }

    @Inject(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V", at = @At("HEAD"))
    private void smwyg$modifyFirstTooltipComponent(
        TextRenderer textRenderer,
        List<TooltipComponent> components,
        Optional<TooltipPositioner> positionerOpt,
        int x,
        int y,
        CallbackInfo ci
    ) {
        if(smwyg$hoveredStack == null || smwyg$hoveredStack.isEmpty() || components.isEmpty()) {
            return;
        }

        try {
            Object firstElement = components.get(0);
            if (!(firstElement instanceof TooltipComponent)) {
                smwyg$hoveredStack = null;
                return;
            }
            TooltipComponent originalComponent = (TooltipComponent) firstElement;
            TooltipComponent stackComponent = new ItemStackTooltipComponent(smwyg$hoveredStack);
            
            TooltipComponent combinedComponent;
            if(textRenderer.isRightToLeft()) {
                combinedComponent = new HorizontalLayoutTooltipComponent(
                    List.of(originalComponent, stackComponent), 3);
            } else {
                combinedComponent = new HorizontalLayoutTooltipComponent(
                    List.of(stackComponent, originalComponent), 3);
            }
            
            try {
                components.set(0, combinedComponent);
            } catch (UnsupportedOperationException e) {
                List<TooltipComponent> newComponents = new ArrayList<>(components);
                newComponents.set(0, combinedComponent);
                components.clear();
                components.addAll(newComponents);
            }
        } catch (Exception e) {}
        
        smwyg$hoveredStack = null;
    }
}
