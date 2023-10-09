package ru.stepanov.dominospring.models;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;

@Component
public class GamePVEAccess {
    // Поля для отрисовки игрового поля
    private Knuckle[] playersKnucklesToPut;
    private Knuckle[] playersKnucklesNotToPut;
    private Knuckle[] sequenceOnBoard;
    private int numOfCompKnuckles;
    private int playersTurn;
    private int indexOfStartInSequence;

    // Поля для сохранения

    private Knuckle[] computerKnuckles;

    private Knuckle[] market;

    // Поле для ведения протокола игры
    private String protokol;


    public void setValues(ArrayList<Knuckle> playersKnucklesToPut, ArrayList<Knuckle> playersKnucklesNotToPut,
                          LinkedList<Knuckle> sequenceOnBoard, int numOfCompKnuckles, int indexOfStartInSequence,
                          ArrayList<Knuckle> computerKnuckles, ArrayList<Knuckle> market, String protokol) {
        // Установка костяшек, которыми можно ходить
        this.playersKnucklesToPut = new Knuckle[playersKnucklesToPut.size()];
        for (int i = 0; i != playersKnucklesToPut.size(); i++) {
            this.playersKnucklesToPut[i] = playersKnucklesToPut.get(i);
        }

        // Установка костяшек, которыми нельзя ходить
        this.playersKnucklesNotToPut = new Knuckle[playersKnucklesNotToPut.size()];
        for (int i = 0; i != playersKnucklesNotToPut.size(); i++) {
            this.playersKnucklesNotToPut[i] = playersKnucklesNotToPut.get(i);
        }

        // Установка костяшек, расположенных на поле
        this.sequenceOnBoard = new Knuckle[sequenceOnBoard.size()];
        for (int i = 0; i != sequenceOnBoard.size(); i++) {
            this.sequenceOnBoard[i] = sequenceOnBoard.get(i);
        }

        // Установка количества костяшек компа
        this.numOfCompKnuckles = numOfCompKnuckles;

        this.indexOfStartInSequence = indexOfStartInSequence;

        this.playersTurn = -1;

        // Установка значений лишь для сохранения, чтобы иметь полное представление об игре
        this.computerKnuckles = new Knuckle[computerKnuckles.size()];
        for (int i = 0; i != computerKnuckles.size(); i++) {
            this.computerKnuckles[i] = computerKnuckles.get(i);
        }

        this.market = new Knuckle[market.size()];
        for (int i = 0; i != market.size(); i++) {
            this.market[i] = market.get(i);
        }

        this.protokol = protokol;

    }

    public void updateGamePVE(GamePVE gamePVE) throws Exception {
        System.out.println("players turn: " + this.getPlayersTurn());
        System.out.printf("Ходит игрок");
        try {
            gamePVE.doTurn(this.getPlayersTurn());
        } catch (Exception e){
            System.out.println("Игрок победил");
            throw new Exception("Игрок победил");
        }

        gamePVE.showCurrentGame();
        System.out.printf("Ходит компьютер");
        gamePVE.computerTurn();

        gamePVE.WhatCanMakeMove();
        if(gamePVE.getWhatCanMove().size() == 0){
            while (gamePVE.getWhatCanMove().size() == 0) {
                gamePVE.takeKnuckleFromMarket();
                gamePVE.WhatCanMakeMove();
                if(gamePVE.getWhatCanMove().size() == 0){
                    if (gamePVE.getBoard().getMarket().size() > 0) {
                        gamePVE.takeKnuckleFromMarket();
                        gamePVE.WhatCanMakeMove();
                        if (gamePVE.getWhatCanMove().size() > 0) {
                            // Ничего, мы просто знаем что можем пойти
                        } else {
                            gamePVE.nextMove();
                        }
                    }
                    else {
                        gamePVE.nextMove();
                        gamePVE.WhatCanMakeMove();
                        if (gamePVE.getWhatCanMove().size() == 0) {
                            // Ситуация "рыба"
                            System.out.println("Ситуация - рыба");
                            throw new Exception("Ситуация - рыба");
                        }
                    }
                }
            }
            if (!gamePVE.isWhichTurn()) {
                try {
                    gamePVE.computerTurn();
                } catch (Exception e){
                    System.out.println("После череды взятий фишек с базара все сломалось");
                }
            }
        }
        gamePVE.showCurrentGame();
    }

    public int getPlayersTurn() {
        return playersTurn;
    }

    public void setPlayersTurn(int playersTurn) {
        this.playersTurn = playersTurn;
    }

    public Knuckle[] getPlayersKnucklesToPut() {
        return playersKnucklesToPut;
    }

    public void setPlayersKnucklesToPut(Knuckle[] playersKnucklesToPut) {
        this.playersKnucklesToPut = playersKnucklesToPut;
    }

    public Knuckle[] getPlayersKnucklesNotToPut() {
        return playersKnucklesNotToPut;
    }

    public void setPlayersKnucklesNotToPut(Knuckle[] playersKnucklesNotToPut) {
        this.playersKnucklesNotToPut = playersKnucklesNotToPut;
    }

    public Knuckle[] getSequenceOnBoard() {
        return sequenceOnBoard;
    }

    public void setSequenceOnBoard(Knuckle[] sequenceOnBoard) {
        this.sequenceOnBoard = sequenceOnBoard;
    }

    public int getNumOfCompKnuckles() {
        return numOfCompKnuckles;
    }

    public void setNumOfCompKnuckles(int numOfCompKnuckles) {
        this.numOfCompKnuckles = numOfCompKnuckles;
    }

    public int getIndexOfStartInSequence() {
        return indexOfStartInSequence;
    }

    public void setIndexOfStartInSequence(int indexOfStartInSequence) {
        this.indexOfStartInSequence = indexOfStartInSequence;
    }

    public Knuckle[] getComputerKnuckles() {
        return computerKnuckles;
    }

    public Knuckle[] getMarket() {
        return market;
    }

    public String getProtokol() {
        return protokol;
    }

    public void setProtokol(String protokol) {
        this.protokol = protokol;
    }
}
