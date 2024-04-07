package org.example;
import java.io.*;
import java.util.*;

public class LocalSearch {

    private AssignmentProblem problem; // конкретная задача на минимум
    private int rec; // рекорд, которого мы достигли
    private ArrayList<Integer> recPi; // решение рекорда
    private static int seed = 9; // для генерации псевдослучайных чисел
    private int pSwap;
    private int pInvert;
    private int pShuffle;
    private int pInsert;
    private int sum;



    public LocalSearch(AssignmentProblem problem) throws FileNotFoundException {
        this.problem = problem;
        recPi = new ArrayList<>();
        for(int i=0; i < problem.costArray.length; i++){
            recPi.add(i, i);
        }

        rec = problem.function(recPi);

        Scanner s = new Scanner(new File("D:\\ярлыкиРабочегоСтола\\3course\\courceWork\\localSearch\\src\\main\\java\\org\\example\\statistic.txt"));
        pSwap = s.nextInt();
        pInvert = s.nextInt();
        pShuffle = s.nextInt();
        pInsert = s.nextInt();
        sum = s.nextInt();

    }



    // применение случайных окрестностей
    public void localSearch(BufferedWriter out) throws IOException {
        for (int count=0; count <8000; count++){
            double choice = ((new Random()).nextDouble(1.0));
            if (choice < ((double)pSwap/sum) ) {
                swap(out, count);
            }
            else if (choice < ( ((double)pSwap+pInvert) /sum)) {
                invert(out, count);
            }
            else if (choice < ( ((double)pSwap+pInvert+pShuffle) /sum)) {
                shuffle(out, count);
            }
            else {
                insert(out, count);
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
            pSwap++;
            sum++;
        }
    }

    // переворачивает массив(меняет местами индексы 1ый становится последним и т.д.)
    private void invert(BufferedWriter out, int count) throws IOException {
        ArrayList<Integer> currentPi = (ArrayList<Integer>) recPi.clone();
        Collections.reverse(currentPi);
        if (rec < problem.function(currentPi)){
            recPi = currentPi;
            rec = problem.function(recPi);
            out.write(count + ". invert ");
        //    out.write(recPi.toString() + " ");
            out.write(rec + "   ");
            pInvert++;
            sum++;
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
            pShuffle++;
            sum++;
        }
    }



    // тут все отлично работает
    // вытаскивает подпоследовательность и вставляет ее в случайное место
    private void insert(BufferedWriter out, int count) throws IOException {
        int s1 = ((new Random()).nextInt(recPi.size()));
        int s2 = s1;
        while(s1 == s2) {
            s2 = (s1 + ((new Random()).nextInt(recPi.size() - 1))) % recPi.size();
            seed++;
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
            pInsert++;
            sum++;
        }
    }

    private void updateStatic(){
        try(BufferedWriter out = new BufferedWriter(new FileWriter(new File("D:\\ярлыкиРабочегоСтола\\3course\\courceWork\\localSearch\\src\\main\\java\\org\\example\\statistic.txt")))) {
            out.write(pSwap + " " + pInvert + " " + pShuffle + " " + pInsert + " " + sum);
        }catch(IOException e) { System.out.println(e.getMessage()); }
    }
}
