package Utilities;

import java.util.ArrayList;
import java.util.Arrays;

public class Parameters {

    public final static String PROBLEM_FOLDER = "./data/Testing Data/Data Files/";
    public final static String SOLUTIONS_FOLDER = "./data/Solution files/";
    public final static String PLOT_FILE = "./plots/mdvrp_plot.py";
    public final static Double FITNESS_TARGET = Double.NEGATIVE_INFINITY;
    public final static String PROBLEM_FILE = "p08";
    public final static boolean RUN_ALL = false;
    public final static int MAX_TIME = 300;

    public final static int POPULATION_SIZE = 400;
    public final static int GENERATIONS = 3000;
    public final static int ELITISM = 4;

    public final static double KEEP_BEST = 0.8;
    public final static double MUTATION_PROB = 0.4;
    public final static double XOVER_PROB = 0.6;
    public final static double INSERT_BEST = 0.8;
    public final static double SWAP_BOUND = 0.5;

    public final static double PENALTY_DEMAND = 10;
    public final static double PENALTY_LENGTH = 15;

    public final static ArrayList<String> ALL_PROBLEMS = new ArrayList<>(Arrays.asList(
            "p01", "p02", "p03", "p04", "p05", "p06",
            "p07", "p08", "p09", "p10", "p11", "p12",
            "p13", "p14", "p15", "p16", "p17", "p18",
            "p19", "p20", "p21", "p22", "p23"));

}
