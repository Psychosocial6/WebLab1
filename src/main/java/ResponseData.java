import java.time.Instant;
import java.time.LocalDateTime;

public class ResponseData {
    private long time;
    private LocalDateTime localTime;
    private boolean result;

    public ResponseData(long time, LocalDateTime localTime, boolean result) {
        this.time = time;
        this.localTime = localTime;
        this.result = result;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public LocalDateTime getLocalTime() {
        return localTime;
    }

    public void setLocalTime(LocalDateTime localTime) {
        this.localTime = localTime;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return String.format("{ time: %s, localTime: %s, result: %s }", time, localTime, result);
    }
}
