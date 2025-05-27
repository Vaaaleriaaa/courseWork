/*package org.example;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class QuadraticAbstractAssignmentProblem extends AbstractAssignmentProblem {

    public int[][] cArray; // матрица расстояний между зданиями
    public int[][] bArray; // матрица объема продукции, транспортируемой между цехами
    public List<Integer> pi; // оптимальное решение
    public int fPi;


    // загрузка задачи из файла
    // В файле первая строка - размер задачи, оптимальное решение
    //         вторая строка - само решение
    //         дальше идет матрица расстояний, после матрица объема продукции
    public QuadraticAbstractAssignmentProblem(File file){
        try(Scanner scanner = new Scanner(file)) {
            n = scanner.nextInt();
            if (n <= 0) {
                throw new IllegalArgumentException("n <= 0");
            }
            fPi = scanner.nextInt();
            super.setN(n);
            setfPi(fPi);
            pi = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                pi.add(scanner.nextInt() - 1);
            }
            cArray = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    cArray[i][j] = scanner.nextInt();
                }
            }
            bArray = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    bArray[i][j] = scanner.nextInt();
                }
            }
        }catch (IOException e) { System.out.println(e.getMessage()); }
    }

    public void setfPi(int fPi) {
        this.fPi = fPi;
    }

    @Override
    public int function(List<Integer> list) {
        int f=0;
        for (int i=0; i<n; i++){
                for (int j=0; j<n; j++) {
                    f += cArray[i][j] * bArray[list.get(i)][list.get(j)];
            }
        }
        return f;
    }

    @Override
    public ArrayList<Integer> generateSmartStart() {
        ArrayList<Integer> pi = new ArrayList<>(n);
        for (int i=0; i<n; i++){
            pi.add(i);
        }
        Collections.shuffle(pi);
        return pi;
    }
}

 */