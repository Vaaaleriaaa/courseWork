package org.example;

import java.io.*;
import java.util.*;

public class LocalSearch {

    public AbstractAssignmentProblem problem; // конкретная задача
    public ArrayList<Integer> pi; // текущее решение
    private ArrayList<Integer> winnerPi; // решение победителя
    private int winner; // Значение целевой функции решения победителя
    public ArrayList<Integer> recPi; // решение рекорда
    public int rec; // рекорд, которого мы достигли

    boolean restart;
    private ArrayList<Integer> recRestartPi; // решение рекорда
    public int recRestart; // рекорд, которого мы достигли
    private final int neighborhoodCount = 7;  // количество используемых окрестностей
    public int step; // шаг на котором мы сейчас находимся, при работе алгоритма
    int stepRestart;
    private int stepNeighborhood; // шаг на котором мы сейчас находимся, при использовании окрестности

    private int stepSuccess; // шаг, на котором был последний переход к более хорошему решению
    private final int stepSuccessStart = 100000; // шаг, на котором был последний переход к более хорошему решению

    private int stepsInNeighborhood; // количество попыток сделать шаг в окрестности

    private ArrayList<Double> p; // лист вероятностей применения окрестностей
    private ArrayList<Integer> wins; // количество успешных применений окрестностей
    private int sum; // количества всех применений окрестностей

    private final GeneratorDistribution generatorDistribution;
    private static double shape = 1.5; // начально значение степени для использования распределения с тяжелыми хвостами
    private static double shapeChange = 0.2;
    private static double stepShapeSuccess = 0;


    // Фиксируем время потраченное на решение
    private long startTime; // = System.currentTimeMillis(); // Начало (мс)
    private long endTime; // = System.currentTimeMillis(); // Конец (мс)
    public long wall_time_ls; // Полное время затраченное на решение

    public long timeout = 30 * 1000; // ограничение на время (1 с = 1000 мс) (мс)

    // конструктор поиска
    public LocalSearch(AbstractAssignmentProblem problem) {
        //System.out.println("LocalSearch");
        this.problem = problem;  // фиксируем решаемую задачу

        recPi = new ArrayList<>();  // инициализируем переменную хранящую решение рекорда
        pi = new ArrayList<>();  // инициализируем переменную хранящую текущее решение
        winnerPi = new ArrayList<>();  // инициализируем переменную хранящую решение победителя

        wins = new ArrayList<>();  // инициализируем переменную хранящую победы окрестностей
        for (int i=0; i<neighborhoodCount; i++){
            wins.add(1);
        }

        sum = neighborhoodCount;  // инициализируем сумму применения окрестностей как количество окрестностей
        p = new ArrayList<>();  // инициализируем список вероятностей применения окрестностей

        generatorDistribution = new GeneratorDistribution();

        // было 500000 для конфликтных комбинаций
        // было 100000 для учета должностей
        stepSuccess = stepSuccessStart;  // инициализируем количество итераций, по прошествии которых, возможен перезапуск поиска
        stepsInNeighborhood = 20;
    }

    // генерация случайного начального решения
    public void generateRandomPi(){
        //System.out.println("generateRandomPi");
        if ( !pi.isEmpty() ) {pi.clear(); }  // если текущее решение существует, удаляем
        for (int i=0; i<problem.getN(); i++){  // заполняем текущее решение числами от 0 до n
            pi.add(i);
        }
        Collections.shuffle(pi);  // применяем shuffle к текущему решению
        updateRec(pi);  // обновляем рекорд
    }

    // генерация решения без обновления рекорда(нужна для перезапуска)
    public void generateRandomPiWithoutUpdateRec(){
        //System.out.println("generateRandomPiForRestart");
        if ( !pi.isEmpty() ) {pi.clear(); }
        for (int i=0; i<problem.getN(); i++){
            pi.add(i);
        }
        Collections.shuffle(pi);
    }

    // генерация решения без обновления рекорда(нужна для перезапуска)
    public void generateSolverPiWithoutUpdateRec(){
        //System.out.println("generateRandomPiForRestart");
        if ( !pi.isEmpty() ) {pi.clear(); }
        pi = problem.generateSolverStart();
        if ((problem.getMax() && (problem.function(pi) > rec)) ||
                (!problem.getMax() && (problem.function(pi) < rec))) {
            updateRec(pi);
        }
    }

