package com.pegs.volumes.compat;

import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.Method;

public final class IrisCompat {
    private static boolean irisLoaded = false;
    private static Object irisApiInstance = null;
    private static Method isShaderPackInUseMethod = null;
    private static long lastCheckMs = 0L;
    private static boolean cachedShaderActive = false;

    private IrisCompat() {}

    public static void init() {
        irisLoaded = FabricLoader.getInstance().isModLoaded("iris");
        if (!irisLoaded) {
            return;
        }
        try {
            Class<?> apiClass = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            Method getInstance = apiClass.getMethod("getInstance");
            irisApiInstance = getInstance.invoke(null);
            isShaderPackInUseMethod = apiClass.getMethod("isShaderPackInUse");
        } catch (Throwable ignored) {
            irisApiInstance = null;
            isShaderPackInUseMethod = null;
        }
    }

    public static boolean isIrisLoaded() {
        return irisLoaded;
    }

    public static boolean areShadersActive() {
        if (!irisLoaded || irisApiInstance == null || isShaderPackInUseMethod == null) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (now - lastCheckMs > 1000L) {
            lastCheckMs = now;
            try {
                Object result = isShaderPackInUseMethod.invoke(irisApiInstance);
                cachedShaderActive = result instanceof Boolean b && b;
            } catch (Throwable ignored) {
                cachedShaderActive = false;
            }
        }
        return cachedShaderActive;
    }
}
