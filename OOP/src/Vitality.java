public final class Vitality {
    private Vitality() {}

    public static final int window_Width  = 1000;
    public static final int window_Height = 600;

    public static final int meteorSize    = 50;
    public static final int speedMeteor   = 6;      // ใช้สุ่มความเร็วตั้งต้น
    public static final float speedMultiply = 1.0f; // คูณตอนเด้งขอบ (1.0 = เด้งเท่าเดิม)
    public static final float maxSpeed    = 8f;     // เพดานความเร็ว
    public static final int bottomPadding = 45;     // เว้นพื้นที่ขอบล่าง (เช่นแถบสถานะ)
}
