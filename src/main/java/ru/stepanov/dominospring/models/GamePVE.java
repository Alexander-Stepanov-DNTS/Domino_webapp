package ru.stepanov.dominospring.models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

@Component
public class GamePVE {
    // Поле, хранящее последовательность из костей на столе
    LinkedList<Knuckle> sequence;

    ArrayList<Knuckle> knucklesPlayerCantMove;

    // Поле, хранящее левую часть последовательности
    int leftPartOfSequence;

    // Поле, хранящее правую часть последовательсноти
    int rightPartOfSequence;

    // Поле, хранящее модель доски с котяшками
    private Board board;

    // Поле, хранящее перечень костей, которыми может пойти текущий игрок
    private ArrayList<Knuckle> whatCanMove;

    // Поле, хранящее очередность хода
    // if false - ходит компютер else ходит второй игрок
    private boolean whichTurn;

    // Поле, хранящее первую кость, которая будет положена на игровое поле
    private int whatNumToStart;

    // Поле, хранящее закончена игра или нет
    private boolean playOn;

    private int indexOfStartInSequence;

    // Поле для ведения протокола игры
    private String protokol;

    @Autowired
    public GamePVE() {
        board = new Board();
        // Последовательность костяшек на столе
        sequence = new LinkedList<>();
        // Перечень всех костей, которыми может пойти текущий игрок
        whatCanMove = new ArrayList<>();
        // Перечень всех костей, которыми может не может пойти текущий игрок
        knucklesPlayerCantMove = new ArrayList<>();
        // Определяем что игра продолжается
        playOn = true;
        // Инициализируем протокол игры
        protokol = "";

        // Определяем очередность хода
        while (true) {
            try {
                WhoIsFirst(1);
                break;
            } catch (IOException e) {
                // Если сложилась ситуация, в которой никто не может начать игру, происходит пересдача костей
                board.initBoard();
            }
        }

        // Вывод состояния костей и поля перед первым ходом в консоль
        System.out.println("Игра началась");
        showCurrentGame();
        System.out.println("------------------------------------------");

        // Первый ход "вынужденный", поэтому он определен и игра знает, каков он будет
        setFirstKnuckle();
        indexOfStartInSequence = 0;
        nextMove();
        showCurrentGame();

        // Определяем переменные хранящие крайние значения нашей последовательности из костей
        leftPartOfSequence = sequence.getFirst().getOnePart();
        rightPartOfSequence = sequence.getFirst().getSecondPart();

        if (!whichTurn) {
            try {
                computerTurn();
            } catch (Exception e) {
                System.out.println("Первый ход сломался");
            }
            showCurrentGame();
        }

        WhatCanMakeMove();
        if (whatCanMove.size() == 0) {
            while (whatCanMove.size() == 0) {
                if (board.getMarket().size() > 0) {
                    takeKnuckleFromMarket();
                    WhatCanMakeMove();
                    if (whatCanMove.size() > 0) {

                    } else {
                        nextMove();
                    }
                } else {
                    nextMove();
                    WhatCanMakeMove();
                    if (whatCanMove.size() == 0) {
                        // Ситуация "рыба"
                        System.out.println("Ситуация - рыба");
                        playOn = false;
                    }
                }
            }
            if (!whichTurn) {
                try {
                    computerTurn();
                } catch (Exception e) {
                    System.out.println("Второй ход сломался");
                }
                showCurrentGame();
            }
        }
    }

    // Метод определяет очередность первого хода
    private void WhoIsFirst(int firstNumber) throws IOException {
        for (int i = 0; i != board.getTheFirstPlayerKnuckles().size(); i++) {
            if (board.getTheFirstPlayerKnuckles().get(i).getOnePart() == firstNumber && board.getTheFirstPlayerKnuckles().get(i).getSecondPart() == firstNumber) {
                whichTurn = false;
                whatNumToStart = firstNumber;
                return;
            } else if (board.getTheSecondPlayerKnuckles().get(i).getOnePart() == firstNumber && board.getTheSecondPlayerKnuckles().get(i).getSecondPart() == firstNumber) {
                whichTurn = true;
                whatNumToStart = firstNumber;
                return;
            }
        }
        if (firstNumber < 7) {
            WhoIsFirst(firstNumber + 1);
        } else {
            throw new IOException("Ни у кого нет стартовой кости, нужна пересдача");
        }
    }

