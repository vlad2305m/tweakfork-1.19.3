package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.gui.widgets.WidgetConfigOptionBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;

@Mixin(WidgetConfigOptionBase.class)
public class MixinWidgetConfigOptionBase<TYPE> extends WidgetListEntryBase<TYPE> {

    @Shadow
    protected int maxTextfieldTextLength;
    
    public MixinWidgetConfigOptionBase(int x, int y, int width, int height, TYPE entry, int listIndex) {
        super(x, y, width, height, entry, listIndex);
        // TODO Auto-generated constructor stub
    }



    @Inject(method = "<init>", at = @At("RETURN"))
    private void overrideMaxLength(int x, int y, int width, int height,
    WidgetListConfigOptionsBase<?, ?> parent, TYPE entry, int listIndex, CallbackInfo ci) {
        maxTextfieldTextLength = Integer.MAX_VALUE;
    }
}
