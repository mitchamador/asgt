package gbas.gtbch.web.response;

public class AuthResponse extends Response {

    private String token;

    public AuthResponse(String message) {
        super(message);
    }

    public AuthResponse(String message, String token) {
        super(message);
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
