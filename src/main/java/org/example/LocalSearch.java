package org.example;
import java.io.*;
import java.util.*;

public class LocalSearch {

    public LinearAssignmentProblem problem; // конкретная задача на МАКСИМУМ
    private ArrayList<Integer> pi; // текущее решение
    private ArrayList<Integer> recPi; // решение рекорда
    public int rec; // рекорд, которого мы достигли

    private int step; // шаг на котором мы сейчас находимся
    private int stepSuccsess; // шаг, на котором был последний переход к более хорошему решению

    private ArrayList<Double> p; // лист вероятностей применения окрестностей
    private ArrayList<Integer> wins; // количество успешных применений окрестностей
    private ArrayList<Integer> sum; // количества всех применений окрестностей

    private double errorRate; // погрешность
    public int minRecPi = 1000000; // минимальное значение функции

    // конструктор поиска
    public LocalSearch(LinearAssignmentProblem problem) {
        //System.out.println("LocalSearchKonstructor");
        this.problem = problem;
        recPi = new ArrayList<>();
        pi = new ArrayList<>();
        wins = new ArrayList<>();
        for (int i=0; i<5; i++){
            wins.add(1);
        }

        sum = new ArrayList<>();
        for (int i=0; i<5; i++){
            sum.add(1);
        }
        p = new ArrayList<>();
    }

    // генерация случайного начального решения
    public void generateRandomPi(){
        //System.out.println("generateRandomPi");
        if ( !pi.isEmpty() && pi != null ) {pi.clear(); }
        for (int i=0; i<problem.getN(); i++){
            pi.add(i);
        }
        Collections.shuffle(pi);
        updateRec();
        if ( problem.function(pi) < minRecPi) { minRecPi = problem.function(pi); }
    }

    // задание начального решения
    private void setRandomPi(ArrayList<Integer> oldPi){
        //System.out.println("setRandomPi");
        if ( !pi.isEmpty() && pi != null ) {pi.clear(); }
        for (int i=0; i<oldPi.size(); i++){
            pi.add(i, oldPi.get(i));
        }
        updateRec();
        minRecPi = 1000000;
    }

    // генерация решения без обновления рекорда(нужна для перезапуска)
    public void generateRandomPiWithoutUpdateRec(){
        //System.out.println("generateRandomPiForRestart");
        if ( !pi.isEmpty() && pi != null ) {pi.clear(); }
        for (int i=0; i<problem.getN(); i++){
            pi.add(i);
        }
        Collections.shuffle(pi);
        if ( problem.function(pi) < minRecPi) { minRecPi = problem.function(pi); }
    }

    // генерация умного начального решения
    public void generateSmartPi(){
        //System.out.println("generateSmartPi");
        if ( !pi.isEmpty() && pi != null) {pi.clear(); }
        pi = problem.generateSmartStart();
        updateRec();
        if ( problem.function(pi) < minRecPi) { minRecPi = problem.function(pi); }
    }

    // обновление рекорда
    private void updateRec(){
        //System.out.println("updateRec");
        if ( !recPi.isEmpty() ) { recPi.clear(); }
        Collections.addAll(pi);
        rec = problem.function(pi);
    }

    // обработка локальной точки
    private void processPi(int n){
        //System.out.println("processPi");
        if (n>5 || n<0) { throw new IllegalArgumentException("Окрестность с таким номером не существует"); }
        if (problem.function(pi) > rec){
            updateRec();
            Integer change = wins.get(n) +1;
            wins.set(n, change);
            change = sum.get(n) + 1;
            sum.set(n, change);
            if ( step > 1000 ){
                stepSuccsess = step;
            }
        }
        if (problem.function(pi) < rec){
            Integer change = sum.get(n) + 1;
            sum.set(n, change);
        }
    }

    // применение окрестностей с вычисленными вероятностями
    private void localSearch(){
        double choice = ((new Random()).nextDouble(1.0));
        if (choice < p.get(0)) {
            for (int i = 0; i < 10; i++) {
                pi = swap(this.pi);
                processPi(0);
            }
        } else if (choice < p.get(0) + p.get(1)) {
            for (int i = 0; i < 10; i++) {
                pi = invert(this.pi);
                processPi(1);
            }
        } else if (choice < p.get(0) + p.get(1) + p.get(2)) {
            for (int i = 0; i < 10; i++) {
                pi = shuffle(this.pi);
                processPi(2);
            }
        } else if (choice < p.get(0) + p.get(1) + p.get(2) + p.get(3)) {
            for (int i = 0; i < 10; i++) {
                pi = insertP(this.pi);
                processPi(3);
            }
        } else {
            for (int i = 0; i < 10; i++) {
                pi = insert(this.pi);
                processPi(4);
            }
        }
    }

