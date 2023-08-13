package com.mystic.betterdebug.mixin;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.EOFException;
import java.io.IOException;

@Mixin(ByteBufInputStream.class)
public class BetterDebugMixin {

    public int available(int fieldSize) {
        return (endIndex + fieldSize) - buffer.readerIndex();
    }

    @Shadow @Final private int endIndex;

    @Shadow @Final private ByteBuf buffer;

    @Inject(method = "checkAvailable(I)V", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void debug(int fieldSize, CallbackInfo ci) throws IOException {
        ci.cancel();
        if (fieldSize < 0) {
            throw new IndexOutOfBoundsException("fieldSize cannot be a negative number");
        }
        if (fieldSize > available(fieldSize)) {
            throw new EOFException("fieldSize is too long! Length is " + fieldSize
                    + ", but maximum is " + available(fieldSize));
        }
    }
}
