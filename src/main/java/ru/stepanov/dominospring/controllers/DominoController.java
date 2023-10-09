package ru.stepanov.dominospring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.stepanov.dominospring.models.*;

import java.util.ArrayList;

@Controller
@RequestMapping("/domino")
public class DominoController {
    // Наш канал связи с игрой
    private GamePVEAccess gamePVEAccess;

    // Сам объект игры
    private GamePVE gamePVE;

    private InfoForLoadingGame infoForLoadingGame;

    @Autowired
    public DominoController(GamePVEAccess gamePVEAccess, GamePVE gamePVE, InfoForLoadingGame infoForLoadingGame) {
        this.gamePVEAccess = gamePVEAccess;
        this.gamePVE = gamePVE;
        this.infoForLoadingGame = infoForLoadingGame;
    }

    @GetMapping("/MainPage")
    public String MainPage() {
        return "domino/MainPage";
    }

    @GetMapping("/HistoryOfDomino")
    public String HistoryOfDomino() {
        return "domino/HistoryOfDomino";
    }

    @GetMapping("/StartGame")
    public String StartGame(Model model) {
        infoForLoadingGame.setInfoFromFile("something");
        model.addAttribute("infoForLoadingGame", infoForLoadingGame);
        return "domino/StartGame";
    }

    @GetMapping("/EndOfGame")
    public String EndOfGame(Model model) {
        InfoForEndOfTheGame infoForEndOfTheGame = new InfoForEndOfTheGame();

        // Определяем кто победил
        String whoIsTheWinner;

        if(gamePVE.getBoard().getTheFirstPlayerKnuckles().size() != 0 && gamePVE.getBoard().getTheSecondPlayerKnuckles().size() != 0){
            whoIsTheWinner = "FISH on the board! Check your points and find the winner!";
        } else {
            if(!gamePVE.isWhichTurn()){
                whoIsTheWinner = "Computer wins! Fatality!";
            } else {
                whoIsTheWinner = "Player wins! Congratulations!";
            }
        }

        infoForEndOfTheGame.setValues(whoIsTheWinner, gamePVE.scoringPoints());

        model.addAttribute("infoForEndOfTheGame", infoForEndOfTheGame);
        return "domino/EndOfGame";
    }

    @GetMapping("/ComputerPlaying")
    public String testOfConnection(Model model) {
        System.out.println(gamePVE.getProtokol());
        gamePVE.WhatCanMakeMove();
        gamePVEAccess.setValues(gamePVE.getWhatCanMove(), gamePVE.getKnucklesPlayerCantMove(), gamePVE.getSequence(),
                gamePVE.getBoard().getTheFirstPlayerKnuckles().size(), gamePVE.getIndexOfStartInSequence(),
                gamePVE.getBoard().getTheFirstPlayerKnuckles(), gamePVE.getBoard().getMarket(), gamePVE.getProtokol());

        if(gamePVE.isPlayOn()) {
            model.addAttribute("gamePVEAccess", gamePVEAccess);
        }
        return "domino/ComputerPlaying";
    }

    @PatchMapping("/update")
    public String update(@ModelAttribute("gamePVEAccess") GamePVEAccess gamePVEAccess) {
        try {
            gamePVEAccess.updateGamePVE(gamePVE);
        }
        catch (Exception e){
            System.out.println("игра окончена - это дошло до констроллера");
            return "redirect:/domino/EndOfGame";
        }
        return "redirect:/domino/ComputerPlaying";
    }

    @GetMapping("/newGame")
    public String NewGame() {
        gamePVE = new GamePVE();
        return "redirect:/domino/ComputerPlaying";
    }

    @PatchMapping("/loadGame")
    public String loadGame(@ModelAttribute("infoForLoadingGame") InfoForLoadingGame infoForLoadingGame) {
        try {
            infoForLoadingGame.setAllParametrs(gamePVE);
        }
        catch (Exception e){
            System.out.println("Bad data. Not right save file.");
            return "redirect:/domino/StartGame";
        }
        return "redirect:/domino/ComputerPlaying";
    }
}
