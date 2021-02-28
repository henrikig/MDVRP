package Utilities;

import Models.MDVRP;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ProblemInit {
    public static MDVRP initializeProblem() {
        String filename = Parameters.PROBLEM_FOLDER + Parameters.PROBLEM_FILE;
        ArrayList<String> fileContent = new ArrayList<>();

        // Read the problem file line for line
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            while (br.ready()) {
                fileContent.add(br.readLine().trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get problem meta data
        String problemInfo = fileContent.get(0);
        // Split line on whitespace
        String[] problemInfoSplit = problemInfo.split("\\s+");
        int maxVehicles = Integer.parseInt(problemInfoSplit[0]);
        int numCustomers = Integer.parseInt(problemInfoSplit[1]);
        int numDepots = Integer.parseInt(problemInfoSplit[2]);

        List<String> maxLoadLine = Arrays.asList(fileContent.get(1).split("\\s+"));
        Double maxLoad = Double.parseDouble(maxLoadLine.get(0));

        Map<Integer, ArrayList<Double>> depots = new HashMap<>();

        // Iterate depots and extract data
        for(int depot = 1; depot < numDepots + 1; depot++) {
            List<String> depotEntry = Arrays.asList(fileContent.get(depot + numCustomers + numDepots).split("\\s+"));

            Double xDepot = Double.parseDouble(depotEntry.get(1));
            Double yDepot = Double.parseDouble(depotEntry.get(2));

            ArrayList<Double> depotLoc = new ArrayList<>(Arrays.asList(xDepot, yDepot, maxLoad));
            depots.put(depot - 1, depotLoc);
        }

        // Iterate customers and extract data
        Map<Integer, ArrayList<Double>> customers = new HashMap<>();
        for(int customer = 1 + numDepots; customer < numCustomers + numDepots + 1; customer++) {
            List<String> customerEntry = Arrays.asList(fileContent.get(customer).split("\\s+"));

            Double xCustomer = Double.parseDouble(customerEntry.get(1));
            Double yCustomer = Double.parseDouble(customerEntry.get(2));
            Double demand = Double.parseDouble(customerEntry.get(4));

            ArrayList<Double> customerLoc = new ArrayList<>(Arrays.asList(xCustomer, yCustomer, demand));
            customers.put(customer - numDepots - 1, customerLoc);
        }

        return new MDVRP(depots, customers, numDepots, numCustomers, maxVehicles, maxLoad);


    }
}
