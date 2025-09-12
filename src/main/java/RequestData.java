public class RequestData {
    private float x;
    private float y;
    private float r;

    public RequestData(float x, float y, float r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    public RequestData() {
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getR() {
        return r;
    }

    public void setR(float r) {
        this.r = r;
    }
}
