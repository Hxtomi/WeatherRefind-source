package dev.norevy.weatherrefind;

import java.lang.reflect.Method;
/** Optional Iris/Oculus API bridge. Lookup happens once, never per particle/frame. */
public final class ShaderSupport {
    private static boolean discovered;
    private static Object api;
    private static Method query;
    private static boolean failed;
    private ShaderSupport() {}
    public static boolean active() {
        if (!discovered) {
            discovered = true;
            for (String name : new String[]{"net.irisshaders.iris.api.v0.IrisApi", "net.coderbot.iris.api.v0.IrisApi"}) {
                try {
                    Class<?> type = Class.forName(name, false, ShaderSupport.class.getClassLoader());
                    api = type.getMethod("getInstance").invoke(null);
                    query = type.getMethod("isShaderPackInUse");
                    break;
                } catch (ClassNotFoundException ignored) {
                    // This optional shader loader is not installed.
                } catch (ReflectiveOperationException | LinkageError failure) {
                    failed = true;
                    WeatherRefind.LOGGER.warn("[WeatherRefind] Shader API unavailable; AUTO uses vanilla weather.", failure);
                    break;
                }
            }
        }
        if (failed) return true;
        if (query == null) return false;
        try {
            return Boolean.TRUE.equals(query.invoke(api));
        } catch (ReflectiveOperationException | LinkageError failure) {
            failed = true;
            WeatherRefind.LOGGER.warn("[WeatherRefind] Shader API failed; AUTO uses vanilla weather.", failure);
            return true;
        }
    }
}
