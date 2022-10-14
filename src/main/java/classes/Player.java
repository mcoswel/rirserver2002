package classes;

import java.util.ArrayList;
import java.util.List;

public class Player implements Comparable<Player>{
    private String name;
    private String id;
    private String picUri;
    private String fcmToken;
    private int bestScore;
    private int timesPlayed;
    boolean logged;
    private boolean winTicket;
    private List<Integer> playerGifts = new ArrayList<>();
    private List<Integer> playerBonus = new ArrayList<>();
    private boolean reviewed;
    private int bankrupt;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPicUri() {
        return picUri;
    }

    public void setPicUri(String picUri) {
        this.picUri = picUri;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public int getBestScore() {
        return bestScore;
    }

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }

    public int getTimesPlayed() {
        return timesPlayed;
    }

    public void setTimesPlayed(int timesPlayed) {
        this.timesPlayed = timesPlayed;
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public List<Integer> getPlayerGifts() {
        return playerGifts;
    }

    public List<Integer> getPlayerBonus() {
        return playerBonus;
    }

    public boolean isWinTicket() {
        return winTicket;
    }

    public void setWinTicket(boolean winTicket) {
        this.winTicket = winTicket;
    }

    public boolean isReviewed() {
        return reviewed;
    }

    public void setReviewed(boolean reviewed) {
        this.reviewed = reviewed;
    }

    public int getBankrupt() {
        return bankrupt;
    }

    public void setBankrupt(int bankrupt) {
        this.bankrupt = bankrupt;
    }

    @Override
    public int compareTo(Player player) {
        return (player.bestScore-this.bestScore);
    }
}