    // генерация умного начального решения
    public void generateSmartPi(){
        //System.out.println("generateSmartPi");
        if ( !pi.isEmpty() ) {pi.clear(); }
        pi = problem.generateSmartStart();
        updateRec(pi);
    }

    // генерация начального решения с помощью солвера
    public void generateSolverPi(){
        //System.out.println("generateSolverPi");
        if ( !pi.isEmpty() ) {pi.clear(); }
        pi = problem.generateSolverStart();
        updateRec(pi);
    }

    // обновление рекорда
    private void updateRec( ArrayList<Integer> newRec){
        //System.out.println("updateRec");
        if (!restart) {
            if (!recPi.isEmpty()) {
                recPi.clear();
            }
            recPi.addAll(newRec);
            rec = problem.function(recPi);
        }
        else {
            if (!recRestartPi.isEmpty()) {
                recRestartPi.clear();
            }
            recRestartPi.addAll(newRec);
            recRestart = problem.function(recPi);
        }
    }

    // обновление победителя
    private void updateWinner(ArrayList<Integer> newRec){
        //System.out.println("updateWinner");
        if ( !winnerPi.isEmpty() ) { winnerPi.clear(); }
        winnerPi.addAll(newRec);
        winner = problem.function(winnerPi);
    }

    // обновление решения
    private void updatePi(ArrayList<Integer> winnerPi){
        //System.out.println("updateWinner");
        if ( !pi.isEmpty() ) { pi.clear(); }
        this.pi.addAll(winnerPi);
        this.winner = problem.function(winnerPi);
    }

    // обработка локальной точки, (1+1)ES
    private boolean processPiRec(int neighborhoodN){
        //System.out.println("processPiRec");
        if (neighborhoodN > neighborhoodCount || neighborhoodN<0) { throw new IllegalArgumentException("Окрестность с таким номером не существует"); }
        // если найденное решение лучше рекорда
        if (!restart) {
            if ((problem.getMax() && (problem.function(pi) > rec)) ||
                    (!problem.getMax() && (problem.function(pi) < rec))) {
                updateRec(pi);
                Integer change = wins.get(neighborhoodN) + 1;
                wins.set(neighborhoodN, change);
                sum++;
                if (step > stepSuccessStart) {
                    stepSuccess = step;
                }
                return true;
            }
            // если найденное решение хуже рекорда
            if ((problem.getMax() && (problem.function(pi) < rec)) ||
                    (!problem.getMax() && (problem.function(pi) > rec))) {
                pi.clear();
                pi.addAll(recPi);
            }
            return false;
        }
        else{
            if ((problem.getMax() && (problem.function(pi) > recRestart)) ||
                    (!problem.getMax() && (problem.function(pi) < recRestart))) {
                updateRec(pi);
                Integer change = wins.get(neighborhoodN) + 1;
                wins.set(neighborhoodN, change);
                sum++;
                if (step > stepSuccessStart) {
                    stepSuccess = step;
                }
                return true;
            }
            // если найденное решение хуже рекорда
            if ((problem.getMax() && (problem.function(pi) < recRestart)) ||
                    (!problem.getMax() && (problem.function(pi) > recRestart))) {
                pi.clear();
                pi.addAll(recRestartPi);
            }
            return false;

        }
    }

    // обработка локальной точки, идем с победителями
    private void processPiWin(int neighborhoodN){
        //System.out.println("processPiWin");
        if ( neighborhoodN > neighborhoodCount || neighborhoodN < 0 ) { throw new IllegalArgumentException("Окрестность с таким номером не существует"); }
        // если найденное решение лучше, чем у победителя
        if ((problem.getMax() && (problem.function(pi) > winner)) ||
                (!problem.getMax() && (problem.function(pi) < winner))) {
            updateWinner(pi);
            Integer change = wins.get(neighborhoodN) + 1;
            wins.set(neighborhoodN, change);
            sum++;
          //  shape -= shapeChange;
         //   checkShape();
            if ( step > stepSuccessStart ){
                stepSuccess = step;
            }
        }
    }