    // обучение
    public void training() {
        generateRandomPi();
        calculationP();
        for (int stepLS=0; stepLS < 20000; stepLS = stepLS+10) {
            localSearch();
        }
    }

    // поиск решения методом локального поиска с перезапуском
    public void localSearchRestart() {
        //System.out.println("localSearchRestart");
        generateSmartPi();
        calculationP();
        step = 0;
        stepSuccsess = 1000;
        while (step <= 2 * stepSuccsess) {
            localSearch();
            step = step + 10;
            if (step%100 == 0){
                calculationP();
            }
        }
        generateRandomPiWithoutUpdateRec();
        for (int stepRestart = 0; stepRestart < 1500; stepRestart = stepRestart + 10) {
            localSearch();
            if (stepRestart%100 == 0){
                calculationP();
            }
        }

        for (int k = 0; k < 100; k++) {
            pi = insert(this.pi);
            processPi(0);
        }

    }

    // поиск решения методом локального поиска с заданным количеством шагов(20000), начальное решение - случайное
    public void localSearch20000RandomStart() {
        //System.out.println("localSearch20000RandomStart");
        generateRandomPi();
        calculationP();
        for (int stepLS=0; stepLS < 20000; stepLS = stepLS+10) {
            localSearch();
            if (stepLS%100 == 0){
                calculationP();
            }
        }
    }

    // поиск решения методом локального поиска с заданным количеством шагов(20000), начальное решение - "умное"
    public void localSearch20000SmartStart() {
        //System.out.println("localSearch20000RandomStart");
        generateSmartPi();
        calculationP();
        for (int stepLS=0; stepLS < 20000; stepLS = stepLS+10) {
            localSearch();
            if (stepLS%100 == 0){
                calculationP();
            }
        }
    }

    // меняет местами 2 случайных элемента массива
    private ArrayList<Integer> swap( ArrayList<Integer> pi ) {
        //System.out.println("Swap");
        ArrayList<Integer> currentPi = (ArrayList<Integer>) pi.clone();
        int i1 = ((new Random()).nextInt(currentPi.size()));
        int i2 = i1;
        while(i1 == i2) {
            i2 = (i1 + ((new Random()).nextInt(currentPi.size() - 1))) % currentPi.size();
        }
        Collections.swap(currentPi, i1, i2);
        return currentPi;
    }

    // переворачивает подпоследовать(меняет местами индексы 1ый становится последним и т.д.)
    private ArrayList<Integer> invert( ArrayList<Integer> pi ) {
        //System.out.println("invert");
        ArrayList<Integer> currentPi = (ArrayList<Integer>) pi.clone();
        int subLen = ((new Random()).nextInt(currentPi.size())); // длина вытаскиваемой подпоследовательности
        while(subLen < 2) { subLen = ((new Random()).nextInt(currentPi.size())); }
        int firstIndex = ((new Random()).nextInt(currentPi.size() - subLen + 1)); // индекс первого элемента подпоследовательности в исходной последовательности
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
        int subLen = ((new Random()).nextInt(currentPi.size())); // длина вытаскиваемой подпоследовательности
        while(subLen < 2) { subLen = ((new Random()).nextInt(currentPi.size())); }
        int firstIndex = ((new Random()).nextInt(currentPi.size() - subLen + 1)); // индекс первого элемента подпоследовательности в исходной последовательности
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
        ArrayList<Integer> currentPi = (ArrayList<Integer>) pi.clone();
        int subLen = ((new Random()).nextInt(currentPi.size())); // длина вытаскиваемой подпоследовательности
        while(subLen < 2) { subLen = ((new Random()).nextInt(currentPi.size())); }
        int firstIndex = ((new Random()).nextInt(currentPi.size() - subLen + 1)); // индекс первого элемента подпоследовательности в исходной последовательности
        ArrayList<Integer> subCurrentPi = new ArrayList<>(); // вытаскиваем подпоследовательность
        for (int i=firstIndex; i<firstIndex+subLen-1; i++){
            subCurrentPi.add( currentPi.get(i) );
        }
        currentPi.removeAll(subCurrentPi);
        int newPlace = ((new Random()).nextInt(currentPi.size())); // индекс первого элемента подпоследовательности в исходной последовательности
        currentPi.addAll(newPlace, subCurrentPi);
        return currentPi;
    }

