package gbas.gtbch.web.response;

public class ResponseValue<T> extends Response {
    private final T value;

    public T getValue() {
        return value;
    }

    public ResponseValue(T value, String message) {
        this.message = message;
        this.value = value;
    }
}
