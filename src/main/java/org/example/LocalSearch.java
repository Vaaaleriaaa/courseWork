package org.example;
import java.io.*;
import java.util.*;

// Задача на максимум !!!
public class LocalSearch {

    private AssignmentProblem problem; // конкретная задача на максимум
    private int rec; // рекорд, которого мы достигли
    private ArrayList<Integer> recPi; // решение рекорда

    private ArrayList<Integer> beforeRestartPi; // решение при перезапуске
    // private static int seed = 9; // для генерации псевдослучайных чисел

    private int lastUpdate = 1000; // шаг, на котором был последний переход к более хорошему решению
    private ArrayList<Double> p; // лист вероятностей применения окрестностей
    private ArrayList<Integer> wins; // количество успешных применений окрестностей
    private ArrayList<Integer> sum; // количества всех применений окрестнойстей




    public LocalSearch(AssignmentProblem problem) throws FileNotFoundException {
        this.problem = problem;
        recPi = new ArrayList<>();

        for (int i=0; i<problem.costArray.length; i++) {
            int elemetAdd = problem.costArray[i][0];
            Integer indexElementAdd = 0;
            for (int j=1; j<problem.costArray.length; j++){
                if (elemetAdd < problem.costArray[i][j] && !(recPi.contains(j))){
                    elemetAdd = problem.costArray[i][j];
                    indexElementAdd = j;
                }
            }
            recPi.add(i, indexElementAdd);
        }

        rec = problem.function(recPi);

        Scanner s = new Scanner(new File("D:\\ярлыкиРабочегоСтола\\3course\\courceWork\\localSearch\\src\\main\\java\\org\\example\\statistic.txt"));

        wins = new ArrayList<>();
        for (int i=0; i<5; i++){
            wins.add(s.nextInt());
        }

        sum = new ArrayList<>();
        for (int i=0; i<5; i++){
            sum.add(s.nextInt());
        }

        double pSum = 0;
        p = new ArrayList<>();
        for (int i=0; i<5; i++){
            p.add( (double)wins.get(i)/sum.get(i) );
            pSum += p.get(i);
        }
        for (int i=0; i<5; i++){
            p.set( i, p.get(i)/pSum );
        }

    }



    // применение случайных окрестностей
    public void localSearch(BufferedWriter out) throws IOException {
        int count = 0;
        while (count <= 2*lastUpdate){
            double choice = ((new Random()).nextDouble(1.0));
            if (choice < p.get(0) ) {
                for (int i=0; i<10;i++) {
                    swap(out, count);
                }
                count = count + 10;
            }
            else if (choice < p.get(0) + p.get(1) ) {
                invert(out, count);
                count++;
            }
            else if (choice < p.get(0) + p.get(1) + p.get(2)) {
                for (int i=0; i<10;i++) {
                    shuffle(out, count);
                }
                count = count + 10;
            }
            else if (choice < p.get(0) + p.get(1) + p.get(2) + p.get(3) ) {
                for (int i=0; i<10;i++) {
                    insertP(out, count);
                }
                count = count + 10;
            }
            else {
                for (int i=0; i<10;i++) {
                    insert(out, count);
                }
                count = count + 10;
            }
            System.out.println(count);
        }

        beforeRestartPi = new ArrayList<>(recPi);
        Collections.shuffle(recPi);

        for (int count2=0; count2 <1000; count2=count2+10){
            double choice = ((new Random()).nextDouble(1.0));
            if (choice < p.get(0) ) {
                for (int i=0; i<10;i++) {
                    swap(out, count2);
                }
                count2 = count2 + 10;
            }
            else if (choice < p.get(0) + p.get(1) ) {
                invert(out, count2);
                count2++;
            }
            else if (choice < p.get(0) + p.get(1) + p.get(2)) {
                for (int i=0; i<10;i++) {
                    shuffle(out, count2);
                }
                count2 = count2 + 10;
            }
            else if (choice < p.get(0) + p.get(1) + p.get(2) + p.get(3) ) {
                for (int i=0; i<10;i++) {
                    insertP(out, count2);
                }
                count2 = count2 + 10;
            }
            else {
                for (int i=0; i<10;i++) {
                    insert(out, count2);
                }
                count2 = count2 + 10;
            }
        }





        out.write(Boolean.toString(rec==problem.fPi));
        updateStatic();

    }



