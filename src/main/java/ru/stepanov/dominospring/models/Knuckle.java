package ru.stepanov.dominospring.models;

public class Knuckle {
    private int onePart;
    private int secondPart;

    public Knuckle(int onePart, int secondPart) {
        this.onePart = onePart;
        this.secondPart = secondPart;
    }

    public int getOnePart() {
        return onePart;
    }

    public int getSecondPart() {
        return secondPart;
    }

    public void turnOver() {
        int temp = onePart;
        onePart = secondPart;
        secondPart = temp;
    }
}