    // применение окрестностей с вычисленными вероятностями, стратегия (1+1)ES
    private int localSearch1p1(){
        //System.out.println("localSearch1p1");
        double choice = ((new Random()).nextDouble(1.0));
        boolean flagUpdate = false;
        int i;
        if (choice < p.get(0)) {
            for ( i = 0; !flagUpdate && i < stepsInNeighborhood; i++) {
                pi = swap(this.pi);
                flagUpdate = processPiRec(0);
            }
        } else if (choice < p.get(0) + p.get(1)) {
            for ( i = 0; !flagUpdate && i < stepsInNeighborhood; i++) {
                pi = invert(this.pi);
                flagUpdate = processPiRec(1);
            }
        } else if (choice < p.get(0) + p.get(1) + p.get(2)) {
            for ( i = 0; !flagUpdate && i < stepsInNeighborhood; i++) {
                pi = shuffle(this.pi);
                flagUpdate = processPiRec(2);
            }
        } else if (choice < p.get(0) + p.get(1) + p.get(2) + p.get(3)) {
            for ( i = 0; !flagUpdate && i < stepsInNeighborhood; i++) {
                pi = insertP(this.pi);
                flagUpdate = processPiRec(3);
            }
        } else if (choice < p.get(0) + p.get(1) + p.get(2) + p.get(3) + p.get(4)) {
            for ( i = 0; !flagUpdate && i < stepsInNeighborhood; i++) {
                pi = insert(this.pi);
                flagUpdate = processPiRec(4);
            }
        } else if (choice < p.get(0) + p.get(1) + p.get(2) + p.get(3)+ p.get(4)+ p.get(5)) {
            for ( i = 0; !flagUpdate && i < stepsInNeighborhood; i++) {
                pi = swapK(this.pi);
                flagUpdate = processPiRec(5);
            }
        } else {
            for ( i = 0; !flagUpdate && i < stepsInNeighborhood; i++) {
                pi = insertK(this.pi);
                flagUpdate = processPiRec(6);
            }
        }

        //// ВРЕМЕННЫЕ ВЫВОДЫ
        System.out.println("Step " + step);
        System.out.println("Rec " + rec);
        //// ВРЕМЕННЫЕ ВЫВОДЫ

        return i;
    }

    // применение окрестностей с вычисленными вероятностями, стратегия иди с победителями
    private void localSearchWinners(){
        //System.out.println("localSearchWinners");
        winner = problem.function(pi);
        winnerPi.clear();
        winnerPi.addAll(pi);
        double choice = ((new Random()).nextDouble(1.0));
        if (choice < p.get(0)) {
            for (int i = 0; i < stepsInNeighborhood; i++) {
                pi = swap(this.pi);
                processPiWin(0);
            }
        } else if (choice < p.get(0) + p.get(1)) {
            for (int i = 0; i < stepsInNeighborhood; i++) {
                pi = invert(this.pi);
                processPiWin(1);
            }
        } else if (choice < p.get(0) + p.get(1) + p.get(2)) {
            for (int i = 0; i < stepsInNeighborhood; i++) {
                pi = shuffle(this.pi);
                processPiWin(2);
            }
        } else if (choice < p.get(0) + p.get(1) + p.get(2) + p.get(3)) {
            for (int i = 0; i < stepsInNeighborhood; i++) {
                pi = insertP(this.pi);
                processPiWin(3);
            }
        } else {
            for (int i = 0; i < stepsInNeighborhood; i++) {
                pi = insert(this.pi);
                processPiWin(4);
            }
        }

        // если найденный победитель лучше, чем рекорда
        if ( (problem.getMax() && (winner > rec) ) ||
                ( !problem.getMax() && (winner < rec) ) ){
            updatePi(this.winnerPi);
        }

        if ((problem.getMax() && (problem.function(winnerPi) > problem.function(recPi))) ||
                (!problem.getMax() && (problem.function(winnerPi) < problem.function(recPi)))) {
            updateRec(winnerPi);
        }
    }

    // обучение
    public void training() {
        //System.out.println("training");
        generateRandomPi();
        calculationP();
        for (int stepLS=0; stepLS < 20000; stepLS = stepLS+100) {
            localSearch1p1();
        }
    }

    // поиск решения методом локального поиска с перезапуском с ограничением шагов
    public void localSearchRestartStepWin() {
        //System.out.println("localSearchRestartStepWin");
        // Зафиксируем время начала вычислений
        startTime = System.currentTimeMillis();

        training();

        //System.out.println("localSearchRestartStepWin");
        generateSmartPi();

        calculationP();
        step = 0;

        while ( step <= 2 * stepSuccess) {
            localSearchWinners();
            step = step + stepsInNeighborhood;
            if (step%140 == 0){
                calculationP();
                //  checkShape();
            }
        }

        for (int k = 0; k < 2000; k++) {
            pi = insert(this.pi);
            processPiRec(0);
            step++;
        }
        // Зафиксируем время окончания работы
        endTime = System.currentTimeMillis();
        wall_time_ls = endTime - startTime;
    }