    // Метод, устанавливающий первую костяшку на игровое поле
    // Первый ход "вынужденный", поэтому программа знает какой будет сделан ход
    private void setFirstKnuckle() {
        Knuckle knuckle = new Knuckle(whatNumToStart, whatNumToStart);
        if (whichTurn == false) {
            for (int i = 0; i != board.getTheFirstPlayerKnuckles().size(); i++) {
                if (board.getTheFirstPlayerKnuckles().get(i).getOnePart() == knuckle.getOnePart() && board.getTheFirstPlayerKnuckles().get(i).getSecondPart() == knuckle.getSecondPart()) {
                    sequence.add(knuckle);
                    board.getTheFirstPlayerKnuckles().remove(i);
                    addTurnToProtokol("turn" + knuckle.getOnePart() + " + " + knuckle.getSecondPart());
                    break;
                }
            }
        } else {
            for (int i = 0; i != board.getTheSecondPlayerKnuckles().size(); i++) {
                if (board.getTheSecondPlayerKnuckles().get(i).getOnePart() == knuckle.getOnePart() && board.getTheSecondPlayerKnuckles().get(i).getSecondPart() == knuckle.getSecondPart()) {
                    sequence.add(knuckle);
                    board.getTheSecondPlayerKnuckles().remove(i);
                    addTurnToProtokol("turn" + knuckle.getOnePart() + " + " + knuckle.getSecondPart());
                    break;
                }
            }
        }
    }

    // Метод, находящий все возможные ходы, игрока, ход которого сейчас идет
    public void WhatCanMakeMove() {
        whatCanMove.clear();
        knucklesPlayerCantMove.clear();
        if (whichTurn == false) {
            if (sequence.size() == 1) {
                for (int i = 0; i != board.getTheFirstPlayerKnuckles().size(); i++) {
                    Knuckle knuckleTemp = board.getTheFirstPlayerKnuckles().get(i);
                    if (knuckleTemp.getOnePart() == sequence.get(0).getOnePart() || knuckleTemp.getSecondPart() == sequence.get(0).getSecondPart()) {
                        whatCanMove.add(knuckleTemp);
                    } else {
                        knucklesPlayerCantMove.add(knuckleTemp);
                    }
                }
            } else {
                int left = sequence.getFirst().getOnePart();
                int right = sequence.getLast().getSecondPart();
                for (int i = 0; i != board.getTheFirstPlayerKnuckles().size(); i++) {
                    Knuckle knuckleTemp = board.getTheFirstPlayerKnuckles().get(i);
                    if (knuckleTemp.getOnePart() == left || knuckleTemp.getSecondPart() == left || knuckleTemp.getOnePart() == right || knuckleTemp.getSecondPart() == right) {
                        whatCanMove.add(knuckleTemp);
                    } else {
                        knucklesPlayerCantMove.add(knuckleTemp);
                    }
                }
            }
        } else {
            if (sequence.size() == 1) {
                for (int i = 0; i != board.getTheSecondPlayerKnuckles().size(); i++) {
                    Knuckle knuckleTemp = board.getTheSecondPlayerKnuckles().get(i);
                    if (knuckleTemp.getOnePart() == sequence.getFirst().getOnePart() || knuckleTemp.getSecondPart() == sequence.getFirst().getOnePart()) {
                        whatCanMove.add(knuckleTemp);
                    } else {
                        knucklesPlayerCantMove.add(knuckleTemp);
                    }
                }
            } else {
                int left = sequence.getFirst().getOnePart();
                int right = sequence.getLast().getSecondPart();
                for (int i = 0; i != board.getTheSecondPlayerKnuckles().size(); i++) {
                    Knuckle knuckleTemp = board.getTheSecondPlayerKnuckles().get(i);
                    if (knuckleTemp.getOnePart() == left || knuckleTemp.getSecondPart() == left || knuckleTemp.getOnePart() == right || knuckleTemp.getSecondPart() == right) {
                        whatCanMove.add(knuckleTemp);
                    } else {
                        knucklesPlayerCantMove.add(knuckleTemp);
                    }
                }
            }
        }
        // Вывод всем чем можно походить:
        System.out.println("Кости которые можно положить:");
        if (whatCanMove.size() != 0) {
            for (int i = 0; i != whatCanMove.size(); i++) {
                System.out.println(whatCanMove.get(i).getOnePart() + " + " + whatCanMove.get(i).getSecondPart());
            }
        } else {
            System.out.println("А ничего положить нельзя! Нужно идти на базар!");
        }
    }

