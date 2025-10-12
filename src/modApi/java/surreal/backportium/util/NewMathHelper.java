package surreal.backportium.util;

public final class NewMathHelper {

    public static float lerp(float pct, float start, float end) {
        return start + pct * (end - start);
    }

    public static float rotLerpRad(float angleIn, float maxAngleIn, float mulIn) {
        float f = (mulIn - maxAngleIn) % ((float) Math.PI * 2F);
        if (f < -(float) Math.PI) {
            f += ((float) Math.PI * 2F);
        }
        if (f >= (float) Math.PI) {
            f -= ((float) Math.PI * 2F);
        }
        return maxAngleIn + angleIn * f;
    }

    private NewMathHelper() {}
}
