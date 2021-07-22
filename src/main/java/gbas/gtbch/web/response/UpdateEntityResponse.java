package gbas.gtbch.web.response;

public class UpdateEntityResponse extends Response {

    /**
     * entity id
     */
    int id;

    public UpdateEntityResponse(int id, String message) {
        super(message);
        this.id = id;
    }

    public UpdateEntityResponse() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