    public void localSearchRestartStep1p1() {
        //System.out.println("localSearchRestartStep1p1");
        // Зафиксируем время начала вычислений
        startTime = System.currentTimeMillis();

        //training();
        calculationP();

        generateSolverPi();

        //restart = false;
        step = 0;

        while ( step <= 2 * stepSuccess) {
            stepNeighborhood = localSearch1p1();
            step = step + stepNeighborhood;
            if (step%140 == 0){
                calculationP();
              //  checkShape();
            }
        }

        /*
        generateRandomPiWithoutUpdateRec();
        recRestartPi = new ArrayList<>();
        recRestartPi.addAll(pi);
        recRestart = problem.function(recRestartPi);
        restart = true;
        stepRestart = 0;
        stepSuccess = 150000;

        while ( stepRestart <= stepSuccess) {
            stepNeighborhood = localSearch1p1();
            stepRestart = stepRestart + stepNeighborhood;
            if (stepRestart%140 == 0){
                calculationP();
            }
        }
        restart = false;
        if ( problem.getMax() && recRestart > rec ){
            rec = recRestart;
            recPi.clear();
            recPi.addAll(recRestartPi);
        }

         */


        for (int k = 0; k < 2000; k++) {
            pi = insert(this.pi);
            processPiRec(0);
            step++;
        }
        // Зафиксируем время окончания работы
        endTime = System.currentTimeMillis();
        wall_time_ls = endTime - startTime;
    }

    // скремблирование с тяжелыми хвостами
    public void localSearchScramblingHeavyTails() {
        //System.out.println("localSearchScramblingHeavyTails");
        startTime = System.currentTimeMillis(); // Зафиксируем время начала вычислений

        training();

        calculationP();
        while ( time() <= timeout) {
            generateRandomPiWithoutUpdateRec();
            for (step = 0; step < 50000; step += 100) {
                localSearchWinners();
                if (step % 1000 == 0) {
                    calculationP();
                }
            }
        }

        for (int k = 0; k < 1000; k++) {
            pi = insert(this.pi);
            processPiRec(0);
        }
        // Зафиксируем время окончания работы
        endTime = System.currentTimeMillis();
        wall_time_ls = endTime - startTime;
    }

    // поиск решения методом локального поиска с перезапуском по истечению времени
    public void localSearchRestartTime() {
        //System.out.println("localSearchRestartTime");
        // Зафиксируем время начала вычислений
        startTime = System.currentTimeMillis();

        training();

        //System.out.println("localSearchRestart");
        generateSmartPi();
        calculationP();
        step = 0;
        while ( time() <= timeout ) {
            localSearch1p1();
            step = step + 10;
            if (step%100 == 0){
                calculationP();
            }
        }
        generateRandomPiWithoutUpdateRec();
        for (int stepRestart = 0; stepRestart < 1500; stepRestart = stepRestart + 10) {
            localSearch1p1();
            if (stepRestart%100 == 0){
                calculationP();
            }
        }

        for (int k = 0; k < 100; k++) {
            pi = insert(this.pi);
            processPiRec(0);
        }
        // Зафиксируем время окончания работы
        endTime = System.currentTimeMillis();
        wall_time_ls = endTime - startTime;
    }

    // поиск решения методом локального поиска с заданным количеством шагов(20000), начальное решение - случайное
    public void localSearch20000RandomStart() {
        //System.out.println("localSearch20000RandomStart");
        // Зафиксируем время начала вычислений
        startTime = System.currentTimeMillis();

        training();

        //System.out.println("localSearch20000RandomStart");
        generateRandomPi();
        calculationP();
        for (int stepLS=0; stepLS < 20000; stepLS = stepLS+10) {
            localSearch1p1();
            if (stepLS%100 == 0){
                calculationP();
            }
        }

        // Зафиксируем время окончания работы
        endTime = System.currentTimeMillis();
        wall_time_ls = endTime - startTime;
    }

