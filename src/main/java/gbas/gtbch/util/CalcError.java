package gbas.gtbch.util;

public enum CalcError {
    NO_ERROR(0, "Без ошибок"),
    UNKNOWN_OBJECT(1, "Неверный объект расчета"),
    EXCEPTION(2, "Исключение при расчете")
    ;


    private final int code;
    private final String name;

    CalcError(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