    // вытаскивает элемент и вставляет его в случайное место
    private ArrayList<Integer> insert( ArrayList<Integer> pi ) {
        //System.out.println("insert");
        int indexChange = ((new Random()).nextInt(pi.size())); // индекс элемента который будем переставлять
        ArrayList<Integer> currentPi = (ArrayList<Integer>) pi.clone();
        Integer element = currentPi.get(indexChange);
        currentPi.remove(indexChange);
        int p = ((new Random()).nextInt(currentPi.size())); // позиция куда вставим элемент
        currentPi.add(p, element);
        return currentPi;
    }

    // исследование одной из окрестностей с заданным количеством итераций
   public double researchNeighborhood(int numberSteps, int numNeighborhood){
        generateRandomPi();
        for (int i = 0; i < numberSteps; i++) {
            switch (numNeighborhood){
                case (0): pi = swap(this.pi);
                case (1): pi = invert(this.pi);
                case (2): pi = shuffle(this.pi);
                case (3): pi = insertP(this.pi);
                case (4): pi = insert(this.pi);
            }
            processPi(numNeighborhood);
        }
        return getErrorRate();
    }


    // исследование всех окрестностей с одинаковым начальным решением
   public void researchAllNeighborhood(int numberSteps, int numberWrite) throws IOException {
        String folder = "D:\\ярлыкиРабочегоСтола\\3course\\courceWork\\localSearch\\src\\main\\java\\org\\example\\\\problems\\exp\\";
        try (BufferedWriter outResearch = new BufferedWriter(new FileWriter( folder + "researchNeighborhood.txt"))) {
            outResearch.write("Итерация Окрестность Погрешность");

            generateRandomPi();
            ArrayList<Integer> researchStartPi = (ArrayList<Integer>) pi.clone();
            for (int i = 0; i <= numberSteps; i++) {
                pi = swap(this.pi);
                processPi(0);
                if (i % numberWrite == 0) {
                    outResearch.newLine();
                    outResearch.write(i + " swap " + getErrorRate());
                }
            }

            setRandomPi(researchStartPi);
            for (int i = 0; i <= numberSteps; i++) {
                pi = invert(this.pi);
                processPi(1);
                if (i % numberWrite == 0) {
                    outResearch.newLine();
                    outResearch.write(i + " invert " + getErrorRate());
                }
            }


            setRandomPi(researchStartPi);
            for (int i = 0; i <= numberSteps; i++) {
                pi = shuffle(this.pi);
                processPi(2);
                if (i % numberWrite == 0) {
                    outResearch.newLine();
                    outResearch.write(i + " shuffle " + getErrorRate());
                }
            }

            setRandomPi(researchStartPi);
            for (int i = 0; i <= numberSteps; i++) {
                pi = insertP(this.pi);
                processPi(3);
                if (i % numberWrite == 0) {
                    outResearch.newLine();
                    outResearch.write(i + " insertP " + getErrorRate());
                }
            }

            setRandomPi(researchStartPi);
            for (int i = 0; i <= numberSteps; i++) {
                pi = insert(this.pi);
                processPi(4);
                if (i % numberWrite == 0) {
                    outResearch.newLine();
                    outResearch.write(i + " insert " + getErrorRate());
                }
            }

        }catch (IOException e) { System.out.println(e.getMessage());}
    }

    // подсчет погрешности(применим для задач, у которых известно точное решение)
   public double getErrorRate(){
        //System.out.println("getErrorRate");
       if (problem.fPi == -1 ){ return -1; }
        return 100*(problem.fPi - rec)/(double)problem.fPi;

    }

    // вычисление вероятностей использования окрестностей
   private void calculationP(){
        //System.out.println("calculationP");
        if ( !p.isEmpty() ) { p.clear(); }
        double pSum = 0;
        for (int i=0; i<5; i++){
            p.add( (double)wins.get(i)/sum.get(i) );
            pSum += p.get(i);
        }
        for (int i=0; i<5; i++){
            p.set( i, p.get(i)/pSum );
        }
    }
}