    // поиск решения методом локального поиска с заданным количеством шагов(20000), начальное решение - "умное"
    public void localSearch20000SmartStart() {
        //System.out.println("localSearch20000SmartStart");
        // Зафиксируем время начала вычислений
        startTime = System.currentTimeMillis();

        training();

        generateSmartPi();
        calculationP();
        for (int stepLS=0; stepLS < 100000; stepLS = stepLS+10) {
            localSearch1p1();
            if (stepLS%100 == 0){
                calculationP();
            }
        }

        // Зафиксируем время окончания работы
        endTime = System.currentTimeMillis();
        wall_time_ls = endTime - startTime;
    }

    // меняет местами 2 случайных элемента массива
    private ArrayList<Integer> swap( ArrayList<Integer> pi ) {
        //System.out.println("Swap");
        ArrayList<Integer> currentPi = (ArrayList<Integer>) pi.clone();

        ArrayList<Integer> invalidList = problem.getInvalidList(currentPi);
        int i1 = -1;
        if ( invalidList != null && invalidList.size() != 0){
            i1 = invalidList.get( generatorDistribution.generatePoisson20(invalidList.size()));
        }
        else { i1 = generatorDistribution.generatePoisson20(currentPi.size()); }

        int i2 = i1;
        while(i1 == i2) {
            i2 = generatorDistribution.generatePoisson20(currentPi.size());
        }
        Collections.swap(currentPi, i1, i2);
        return currentPi;
    }

    // меняет местами 2 случайных элемента массива
    private ArrayList<Integer> swapK( ArrayList<Integer> pi ) {
        //System.out.println("SwapK");
        ArrayList<Integer> currentPi = (ArrayList<Integer>) pi.clone();

        int k = generatorDistribution.generatePoisson40(problem.getN());  // количество применения swap к текущему решению
        while (k < 2){
            k = generatorDistribution.generatePoisson40(problem.getN());  // Если k < 2, то это обычный swap, который уже есть
        }

        for (int i = 0; i< k; i++) {
            ArrayList<Integer> invalidList = problem.getInvalidList(currentPi);
            int i1 = -1;
            if ( invalidList != null && invalidList.size() != 0){
                i1 = invalidList.get( generatorDistribution.generateNormal(invalidList.size()));
            }
            else { i1 = generatorDistribution.generatePoisson40(currentPi.size()); }

            int i2 = i1;
            while(i1 == i2) {
                i2 = generatorDistribution.generatePoisson40(currentPi.size());
            }
            Collections.swap(currentPi, i1, i2);
        }
        return currentPi;
    }

    // переворачивает подпоследовать(меняет местами индексы 1ый становится последним и т.д.)
    private ArrayList<Integer> invert( ArrayList<Integer> pi ) {
        //System.out.println("invert");
        ArrayList<Integer> currentPi = (ArrayList<Integer>) pi.clone();

        int firstIndex = generatorDistribution.generatePoisson40(currentPi.size()); // индекс первого элемента подпоследовательности в исходной последовательности

        while (firstIndex > 19){
            firstIndex = generatorDistribution.generatePoisson40(currentPi.size());
        }

        int subLen = generatorDistribution.generatePoisson40(currentPi.size() - firstIndex); // длина вытаскиваемой подпоследовательности
        if (firstIndex == 18){
            subLen = 2;
        }

        ArrayList<Integer> subCurrentPi = new ArrayList<>(); // вытаскиваем подпоследовательность
        for (int i=firstIndex; i<firstIndex+subLen-1; i++){
            subCurrentPi.add( currentPi.get(i) );
        }
        Collections.reverse(subCurrentPi);
        for (int i=firstIndex; i<firstIndex+subLen-1; i++){
            currentPi.set(i, subCurrentPi.get(i-firstIndex));
        }
        return currentPi;
    }

    // перемешивает элементы между собой в подпоследовательности решения
    private ArrayList<Integer> shuffle( ArrayList<Integer> pi )  {
        //System.out.println("shuffle");
        ArrayList<Integer> currentPi = (ArrayList<Integer>) pi.clone();

        int firstIndex = generatorDistribution.generatePoisson40(currentPi.size()); // индекс первого элемента подпоследовательности в исходной последовательности

        while (firstIndex > 19){
            firstIndex = generatorDistribution.generatePoisson40(currentPi.size());
        }

        int subLen = generatorDistribution.generatePoisson40(currentPi.size() - firstIndex); // длина вытаскиваемой подпоследовательности
        if (firstIndex == 18){
            subLen = 2;
        }

        ArrayList<Integer> subCurrentPi = new ArrayList<>(); // вытаскиваем подпоследовательность
        for (int i=firstIndex; i<firstIndex+subLen-1; i++){
            subCurrentPi.add( currentPi.get(i) );
        }
        Collections.shuffle(subCurrentPi);
        for (int i=firstIndex; i<firstIndex+subLen-1; i++){
            currentPi.set(i, subCurrentPi.get(i-firstIndex));
        }
        return currentPi;
    }