    private void Move(int ind) {
        Knuckle knuckle = new Knuckle(whatCanMove.get(ind).getOnePart(), whatCanMove.get(ind).getSecondPart());
        int leftInt = whatCanMove.get(ind).getOnePart();
        int rightInt = whatCanMove.get(ind).getSecondPart();

        if (knuckle.getOnePart() == leftPartOfSequence) {
            knuckle.turnOver();
            sequence.addFirst(knuckle);
            indexOfStartInSequence++;
            leftPartOfSequence = knuckle.getOnePart();
        } else if (knuckle.getSecondPart() == leftPartOfSequence) {
            sequence.addFirst(knuckle);
            indexOfStartInSequence++;
            leftPartOfSequence = knuckle.getOnePart();
        } else if (knuckle.getOnePart() == rightPartOfSequence) {
            sequence.addLast(knuckle);
            rightPartOfSequence = knuckle.getSecondPart();
        } else {
            knuckle.turnOver();
            sequence.addLast(knuckle);
            rightPartOfSequence = knuckle.getSecondPart();
        }

        for (int j = 0; j != board.getTheSecondPlayerKnuckles().size(); j++) {
            if (board.getTheSecondPlayerKnuckles().get(j).getOnePart() == whatCanMove.get(ind).getOnePart() && board.getTheSecondPlayerKnuckles().get(j).getSecondPart() == whatCanMove.get(ind).getSecondPart()) {
                board.getTheSecondPlayerKnuckles().remove(j);
                addTurnToProtokol("turn: " + board.getTheSecondPlayerKnuckles().get(j).getOnePart() + " + " + board.getTheSecondPlayerKnuckles().get(j).getSecondPart());
                // Проверка победил ли игрок
                if (board.getTheSecondPlayerKnuckles().size() == 0) {
                    playOn = false;
                    return;
                }
                break;
            }
        }

        nextMove();
    }

    // Сделать ход
    public void doTurn(int par) throws Exception {
        // Определяем кости которые может положить игрок, ход которого идет сейчас
        WhatCanMakeMove();
        // Если игроку нечем ходить, то проверяем может ли он взять кость из базара
        // Если не может, то проверка может ли пойти другой, чтобы проверить создалась ли ситуация рыба
        if (whatCanMove.size() == 0) {
            //взять из маркета случайную кость
            if (board.getMarket().size() > 0) {
                takeKnuckleFromMarket();
                WhatCanMakeMove();
                if (whatCanMove.size() > 0) {
                    try {
                        Move(par);
                    } catch (Exception e) {
                        return;
                    }
                } else {
                    nextMove();
                }
            }
            // Если брать нечего проверяем может ли ходить другой игрок
            else {
                nextMove();
                WhatCanMakeMove();
                if (whatCanMove.size() == 0) {
                    // Ситуация "рыба"
                    System.out.println("Ситуация - рыба");
                    playOn = false;
                }
            }
        }
        // Если игроку есть чем ходить, то он ходит
        else {
            Move(par);
            if (!playOn) {
                throw new Exception("End of game");
            }
        }
    }

