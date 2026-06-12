package app.mkiniz.poctime.base.historic;

public enum HistoryErrorEnum {
    VALID_FROM_NULL(100),
    VALID_UNTIL_NOT_NULL(101),
    VALID_FROM_SMALLER_THEN_LAST_VALID_FROM_HISTORY(102),
    VALID_FROM_MUST_BE_GREATER_THAN_LAST_ENTRY(103),
    VALID_FROM_MUST_BE_EQUAL_TO_VALID_FROM(104),
    VALID_UNTIL_MUST_BE_EQUAL_TO_VALID_UNTIL(105);

    private final int code;

    HistoryErrorEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
