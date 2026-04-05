package app.mkiniz.poctime.base.historic;

public enum HistoryErrorEnum {
    VALID_FROM_NULL(100),
    VALID_UNTIL_NOT_NULL(101),
    VALID_FROM_SMALLER_THEN_LAST_VALID_FROM_HISTORY(102);

    private final int code;

    HistoryErrorEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