    public void computerTurn() throws Exception {
        WhatCanMakeMove();
        if (whatCanMove.size() == 0) {
            //взять из маркета случайную кость
            if (board.getMarket().size() > 0) {
                System.out.println("Берем костяшку из базара");
                takeKnuckleFromMarket();
                WhatCanMakeMove();
                if (whatCanMove.size() > 0) {
                    computerMove();
                } else {
                    nextMove();
                }
            }
            // Если брать нечего проверяем может ли ходить другой игрок
            else {
                nextMove();
                WhatCanMakeMove();
                if (whatCanMove.size() == 0) {
                    // Ситуация "рыба"
                    System.out.println("Ситуация - рыба");
                    playOn = false;
                }
            }
        }
        // Если компьютеру есть чем ходить, то он ходит
        else {
            computerMove();
            if (!playOn) {
                throw new Exception("End of game");
            }
        }
    }

    public void computerMove() {
        int rand = (int) (Math.random() * whatCanMove.size());
        Knuckle knuckle = new Knuckle(whatCanMove.get(rand).getOnePart(), whatCanMove.get(rand).getSecondPart());

        if (knuckle.getOnePart() == leftPartOfSequence) {
            knuckle.turnOver();
            sequence.addFirst(knuckle);
            indexOfStartInSequence++;
            leftPartOfSequence = knuckle.getOnePart();
        } else if (knuckle.getSecondPart() == leftPartOfSequence) {
            sequence.addFirst(knuckle);
            indexOfStartInSequence++;
            leftPartOfSequence = knuckle.getOnePart();
        } else if (knuckle.getOnePart() == rightPartOfSequence) {
            sequence.addLast(knuckle);
            rightPartOfSequence = knuckle.getSecondPart();
        } else {
            knuckle.turnOver();
            sequence.addLast(knuckle);
            rightPartOfSequence = knuckle.getSecondPart();
        }

        for (int j = 0; j != board.getTheFirstPlayerKnuckles().size(); j++) {
            if ((board.getTheFirstPlayerKnuckles().get(j).getOnePart() == knuckle.getOnePart() && board.getTheFirstPlayerKnuckles().get(j).getSecondPart() == knuckle.getSecondPart()) || (board.getTheFirstPlayerKnuckles().get(j).getOnePart() == knuckle.getSecondPart() && board.getTheFirstPlayerKnuckles().get(j).getSecondPart() == knuckle.getOnePart())) {
                board.getTheFirstPlayerKnuckles().remove(j);
                addTurnToProtokol("turn: " + board.getTheSecondPlayerKnuckles().get(j).getOnePart() + " + " + board.getTheSecondPlayerKnuckles().get(j).getSecondPart());
                // Проверка победил ли компьютер
                if (board.getTheFirstPlayerKnuckles().size() == 0) {
                    playOn = false;
                    return;
                }
                break;
            }
        }
        nextMove();
    }

    public void takeKnuckleFromMarket() {
        int rand = (int) (Math.random() * board.getMarket().size());
        Knuckle tempKnuckle = board.getMarket().get(rand);
        board.getMarket().remove(rand);
        System.out.println("Случайная костяшка из базара, которую \"выбрал\" пользователь: ");
        System.out.println(tempKnuckle.getOnePart() + " + " + tempKnuckle.getSecondPart());
        String strTurn = "knuckle taken from market: " + tempKnuckle.getOnePart() + " + " + tempKnuckle.getSecondPart();
        addTurnToProtokol(strTurn);
        if (whichTurn == false) {
            board.getTheFirstPlayerKnuckles().add(tempKnuckle);
        } else {
            board.getTheSecondPlayerKnuckles().add(tempKnuckle);
        }
    }

