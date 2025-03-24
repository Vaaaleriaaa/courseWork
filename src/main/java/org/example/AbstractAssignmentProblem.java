package org.example;
import java.util.*;

// абстрактный класс задачи о назначениях
public abstract class AbstractAssignmentProblem {
    public int n; // количество зданий и цехов
    public boolean max; // Задача на максимум(true) или на минимум(false)
    public int fPi; // значение целевой функции при оптимальном решении(если оно неизвестно, то -1)

    public void setN(int n) {
        this.n = n;
    }

    public void setfPi(int fPi) {
        this.fPi = fPi;
    }

    // чтобы посчитать значение целевой функции в классе LocalSearch
    public abstract int function(List<Integer> list);

    public int getN() {
        return n;
    }

    public boolean getMax() {
        return max;
    }

    // Создает начального решения
    public abstract ArrayList<Integer> generateSmartStart();

}
