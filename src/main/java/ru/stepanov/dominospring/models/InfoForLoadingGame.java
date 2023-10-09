package ru.stepanov.dominospring.models;

import com.sun.scenario.effect.impl.sw.java.JSWPhongLighting_DISTANTPeer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

@Component
public class InfoForLoadingGame {
    public String infoFromFile = "";

    public InfoForLoadingGame() {
    }

    public void setAllParametrs(GamePVE gamePVE) throws Exception {
        String[] subStr = infoFromFile.split("\n");

        LinkedList<Knuckle> sec = new LinkedList<>();
        String[] letters = subStr[0].split(" ");
        for (int i = 0; i != letters.length - 1; i++) {
            if(Character.digit(letters[i].charAt(0), 10) == -1 || (Character.digit(letters[i].charAt(1), 10) == -1)) {
                throw new Exception("Bad data");
            }
            sec.add(new Knuckle(Character.digit(letters[i].charAt(0), 10), (Character.digit(letters[i].charAt(1), 10))));
        }

        ArrayList<Knuckle> mark = new ArrayList<>();
        letters = subStr[1].split(" ");
        System.out.println(letters[0]);
        System.out.println("Размер массива letters " + letters.length);
        if(Objects.equals(letters[0], "null")){
            // маркет пустой
        }
        else{
            for (int i = 0; i != letters.length - 1; i++) {
                if(Character.digit(letters[i].charAt(0), 10) == -1 || (Character.digit(letters[i].charAt(1), 10) == -1)) {
                    throw new Exception("Bad data");
                }
                mark.add(new Knuckle(Character.digit(letters[i].charAt(0), 10), (Character.digit(letters[i].charAt(1), 10))));
            }
        }

        ArrayList<Knuckle> firstPlSec = new ArrayList<>();
        letters = subStr[2].split(" ");
        for (int i = 0; i != letters.length - 1; i++) {
            if(Character.digit(letters[i].charAt(0), 10) == -1 || (Character.digit(letters[i].charAt(1), 10) == -1)) {
                throw new Exception("Bad data");
            }
            firstPlSec.add(new Knuckle(Character.digit(letters[i].charAt(0), 10), (Character.digit(letters[i].charAt(1), 10))));
        }

        ArrayList<Knuckle> secondPlSec = new ArrayList<>();
        letters = subStr[3].split(" ");
        for (int i = 0; i != letters.length - 1; i++) {
            if(Character.digit(letters[i].charAt(0), 10) == -1 || (Character.digit(letters[i].charAt(1), 10) == -1)) {
                throw new Exception("Bad data");
            }
            secondPlSec.add(new Knuckle(Character.digit(letters[i].charAt(0), 10), (Character.digit(letters[i].charAt(1), 10))));
        }


        int index = Character.digit(subStr[4].charAt(0), 10);
        System.out.println(index);

        gamePVE.setAllParametrs(sec, mark, firstPlSec, secondPlSec, index);

    }

    public void setInfoFromFile(String infoFromFile) {
        this.infoFromFile = infoFromFile;
    }

    public String getInfoFromFile() {
        return infoFromFile;
    }
}