    public void showCurrentGame() {
        System.out.println("Последовательность из костей: ");
        for (int i = 0; i != sequence.size(); i++) {
            System.out.print("-|" + sequence.get(i).getOnePart() + "|" + sequence.get(i).getSecondPart() + "|-");
        }
        System.out.println();

        System.out.println("Кости у первого игрока: ");
        for (int i = 0; i != board.getTheFirstPlayerKnuckles().size(); i++) {
            System.out.print(board.getTheFirstPlayerKnuckles().get(i).getOnePart() + " + " + board.getTheFirstPlayerKnuckles().get(i).getSecondPart() + " ; ");
        }
        System.out.println();

        System.out.println("Кости у второго игрока: ");
        for (int i = 0; i != board.getTheSecondPlayerKnuckles().size(); i++) {
            System.out.print(board.getTheSecondPlayerKnuckles().get(i).getOnePart() + " + " + board.getTheSecondPlayerKnuckles().get(i).getSecondPart() + " ; ");
        }
        System.out.println();
    }

    public boolean isPlayOn() {
        return playOn;
    }

    public void nextMove() {
        whichTurn = !whichTurn;
    }

    public int[] scoringPoints() {
        int sumOfPointsOfFirstPlayer = 0;
        int sumOfPointsOfSecondPlayer = 0;
        if (board.getTheFirstPlayerKnuckles().size() != 0) {
            for (int i = 0; i != board.getTheFirstPlayerKnuckles().size(); i++) {
                sumOfPointsOfFirstPlayer += board.getTheFirstPlayerKnuckles().get(i).getOnePart();
                sumOfPointsOfFirstPlayer += board.getTheFirstPlayerKnuckles().get(i).getSecondPart();
            }
        }
        if (board.getTheSecondPlayerKnuckles().size() != 0) {
            for (int i = 0; i != board.getTheSecondPlayerKnuckles().size(); i++) {
                sumOfPointsOfSecondPlayer += board.getTheSecondPlayerKnuckles().get(i).getOnePart();
                sumOfPointsOfSecondPlayer += board.getTheSecondPlayerKnuckles().get(i).getSecondPart();
            }
        }
        System.out.println("Очки первого игрока: " + sumOfPointsOfFirstPlayer);
        System.out.println("Очки второго игрока: " + sumOfPointsOfSecondPlayer);
        int points[] = new int[2];
        points[0] = sumOfPointsOfFirstPlayer;
        points[1] = sumOfPointsOfSecondPlayer;
        return points;
    }

    public LinkedList<Knuckle> getSequence() {
        return sequence;
    }

    public Board getBoard() {
        return board;
    }

    public ArrayList<Knuckle> getWhatCanMove() {
        return whatCanMove;
    }

    public int getIndexOfStartInSequence() {
        return indexOfStartInSequence;
    }

    public ArrayList<Knuckle> getKnucklesPlayerCantMove() {
        return knucklesPlayerCantMove;
    }


    public boolean isWhichTurn() {
        return whichTurn;
    }

    public String getProtokol() {
        return protokol;
    }

    public void addTurnToProtokol(String strTurn) {
        String whatToAdd;
        if (!whichTurn) {
            whatToAdd = "Computer turn: ";
        } else {
            whatToAdd = "Player turn: ";
        }
        whatToAdd += "\n";
        whatToAdd += strTurn;
        whatToAdd += "\n";
        protokol += whatToAdd;
        protokol += '\n';

    }

    public void setAllParametrs(LinkedList<Knuckle> sequence, ArrayList<Knuckle> market, ArrayList<Knuckle> firstPlayerKnuckles, ArrayList<Knuckle> secondPlayerKnuckles, int indexOfStartInSequence) {
        this.sequence = sequence;
        this.board.setMarket(market);
        this.board.setTheFirstPlayerKnuckles(firstPlayerKnuckles);
        this.board.setTheSecondPlayerKnuckles(secondPlayerKnuckles);
        this.indexOfStartInSequence = indexOfStartInSequence;
        whichTurn = true;
        this.showCurrentGame();
    }
}