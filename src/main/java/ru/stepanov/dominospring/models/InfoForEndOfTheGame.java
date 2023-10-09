package ru.stepanov.dominospring.models;

public class InfoForEndOfTheGame {
    private String whoIsTheWinner;
    private int numOfFirstPlayerPoints;
    private int numOfSecondPlayerPoints;

    public InfoForEndOfTheGame() {
    }

    public void setValues(String whoIsTheWinner, int[] points) {
        this.whoIsTheWinner = whoIsTheWinner;
        numOfFirstPlayerPoints = points[0];
        numOfSecondPlayerPoints = points[1];
    }

    public String getWhoIsTheWinner() {
        return whoIsTheWinner;
    }

    public int getNumOfFirstPlayerPoints() {
        return numOfFirstPlayerPoints;
    }

    public int getNumOfSecondPlayerPoints() {
        return numOfSecondPlayerPoints;
    }
}
