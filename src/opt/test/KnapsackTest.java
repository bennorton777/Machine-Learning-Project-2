package opt.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import dist.DiscreteDependencyTree;
import dist.DiscreteUniformDistribution;
import dist.Distribution;

import opt.DiscreteChangeOneNeighbor;
import opt.EvaluationFunction;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.DiscreteChangeOneMutation;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.ga.UniformCrossOver;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

/**
 * A test of the knap sack problem
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class KnapsackTest {
    /** Random number generator */
    private static final Random random = new Random();
    /** The number of items */
    private static final int NUM_ITEMS = 40;
    /** The number of copies each */
    private static final int COPIES_EACH = 4;
    /** The maximum weight for a single element */
    private static final double MAX_WEIGHT = 50;
    /** The maximum volume for a single element */
    private static final double MAX_VOLUME = 50;
    /** The volume of the knapsack */
    private static final double KNAPSACK_VOLUME = 
         MAX_VOLUME * NUM_ITEMS * COPIES_EACH * .4;
    /**
     * The test main
     * @param args ignored
     */
    public static void main(String[] args) {
        int[] copies = new int[NUM_ITEMS];
        Arrays.fill(copies, COPIES_EACH);
        double[] weights = new double[NUM_ITEMS];
        double[] volumes = new double[NUM_ITEMS];
        for (int i = 0; i < NUM_ITEMS; i++) {
            weights[i] = random.nextDouble() * MAX_WEIGHT;
            volumes[i] = random.nextDouble() * MAX_VOLUME;
        }
         int[] ranges = new int[NUM_ITEMS];
        int iterations=Integer.parseInt(args[0]);
        Arrays.fill(ranges, COPIES_EACH + 1);
        EvaluationFunction ef = new KnapsackEvaluationFunction(weights, volumes, KNAPSACK_VOLUME, copies);
        Distribution odd = new DiscreteUniformDistribution(ranges);
        NeighborFunction nf = new DiscreteChangeOneNeighbor(ranges);
        MutationFunction mf = new DiscreteChangeOneMutation(ranges);
        CrossoverFunction cf = new UniformCrossOver();
        Distribution df = new DiscreteDependencyTree(.1, ranges); 
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
        ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);
        ArrayList<Run> saList=new ArrayList<Run>();
        ArrayList<Run> gaList=new ArrayList<Run>();
        ArrayList<Run> mimList=new ArrayList<Run>();
        for (int i=0;i<20;i++){
            FixedIterationTrainer fit;
            double start, end;
            SimulatedAnnealing sa = new SimulatedAnnealing(100, .95, hcp);
            fit = new FixedIterationTrainer(sa, 200*iterations);//200000
            start=System.nanoTime();
            fit.train();
            end=System.nanoTime();
            saList.add(new Run(ef.value(sa.getOptimal()),end-start));


            StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 150, 25, gap);
            fit = new FixedIterationTrainer(ga, 1000);
            start=System.nanoTime();
            fit.train();
            end=System.nanoTime();
            gaList.add(new Run(ef.value(ga.getOptimal()),end-start));


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
