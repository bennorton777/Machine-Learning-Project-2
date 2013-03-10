package opt.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import dist.DiscreteDependencyTree;
import dist.DiscretePermutationDistribution;
import dist.DiscreteUniformDistribution;
import dist.Distribution;

import opt.SwapNeighbor;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.SwapMutation;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

/**
 * 
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class TravelingSalesmanTest {
    /** The n value */
    private static final int N = 50;
    /**
     * The test main
     * @param args ignored
     */
    public static void main(String[] args) {
        Random random = new Random();
        // create the random points
        double[][] points = new double[N][2];
        for (int i = 0; i < points.length; i++) {
            points[i][0] = random.nextDouble();
            points[i][1] = random.nextDouble();   
        }
        // for rhc, sa, and ga we use a permutation based encoding
        TravelingSalesmanEvaluationFunction ef = new TravelingSalesmanRouteEvaluationFunction(points);
        Distribution odd = new DiscretePermutationDistribution(N);
        NeighborFunction nf = new SwapNeighbor();
        MutationFunction mf = new SwapMutation();
        CrossoverFunction cf = new TravelingSalesmanCrossOver(ef);
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);


        FixedIterationTrainer fit;
        ArrayList<Run> saList=new ArrayList<Run>();
        ArrayList<Run> gaList=new ArrayList<Run>();
        ArrayList<Run> mimList=new ArrayList<Run>();
        for (int i=0;i<20;i++){
            double start, end;
            SimulatedAnnealing sa = new SimulatedAnnealing(1E12, .95, hcp);
            fit = new FixedIterationTrainer(sa, 200000);
            start=System.nanoTime();
            fit.train();
            end=System.nanoTime();
            saList.add(new Run(ef.value(sa.getOptimal()),end-start));


            StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 150, 20, gap);
            fit = new FixedIterationTrainer(ga, 1000);
            start=System.nanoTime();
            fit.train();
            end=System.nanoTime();
            gaList.add(new Run(ef.value(ga.getOptimal()),end-start));


            // for mimic we use a sort encoding
            ef = new TravelingSalesmanSortEvaluationFunction(points);
            int[] ranges = new int[N];
            Arrays.fill(ranges, N);
            odd = new  DiscreteUniformDistribution(ranges);
            Distribution df = new DiscreteDependencyTree(.1, ranges);
            ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);

            MIMIC mimic = new MIMIC(200, 100, pop);
            fit = new FixedIterationTrainer(mimic, 1000);
            start=System.nanoTime();
            fit.train();
            end=System.nanoTime();
            mimList.add(new Run(ef.value(mimic.getOptimal()),end-start));




        }
        double[] saStats = getStdDeviation(saList);
        double[] gaStats = getStdDeviation(gaList);
        double[] mimStats = getStdDeviation(mimList);
        System.out.println("Simulated Annealing:\nAverage Value: "+saStats[0]+"\nStandard Deviation Value: "+saStats[1]+"\nAverageTime: "+saStats[2]+"\nStandard Deviation Time: "+saStats[3]);
        System.out.println("\nGenetic Algorithm:\nAverage Value: "+gaStats[0]+"\nStandard Deviation Value: "+gaStats[1]+"\nAverageTime: "+gaStats[2]+"\nStandard Deviation Time: "+gaStats[3]);
        System.out.println("\nMIMIC:\nAverage Value: "+mimStats[0]+"\nStandard Deviation Value: "+mimStats[1]+"\nAverageTime: "+mimStats[2]+"\nStandard Deviation Time: "+mimStats[3]);

    }
    public static double[] getStdDeviation(ArrayList<Run> stats){
        double meanValue=0;
        double meanTime=0;
        for (Run run : stats){
            meanValue+=run.getValue();
            meanTime+=run.getTime();
        }
        meanValue/=stats.size();
        meanTime/=stats.size();
        ArrayList<Double> devValue=new ArrayList<Double>();
        ArrayList<Double> devTime=new ArrayList<Double>();
        for (Run run : stats){
            devValue.add(run.getValue() - meanValue);
            devTime.add(run.getTime() - meanTime);
        }
        double stdDevValue = 0;
        for (Double value : devValue){
            stdDevValue+=Math.pow(value, 2);
        }
        stdDevValue=Math.sqrt(stdDevValue);

        double stdDevTime = 0;
        for (Double time : devTime){
            stdDevTime+=Math.pow(time, 2);
        }
        stdDevTime=Math.sqrt(stdDevTime);
        //order is average value, std dev value, average time, std dev time
        double[] returnVal=new double[4];
        returnVal[0]=meanValue;
        returnVal[1]=stdDevValue;
        returnVal[2]=meanTime;
        returnVal[3]=stdDevTime;
        return returnVal;

    }

}
