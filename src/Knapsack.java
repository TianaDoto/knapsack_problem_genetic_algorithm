import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Knapsack {
    static Scanner sc;
    private int crossover_count = 0;
    private int clone_count = 0;
    private int no_items = 0;
    private int population_size = 0;
    private int maximum_generations = 0;
    private int generation_counter = 1;
    private double knapsack_capacity = 0;
    private double prob_crossover = 0;
    private double prob_mutation = 0;
    private double total_fitness_of_generation = 0;
    private ArrayList<Integer> value = new ArrayList<Integer>();
    private ArrayList<Integer> weight = new ArrayList<Integer>();
    private ArrayList<Double> fitness = new ArrayList<Double>();
    private ArrayList<Double> generation_fitness = new ArrayList<Double>();
    private ArrayList<String> population = new ArrayList<String>();
    private ArrayList<String> next_gen = new ArrayList<String>();
    private ArrayList<String> generation_solution = new ArrayList<String>();
    private ArrayList<ArrayList> cases;

    Knapsack(){
    	BufferedReader br = null;
    	try {
	    	InputStream file = Knapsack.class.getResourceAsStream("input_example.txt");
	    	br = new BufferedReader(new InputStreamReader(file));
    	    String line;
    	    boolean first_line = true;
    	    int case_count = 1;
    	    int total_cases = 0;
    	    while ((line = br.readLine()) != null) {
    	    	if(first_line) {
    	    		total_cases = Integer.parseInt(line);
    	    		cases = new ArrayList<ArrayList>(total_cases);
    	    		System.out.println("Total Cases: "+ total_cases);
    	    	}
    	    	else if(case_count > total_cases) {
    	    		break;
    	    	}
    	    	else if(line.equals(new String())) {
    	    		ArrayList<Integer> single_case = new ArrayList<Integer>();
    	    		int item_count = Integer.parseInt(br.readLine());
    	    		int knapsack_size = Integer.parseInt(br.readLine());
    	    		
    	    		single_case.add(item_count);
    	    		single_case.add(knapsack_size);
    	    		
    	    		for(int x=0; x<item_count; x++) {
    	    			String[] item = br.readLine().split("\\s+");
    	    			int weight = Integer.parseInt(item[0]);
    	    			int value = Integer.parseInt(item[1]);
    	    			
    	    			single_case.add(weight);
    	    			single_case.add(value);
    	    		}
    	    		cases.add(single_case);
    	    		case_count++;
    	    		br.readLine();
    	    	}
    	       //System.out.println(line);
    	    	first_line = false;
    	    }
    	    
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	for(int case_count=0; case_count<cases.size(); case_count++) {
    		calculate_knapsack(case_count);
    	}
    	
    }
    
    private void calculate_knapsack(int case_number) {
    	ArrayList<Integer> single_case = cases.get(case_number);
    	
    	no_items = single_case.get(0);
        for(int i = 1; i <= (single_case.size()-2)/2; i++) {
            weight.add(single_case.get(2*i));
            value.add(single_case.get((2*i)+1));
        }

        // Capacity of knapsack
        knapsack_capacity = single_case.get(1);
        
        // Population size
        population_size = 200;

        // Crossover probability
        prob_crossover = 0.7;

        // Mutation probability
        prob_mutation = 0.1;


        maximum_generations = population_size;
        
        // Generate population
        this.generatePopulation();

        //Fitness
        //this.evaluateFitness();

        //Stopping condition is generation == population size
        while(maximum_generations>=0){
        	
        	//if(generation_counter == 1)
        		this.evaluateFitness();
        	
            for(int i=0;i<population_size/2;i++){

                if(population_size % 2 == 1) {
                    next_gen.add(generation_solution.get(generation_counter - 1));
                }
                int gene1=selectGene();
                int gene2=selectGene();

                crossoverGenes(gene1,gene2);
            }
            
            for(int i=0;i<population_size;i++){
                population.add(i,next_gen.get(i));
            }
            
            if(maximum_generations == 0)
            	this.evaluateFitness();

            next_gen.clear();
            fitness.clear();

            generation_counter++;
            maximum_generations--;
        }
        
        show_result(generation_solution.get(generation_solution.size()-1), case_number, single_case);
        population.clear();
        weight.clear();
        value.clear();
    }
    
    private void show_result(String fitted_gene, int case_number, ArrayList<Integer> case_data) {
    	double total_weight = 0;
        double total_value = 0;
        double fitness_value = 0;
        int item_count = 0;
        char c = '1';
        
        ArrayList<Integer> item_index = new ArrayList<Integer>();

        for(int i = 0; i < no_items; i++) {
            c = fitted_gene.charAt(i);
            
            //Gene value 1
            if(c == '1') {
                total_weight = total_weight + weight.get(i);
                total_value = total_value + value.get(i);
                item_count++;
                item_index.add(i+1);
            }
        }
        
        fitness_value = total_value;
        System.out.println("Case "+(case_number+1)+" : "+fitness_value);
        System.out.println(item_count);
        if(item_count > 0) {
        	for(int x=0; x<item_index.size(); x++) {
        		int start_item_index = item_index.get(x)*2;
        		System.out.println(case_data.get(start_item_index)+" "+case_data.get(start_item_index+1));
        	}
        }
        System.out.println();
    }

    private int selectGene() {

        double rand = Math.random() * total_fitness_of_generation;
        for(int i = 0; i < population_size; i++) {
            if(rand <= fitness.get(i)) {
                return i;
            }
            rand = rand - fitness.get(i);
        }
        return 0;
    }

    private void crossoverGenes(int gene_1, int gene_2) {

        String new_gene_1;
        String new_gene_2;

        double rand_crossover = Math.random();
        if(rand_crossover <= prob_crossover) {
            // Perform crossover
            crossover_count = crossover_count + 1;
            Random generator = new Random();
            int cross_point = generator.nextInt(no_items) + 1;

            new_gene_1 = population.get(gene_1).substring(0, cross_point) + population.get(gene_2).substring(cross_point);
            new_gene_2 = population.get(gene_2).substring(0, cross_point) + population.get(gene_1).substring(cross_point);

            next_gen.add(new_gene_1);
            next_gen.add(new_gene_2);
        }
        else {
            clone_count = clone_count + 1;
            next_gen.add(population.get(gene_1));
            next_gen.add(population.get(gene_2));
        }

        mutateGene();
    }

    private void mutateGene() {

        double rand_mutation = Math.random();
        if(rand_mutation <= prob_mutation) {

            String mut_gene;
            String new_mut_gene;
            Random generator = new Random();
            int mut_point = 0;
            double which_gene = Math.random() * 1;

            // Mutate gene
            if(which_gene <= 0.5) {
                mut_gene = next_gen.get(next_gen.size() - 1);
                mut_point = generator.nextInt(no_items);
                if(mut_gene.substring(mut_point, mut_point + 1).equals("1")) {
                    new_mut_gene = mut_gene.substring(0, mut_point) + "0" + mut_gene.substring(mut_point+1);
                    next_gen.set(next_gen.size() - 1, new_mut_gene);
                }
                if(mut_gene.substring(mut_point, mut_point + 1).equals("0")) {
                    new_mut_gene = mut_gene.substring(0, mut_point) + "1" + mut_gene.substring(mut_point+1);
                    next_gen.set(next_gen.size() - 1, new_mut_gene);
                }
            }
            if(which_gene >0.5) {
                mut_gene = next_gen.get(next_gen.size() - 2);
                mut_point = generator.nextInt(no_items);
                if(mut_gene.substring(mut_point, mut_point + 1).equals("1")) {
                    new_mut_gene = mut_gene.substring(0, mut_point) + "0" + mut_gene.substring(mut_point+1);
                    next_gen.set(next_gen.size() - 1, new_mut_gene);
                }
                if(mut_gene.substring(mut_point, mut_point + 1).equals("0")) {
                    new_mut_gene = mut_gene.substring(0, mut_point) + "1" + mut_gene.substring(mut_point+1);
                    next_gen.set(next_gen.size() - 2, new_mut_gene);
                }
            }
        }
    }


    void evaluateFitness(){
        total_fitness_of_generation = 0;
        double fitest = 0;
        int fitest_index=0;

        for(int j = 0; j < population_size; j++) {
            double total_weight = 0;
            double total_value = 0;
            double fitness_value = 0;
            double diff = 0;
            char c = '1';

            for(int i = 0; i < no_items; i++) {
                c = population.get(j).charAt(i);
                
                //Gene value 1
                if(c == '1') {
                    total_weight = total_weight + weight.get(i);
                    total_value = total_value + value.get(i);
                }
            }

            diff = knapsack_capacity - total_weight;
            if(diff >= 0) {
                fitness_value = total_value;
            }

            fitness.add(fitness_value);
            if(fitness_value>fitest){
                fitest=fitness_value;
                fitest_index=j;
            }
            total_fitness_of_generation = total_fitness_of_generation + fitness_value;

        }
        generation_solution.add(population.get(fitest_index));
    }

    void generatePopulation(){
        for(int i = 0; i < population_size; i++) {
            String gene = "";
            
            for(int j = 0; j < no_items; j++) {

                if(Math.random() > 0.5) {
                    gene+="1";
                }
                else{
                    gene+="0";
                }
            }
            population.add(gene);
        }
    }

    public static void main(String args[]){
        Knapsack k=new Knapsack();
    }
}