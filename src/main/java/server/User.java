package server;

public class User {
    private int id;
    private String name;

    public Boolean getInGame() {
        return inGame;
    }

    public void setInGame(Boolean inGame) {
        this.inGame = inGame;
    }

    private Boolean inGame;
    private int partnerId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public int getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(int partnerId) {
        this.partnerId = partnerId;
    }
    public User(int _id, String _name){
        this.setPartnerId(-1);
        this.setInGame(false);
        this.id = _id;
        this.name = _name;
    }

}