    // меняет местами 2 случайных элемента массива
    private void swap(BufferedWriter out, int count) throws IOException {
        ArrayList<Integer> currentPi = (ArrayList<Integer>) recPi.clone();
        int s1 = ((new Random()).nextInt(currentPi.size()));
        int s2 = s1;
        while(s1 == s2) {
            s2 = (s1 + ((new Random()).nextInt(currentPi.size() - 1))) % currentPi.size();
            // seed++;
        }
        Collections.swap(currentPi, s1, s2);
        if (rec < problem.function(currentPi)){
            recPi = currentPi;
            rec = problem.function(recPi);
            out.write(count + ". swap ");
    //        out.write(recPi.toString() + " ");
            out.write(rec + "   ");

            Integer change = wins.get(0) +2;
            wins.set(0, change);
            change = sum.get(0) + 2;
            sum.set(0, change);
            if (count > 1000) {
                lastUpdate = count;
            }
        }

        if (rec == problem.function(currentPi)){
            Integer change = wins.get(0) +1;
            wins.set(0, change);
            change = sum.get(0) + 2;
            sum.set(0, change);
        }
    }

    // переворачивает массив(меняет местами индексы 1ый становится последним и т.д.)
    // разворачивать подпоследовать!!!
    private void invert(BufferedWriter out, int count) throws IOException {
        ArrayList<Integer> currentPi = (ArrayList<Integer>) recPi.clone();
        Collections.reverse(currentPi);
        if (rec < problem.function(currentPi)){
            recPi = currentPi;
            rec = problem.function(recPi);
            out.write(count + ". invert ");
        //    out.write(recPi.toString() + " ");
            out.write(rec + "   ");

            Integer change = wins.get(1) +2;
            wins.set(1, change);
            change = sum.get(1) + 2;
            sum.set(1, change);
            if (count > 1000) {
                lastUpdate = count;
            }
        }

        if (rec == problem.function(currentPi)){
            Integer change = wins.get(1) +1;
            wins.set(1, change);
            change = sum.get(1) + 2;
            sum.set(1, change);
        }
    }

