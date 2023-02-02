package fi.dy.masa.tweakeroo.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.IGuiEditSign;
import fi.dy.masa.tweakeroo.util.MiscUtils;

@Mixin(AbstractSignEditScreen.class)
public abstract class MixinAbstractSignEditScreen extends Screen implements IGuiEditSign
{
    protected MixinAbstractSignEditScreen(Text textComponent)
    {
        super(textComponent);
    }

    @Shadow @Final protected SignBlockEntity blockEntity;
    @Shadow @Final protected String[] text;

    @Override
    public SignBlockEntity getTile()
    {
        return this.blockEntity;
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void storeText(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_SIGN_COPY.getBooleanValue())
        {
            MiscUtils.copyTextFromSign(this.blockEntity);
        }
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void preventGuiOpen(CallbackInfo ci)
    {
        if (FeatureToggle.TWEAK_SIGN_COPY.getBooleanValue())
        {
            MiscUtils.applyPreviousTextToSign(this.blockEntity, this.text);
        }

        if (Configs.Disable.DISABLE_SIGN_GUI.getBooleanValue())
        {
            // Update the keybind state, because opening a GUI resets them all.
            // Also, KeyBinding.updateKeyBindState() only works for keyboard keys
            KeyBinding keybind = MinecraftClient.getInstance().options.useKey;
            InputUtil.Key input = InputUtil.fromTranslationKey(keybind.getBoundKeyTranslationKey());

            if (input != null)
            {
                KeyBinding.setKeyPressed(input, KeybindMulti.isKeyDown(KeybindMulti.getKeyCode(keybind)));
            }

            GuiBase.openGui(null);
        }
    }
}