    // вытаскивает подпоследовательность и вставляет ее в случайное место
    private ArrayList<Integer> insertP(ArrayList<Integer> pi) {
        //System.out.println("insertP");
        ArrayList<Integer> currentPi = new ArrayList<>();
        currentPi.addAll(pi);

        int subLen = generatorDistribution.generatePoisson20(currentPi.size()/2); // длина вытаскиваемой подпоследовательности
        while(subLen < 2) {
            subLen = generatorDistribution.generatePoisson20(currentPi.size()/2);

        }

        int firstIndex = generatorDistribution.generateNormal(currentPi.size() - subLen + 1); // индекс первого элемента подпоследовательности в исходной последовательности
        ArrayList<Integer> subCurrentPi = new ArrayList<>(); // вытаскиваем подпоследовательность
        for (int i=firstIndex; i<firstIndex+subLen-1; i++){
            subCurrentPi.add( currentPi.get(i) );
        }
        currentPi.removeAll(subCurrentPi);

        int newPlace = generatorDistribution.generateNormal(currentPi.size()); // индекс первого элемента подпоследовательности в исходной последовательности
        currentPi.addAll(newPlace, subCurrentPi);
        return currentPi;
    }

    // вытаскивает элемент и вставляет его в случайное место
    private ArrayList<Integer> insert( ArrayList<Integer> pi ) {
        //System.out.println("insert");
        ArrayList<Integer> currentPi = new ArrayList<>();
        currentPi.addAll(pi);

        ArrayList<Integer> invalidList = problem.getInvalidList(currentPi);

        int indexChange = -1;  // индекс элемента который будем переставлять
        if (invalidList != null && invalidList.size() != 0){
            indexChange = invalidList.get( generatorDistribution.generateNormal(invalidList.size()));
        } else { indexChange = generatorDistribution.generateNormal(currentPi.size());}

        Integer element = currentPi.get(indexChange);
        currentPi.remove(indexChange);

        int p = generatorDistribution.generateNormal(currentPi.size()); // позиция куда вставим элемент
        currentPi.add(p, element);

        return currentPi;
    }

    // вытаскивает элемент и вставляет его в случайное место
    private ArrayList<Integer> insertK( ArrayList<Integer> pi ) {
        //System.out.println("insertK");
        int k = generatorDistribution.generatePoisson20(problem.getN()/2);
        while (k < 2) {
            k = generatorDistribution.generatePoisson20(problem.getN()/2);
        }

        ArrayList<Integer> currentPi = new ArrayList<>();
        currentPi.addAll(pi);
        for (int i = 0; i < k; i++) {
            ArrayList<Integer> invalidList = problem.getInvalidList(currentPi);

            int indexChange = -1;  // индекс элемента который будем переставлять
            if (invalidList != null && invalidList.size() != 0 ){
                indexChange = invalidList.get( generatorDistribution.generateNormal(invalidList.size()));
            } else { indexChange = generatorDistribution.generateNormal(currentPi.size());}

            Integer element = currentPi.get(indexChange);
            currentPi.remove(indexChange);

            int p = generatorDistribution.generateNormal(currentPi.size()); // позиция куда вставим элемент
            currentPi.add(p, element);
        }

        return currentPi;
    }

    // исследование одной из окрестностей с заданным количеством итераций
    public double researchNeighborhood( ArrayList<Integer> piStart, int numberSteps, int numNeighborhood){
        pi.clear();
        pi.addAll(piStart);
        recPi.clear();
        recPi.addAll(pi);
        rec = problem.function(recPi);
        for (int i = 0; i < numberSteps; i++) {
            switch (numNeighborhood){
                case 0:
                    pi = swap(this.pi);
                    break;
                case 1:
                    pi = invert(this.pi);
                    break;
                case 2:
                    pi = shuffle(this.pi);
                    break;
                case 3:
                    pi = insertP(this.pi);
                    break;
                case 4:
                    pi = insert(this.pi);
                    break;
                case 5:
                    pi = swapK(this.pi);
                    break;
                case 6:
                    pi = insertK(this.pi);
                    break;
            }
            processPiRec(numNeighborhood);
        }
        return getErrorRate();
    }

