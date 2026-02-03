package io.github.apace100.smwyg.tooltip;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;

public class ItemStackTooltipComponent implements TooltipComponent {

    private final ItemStack stack;

    public ItemStackTooltipComponent(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return 18;
    }

    @Override
    public int getHeight() {
        return 18;
    }

    // @Override
    // public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext drawContext) {
    //     drawContext.drawItem(stack, x, y);
    //     int count = stack.getCount();
    //     String countLabel = null;
    //     if(count > 1) {
    //         countLabel = String.valueOf(stack.getCount());
    //     }
    //     drawContext.drawItemInSlot(textRenderer, stack, x, y, countLabel);
    // }
    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext drawContext) {
        // Basic item rendering
        drawContext.drawItem(stack, x, y);
    
        // In 1.21.1, drawItemGuiAndLabels is often the preferred way 
        // or drawItemInSlot if using the specific overload:
        String countLabel = (stack.getCount() > 1) ? String.valueOf(stack.getCount()) : null;
    
        // Note: ensure the method signature matches your mappings.
        // For many 1.21.1 environments, it looks like this:
        drawContext.drawItemInSlot(textRenderer, stack, x, y, countLabel);
    }
}
