package ru.stepanov.dominospring.models;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private ArrayList<Knuckle> TheFirstPlayerKnuckles;
    private ArrayList<Knuckle> TheSecondPlayerKnuckles;
    private ArrayList<Knuckle> market;

    Board() {
        TheFirstPlayerKnuckles = new ArrayList<>();
        TheSecondPlayerKnuckles = new ArrayList<>();
        market = new ArrayList<>();
        initBoard();
    }

    public void initBoard() {
        TheFirstPlayerKnuckles.clear();
        TheSecondPlayerKnuckles.clear();
        market.clear();

        List<Knuckle> allKnuckles = new ArrayList<>();

        for (int i = 0; i != 7; i++) {
            for (int j = i; j != 7; j++) {
                Knuckle knuckle = new Knuckle(i, j);
                allKnuckles.add(knuckle);
            }
        }

        for(int i = 0; i != 7; i++){
            int rand = (int) (Math.random() * allKnuckles.size());
            TheFirstPlayerKnuckles.add(allKnuckles.get(rand));
            allKnuckles.remove(rand);
        }

        for(int i = 0; i != 7; i++){
            int rand = (int) (Math.random() * allKnuckles.size());
            TheSecondPlayerKnuckles.add(allKnuckles.get(rand));
            allKnuckles.remove(rand);
        }

        for(int i = 0; i != 14; i++){
            market.add(allKnuckles.get(i));
        }
    }

    public void showProgress() {
        System.out.println("Фишки первого игрока:");
        for (int i = 0; i != TheFirstPlayerKnuckles.size(); i++) {
            System.out.println(TheFirstPlayerKnuckles.get(i).getOnePart() + " " + TheFirstPlayerKnuckles.get(i).getSecondPart());
        }

        System.out.println("Фишки второго игрока:");
        for (int i = 0; i != TheSecondPlayerKnuckles.size(); i++) {
            System.out.println(TheSecondPlayerKnuckles.get(i).getOnePart() + " " + TheSecondPlayerKnuckles.get(i).getSecondPart());
        }

        System.out.println("Фишки на базаре:");
        for (int i = 0; i != market.size(); i++) {
            System.out.println(market.get(i).getOnePart() + " " + market.get(i).getSecondPart());
        }
    }

    public ArrayList<Knuckle> getTheFirstPlayerKnuckles() {
        return TheFirstPlayerKnuckles;
    }

    public ArrayList<Knuckle> getTheSecondPlayerKnuckles() {
        return TheSecondPlayerKnuckles;
    }

    public ArrayList<Knuckle> getMarket() {
        return market;
    }

    public void setTheFirstPlayerKnuckles(ArrayList<Knuckle> par){
        TheFirstPlayerKnuckles.clear();
        TheFirstPlayerKnuckles.addAll(par);
    }

    public void setTheSecondPlayerKnuckles(ArrayList<Knuckle> par){
        TheSecondPlayerKnuckles.clear();
        TheSecondPlayerKnuckles.addAll(par);
    }

    public void setMarket(ArrayList<Knuckle> par){
        market.clear();
        market.addAll(par);
    }

    public void deleteMarket(){
        market.clear();
    }
}