    // задание начального решения(нужно для исследования окрестностей)
    private void setRandomPi(ArrayList<Integer> oldPi){
        //System.out.println("setRandomPi");
        if ( !pi.isEmpty() ) {pi.clear(); }  // если текущее решение существует, удаляем
        for (int i=0; i<oldPi.size(); i++){
            pi.add(i, oldPi.get(i));
        }
        updateRec(pi);
    }

    // исследование всех окрестностей с одинаковым начальным решением
   public void researchAllNeighborhood(int numberSteps, int numberWrite) throws IOException {
       String folder = "src\\main\\java\\org\\example\\problems\\confComb\\n_20\\p_25_30\\";
        try (BufferedWriter outResearch = new BufferedWriter(new FileWriter( folder + "ResearchN.txt"))) {
            outResearch.write("Итерация Окрестность Погрешность");

            generateSolverPi();
            ArrayList<Integer> researchStartPi = (ArrayList<Integer>) pi.clone();
            for (int i = 0; i <= numberSteps; i++) {
                pi = swap(this.pi);
                processPiRec(0);
                if (i % numberWrite == 0) {
                    outResearch.newLine();
                    outResearch.write(i + " swap " + getErrorRate());
                }
            }

            setRandomPi(researchStartPi);
            for (int i = 0; i <= numberSteps; i++) {
                pi = invert(this.pi);
                processPiRec(1);
                if (i % numberWrite == 0) {
                    outResearch.newLine();
                    outResearch.write(i + " invert " + getErrorRate());
                }
            }

            setRandomPi(researchStartPi);
            for (int i = 0; i <= numberSteps; i++) {
                pi = shuffle(this.pi);
                processPiRec(2);
                if (i % numberWrite == 0) {
                    outResearch.newLine();
                    outResearch.write(i + " shuffle " + getErrorRate());
                }
            }

            setRandomPi(researchStartPi);
            for (int i = 0; i <= numberSteps; i++) {
                pi = insertP(this.pi);
                processPiRec(3);
                if (i % numberWrite == 0) {
                    outResearch.newLine();
                    outResearch.write(i + " insertP " + getErrorRate());
                }
            }

            setRandomPi(researchStartPi);
            for (int i = 0; i <= numberSteps; i++) {
                pi = insert(this.pi);
                processPiRec(4);
                if (i % numberWrite == 0) {
                    outResearch.newLine();
                    outResearch.write(i + " insert " + getErrorRate());
                }
            }

            setRandomPi(researchStartPi);
            for (int i = 0; i <= numberSteps; i++) {
                pi = swapK(this.pi);
                processPiRec(5);
                if (i % numberWrite == 0) {
                    outResearch.newLine();
                    outResearch.write(i + " swapK " + getErrorRate());
                }
            }

            setRandomPi(researchStartPi);
            for (int i = 0; i <= numberSteps; i++) {
                pi = insertK(this.pi);
                processPiRec(6);
                if (i % numberWrite == 0) {
                    outResearch.newLine();
                    outResearch.write(i + " insertK " + getErrorRate());
                }
            }

        }catch (IOException e) { System.out.println(e.getMessage());}
    }

    // подсчет погрешности(применим для задач, у которых известно точное решение)
   public double getErrorRate(){
       //System.out.println("getErrorRate");
       if (problem.decisionSolverOrTools < 0 ){ return -1; }
        return 100 * Math.abs(problem.decisionSolverOrTools - rec) / (double)problem.decisionSolverOrTools;

    }

    // вычисление вероятностей использования окрестностей
   private void calculationP(){
        //System.out.println("calculationP");
        if ( !p.isEmpty() ) { p.clear(); }
        for (int i=0; i<neighborhoodCount; i++){
            p.add( i, (double) wins.get(i)/(double)sum );
        }
   }

    // считаем сколько прошло времени
    public long time(){
        System.out.println("time");
        return System.currentTimeMillis() - startTime;
    }

    private void checkShape(){
       if (shape < 0.3 || shape > 3.0){
           shape = 1.5;
       }
    }

}
