package androigati.eshare.model;

import java.util.Date;

/**
 * Created by valerio on 27/08/16.
 */
public class Content {

    private String id;
    private String title;

    private String utenteId;
    private String body;
    private String url;
    private int rank;
    private Date timestamp;
    private String type;
    private Position position;
    private User user;

    public String getUtenteId() {
        return utenteId;
    }

    public void setUtenteId(String utenteId) {
        this.utenteId = utenteId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