    // перемешивает элементы между собой в изначальной подпоследовательности
    private void shuffle(BufferedWriter out,int count) throws IOException {
        int s1 = ((new Random()).nextInt(recPi.size()));
        int s2 = s1;
        while(s1 == s2) {
            s2 = (s1 + ((new Random()).nextInt(recPi.size() - 1))) % recPi.size();
            // seed++;
        }
        if (s2 < s1) {
            int t = s2;
            s2 = s1;
            s1 = t;
        }

        List<Integer> subList = new ArrayList<>(s2-s1+1);
        for (int i = s1; i <= s2; i++){
            subList.add(recPi.get(i));
        }

        Collections.shuffle(subList);

        ArrayList<Integer> currentPi = (ArrayList<Integer>) recPi.clone();
        for (int i = s1; i <= s2; i++){
            currentPi.set(i, subList.get(0));
            subList.remove(0);
        }

        if (rec < problem.function(currentPi)){
            recPi = currentPi;
            rec = problem.function(recPi);
            out.write(count + ". shuffle ");
        //     out.write(recPi.toString() + " ");
            out.write(rec + "   ");

            Integer change = wins.get(2) +2;
            wins.set(2, change);
            change = sum.get(2) + 2;
            sum.set(2, change);
            if (count > 1000) {
                lastUpdate = count;
            }
        }

        if (rec == problem.function(currentPi)){
            Integer change = wins.get(2) +1;
            wins.set(2, change);
            change = sum.get(2) + 2;
            sum.set(2, change);
        }
    }


// тоже самое но с 1 элементом
    // тут все отлично работает
    // вытаскивает подпоследовательность и вставляет ее в случайное место
    private void insertP(BufferedWriter out, int count) throws IOException {
        int s1 = ((new Random()).nextInt(recPi.size()));
        int s2 = s1;
        while(s1 == s2) {
            s2 = (s1 + ((new Random()).nextInt(recPi.size() - 1))) % recPi.size();
            //seed++;
        }
        if (s2 < s1) {
            int t = s2;
            s2 = s1;
            s1 = t;
        }

        List<Integer> subList1 = new ArrayList<>(s2-s1+1);
        for (int i = s1; i <= s2; i++){
            subList1.add(recPi.get(i));
        }

        List<Integer> subList2 = new ArrayList<>(recPi.size()-s2+s1-1);

        for (int i=0; i <s1; i++){
            subList2.add(recPi.get(i));
        }
        for (int i=s2+1; i < recPi.size(); i++){
            subList2.add(recPi.get(i));
        }

        int p = 0;
        if (subList2.size() != 0) {
            p = ((new Random()).nextInt(subList2.size()));
        }

        ArrayList<Integer> currentPi = (ArrayList<Integer>) recPi.clone();
        for (int i = 0; i < recPi.size(); i++){
            if(i<p){
                currentPi.add(subList2.get(0));
                subList2.remove(0);
            }
            else if (subList1.size() != 0){
                currentPi.add(subList1.get(0));
                    subList1.remove(0);
            }
            else {
                currentPi.add(subList2.get(0));
                subList2.remove(0);
            }
        }

        if (rec < problem.function(currentPi)){
            recPi = currentPi;
            rec = problem.function(recPi);
            out.write(count + ". insert ");
        //    out.write(recPi.toString() + " ");
            out.write(rec + "   ");

            Integer change = wins.get(3) +2;
            wins.set(3, change);
            change = sum.get(3) + 2;
            sum.set(3, change);
            if (count > 1000) {
                lastUpdate = count;
            }
        }

        if (rec == problem.function(currentPi)){
            Integer change = wins.get(3) +1;
            wins.set(3, change);
            change = sum.get(3) + 2;
            sum.set(3, change);
        }
    }

    // вытаскивает элемент и вставляет его в случайное место
    private void insert(BufferedWriter out, int count) throws IOException {
        int s1 = ((new Random()).nextInt(recPi.size())); // индекс элемента который будем переставлять
        ArrayList<Integer> currentPi = (ArrayList<Integer>) recPi.clone();
        Integer element = currentPi.get(s1);
        currentPi.remove(s1);
        int p = ((new Random()).nextInt(currentPi.size())); // позиция куда вставим элемент
        currentPi.add(p, element);

        if (rec < problem.function(currentPi)){
            recPi = currentPi;
            rec = problem.function(recPi);
            out.write(count + ". insert ");
            //    out.write(recPi.toString() + " ");
            out.write(rec + "   ");

            Integer change = wins.get(4) +2;
            wins.set(4, change);
            change = sum.get(4) + 2;
            sum.set(4, change);
            if (count > 1000) {
                lastUpdate = count;
            }
        }

        if (rec == problem.function(currentPi)){
            Integer change = wins.get(4) +1;
            wins.set(4, change);
            change = sum.get(4) + 2;
            sum.set(4, change);
        }
    }


    private void updateStatic(){
        try(BufferedWriter out = new BufferedWriter(new FileWriter(new File("D:\\ярлыкиРабочегоСтола\\3course\\courceWork\\localSearch\\src\\main\\java\\org\\example\\statistic.txt")))) {
            for (int i=0; i<5; i++){
                out.write( wins.get(i)+ " " );
            }
            out.newLine();
            for (int i=0; i<5; i++){
                out.write( sum.get(i) + " ");
            }
        }catch(IOException e) { System.out.println(e.getMessage()); }
    }
}
