package org.example;
import com.google.ortools.linearsolver.MPSolver;

import java.util.*;

// абстрактный класс задачи о назначениях
public abstract class AbstractAssignmentProblem {
    public int n; // количество зданий и цехов
    public boolean max; // Задача на максимум(true) или на минимум(false)
    public double decisionSolverOrTools; // значение целевой функции найденное решателем библиотеки orTools
    public MPSolver.ResultStatus resultStatus; // Статус найденного решения (допустимое, оптимальное, задача некорректная и т.д.) с помощью orTools
    public long wall_time; // время, за которое solver orTools нашел решение

    public void setN(int n) {
        this.n = n;
    }

    public void setDecisionSolverOrTools(int decisionSolverOrTools) {
        this.decisionSolverOrTools = decisionSolverOrTools;
    }

    // чтобы посчитать значение целевой функции в классе LocalSearch
    public abstract int function(List<Integer> list);

    public int getN() {
        return n;
    }

    public boolean getMax() {
        return max;
    }

    // Создает умное начального решения
    public abstract ArrayList<Integer> generateSmartStart();

    // Создает начального решения с помощью солвера
    public abstract ArrayList<Integer> generateSolverStart();

}
