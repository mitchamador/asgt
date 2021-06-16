package gbas.gtbch.util.calc;

public enum CalcError {
    NO_ERROR(0, "Без ошибок"),
    UNKNOWN_OBJECT(1, "Неверный объект расчета"),
    EXCEPTION(2, "Ошибка при расчете"),
    NULL(3, "Пустой объект"),
    EMPTY_RESULT(4, "Не заполнен результат расчета"),
    UNKNOWN_ERROR(-1, "Неизвестная ошибка"),
    ;


    private final int code;
    private final String name;

    CalcError(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static CalcError getCalcError(int errorCode) {
        for (CalcError calcError : values()) {
            if (calcError.getCode() == errorCode) {
                return calcError;
            }
        }
        return UNKNOWN_ERROR;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
