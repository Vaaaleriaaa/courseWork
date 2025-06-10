package org.example;

import org.apache.commons.math3.distribution.ParetoDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;

import java.util.Random;

public class GeneratorDistribution {
    private PoissonDistribution poisson20;
    private PoissonDistribution poisson40;
    private PoissonDistribution poisson1;



    public GeneratorDistribution(){
        poisson20 = new PoissonDistribution(20);  // инициализируем распределение Пуассона для случайных величин
        poisson40 = new PoissonDistribution(40);  // инициализируем распределение Пуассона для случайных величин
        poisson1 = new PoissonDistribution(1);  // инициализируем распределение Пуассона для случайных величин
    }
    /*
    // Распределение Паретто(одно из распределений с тяжелыми хваостами)
    public static int generateParetto(double shape, int n) {
        return (int) new ParetoDistribution(scale, shape).sample() % n;
        // return (int) Math.round( new ParetoDistribution(scale, shape).sample() % n);
    }

     */

    public int generatePoisson20(int upBound){ return poisson20.sample() % upBound; }
    public int generatePoisson40(int upBound){ return poisson40.sample() % upBound; }

    public int generateNormal(int upBound){
        return new Random().nextInt(upBound);
    }



}
