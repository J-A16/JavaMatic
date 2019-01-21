import java.util.*;
import static java.util.Map.entry;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.Locale;

public class JavaMatic {

	private String[] ingredients;
	private HashMap<String, Integer> ingredientStock;
	private HashMap<String, Double> ingredientPrices;
	private String[] drinks;
	private boolean[] drinksStock;
	private HashMap<String, Map<String, Integer>> drinkRecipes;
	private NumberFormat usCurrencyformat;
	private String[] drinkPrices;
	
	private final int MAX_INGREDIENT_QUANTITY = 10;

	JavaMatic() {
		ingredients = new String[] { "Coffee", "Decaf Coffee", "Sugar", "Cream", "Steamed Milk", "Foamed Milk",
				"Espresso", "Cocoa", "Whipped Cream" };

		Double[] prices = new Double[] { 0.75, 0.75, 0.25, 0.25, 0.35, 0.35, 1.10, 0.90, 1.00 };

		ingredientStock = new HashMap<>();
		ingredientPrices = new HashMap<>();

		for (int i = 0; i < ingredients.length; i++) {
			ingredientStock.put(ingredients[i], MAX_INGREDIENT_QUANTITY);
			ingredientPrices.put(ingredients[i], prices[i]);
		}

		Arrays.sort(ingredients);

		drinks = new String[] { "Coffee", "Decaf Coffee", "Caffe Latte", "Caffe Americano", "Caffe Mocha",
				"Cappucinputo" };

		Arrays.sort(drinks);

		drinkRecipes = new HashMap<>();

		drinkRecipes.put("Coffee", Map.ofEntries(entry("Coffee", 3), entry("Sugar", 1), entry("Cream", 1)));

		drinkRecipes.put("Decaf Coffee", Map.ofEntries(entry("Decaf Coffee", 3), entry("Sugar", 1), entry("Cream", 1)));

		drinkRecipes.put("Caffe Latte", Map.ofEntries(entry("Espresso", 2), entry("Steamed Milk", 1)));

		drinkRecipes.put("Caffe Americano", Map.ofEntries(entry("Espresso", 3)));

		drinkRecipes.put("Caffe Mocha", Map.ofEntries(entry("Espresso", 1), entry("Cocoa", 1), entry("Steamed Milk", 1),
				entry("Whipped Cream", 1)));

		drinkRecipes.put("Cappucinputo",
				Map.ofEntries(entry("Espresso", 2), entry("Steamed Milk", 1), entry("Foamed Milk", 1)));

		drinksStock = new boolean[drinks.length];
		updateDrinksStock();

		usCurrencyformat = NumberFormat.getCurrencyInstance(Locale.US);

		drinkPrices = new String[drinks.length];

		calculateDrinkPrices();
	}

	private void calculateDrinkPrices() {
		for (int i = 0; i < drinks.length; i++) {
			double price = 0;

			Map<String, Integer> recipe = drinkRecipes.get(drinks[i]);
			String[] drinkIngredientsNames = recipe.keySet().toArray(new String[recipe.size()]);

			for (String ingredient : drinkIngredientsNames) {
				price += ingredientPrices.get(ingredient) * recipe.get(ingredient);
			}

			drinkPrices[i] = usCurrencyformat.format(price);
		}
	}

	private void updateDrinksStock() {

		for (int i = 0; i < drinks.length; i++) {
			boolean inStock = true;

			Map<String, Integer> recipe = drinkRecipes.get(drinks[i]);
			String[] drinkIngredientsNames = recipe.keySet().toArray(new String[recipe.size()]);

			for (String ingredient : drinkIngredientsNames) {
				if (ingredientStock.get(ingredient) < recipe.get(ingredient)) {
					inStock = false;
				}
			}

			drinksStock[i] = inStock;
		}
	}

	public boolean drinkInStock(String drink) {
		return drinksStock[Integer.parseInt(drink) - 1];
	}

	public String getDrinkName(String drink) {
		return drinks[Integer.parseInt(drink) - 1];
	}

	public void restock() {
		for (int i = 0; i < ingredients.length; i++) {
			ingredientStock.put(ingredients[i], MAX_INGREDIENT_QUANTITY);
		}

		updateDrinksStock();
	}

	public void dispenseDrink(String drink) {
		String drinkName = getDrinkName(drink);

		Map<String, Integer> recipe = drinkRecipes.get(drinkName);
		String[] drinkIngredientsNames = recipe.keySet().toArray(new String[recipe.size()]);

		for (String ingredient : drinkIngredientsNames) {
			ingredientStock.put(ingredient, ingredientStock.get(ingredient) - recipe.get(ingredient));
		}

		updateDrinksStock();
	}

	public void printIngredientStocks(PrintStream output) {
		output.println("Inventory:");

		for (String ingredient : ingredients) {
			output.println(ingredient + "," + ingredientStock.get(ingredient));
		}
	}

	public void printMenu(PrintStream output) {
		output.println("Menu:");

		for(int i = 0; i < drinks.length; i++) {
			output.println((i + 1) + "," + drinks[i] + "," + drinkPrices[i] + "," + drinksStock[i]);
		}
	}

	public static void main(String[] args) {

		Scanner input = new Scanner(System.in);
		
		PrintStream output = new PrintStream(System.out);
		
		JavaMatic javaMatic = new JavaMatic();
		
		javaMatic.printIngredientStocks(output);
		javaMatic.printMenu(output);
		
		String instruction = input.nextLine();

		while (!instruction.toLowerCase().equals("q")) {
			if (!valid(instruction)) {
				output.println("Invalid selection: " + instruction);
			} else if (instruction.toLowerCase().equals("r")) {
				javaMatic.restock();
			} else if (javaMatic.drinkInStock(instruction)) {
				output.println("Dispensing: " + javaMatic.getDrinkName(instruction));

				javaMatic.dispenseDrink(instruction);
			} else {
				output.println("Out of stock: " + javaMatic.getDrinkName(instruction));
			}
			
			javaMatic.printIngredientStocks(output);
			javaMatic.printMenu(output);
			
			instruction = input.nextLine();
		}
		
		input.close();
	}

	static boolean valid(String instruction) {
		boolean valid = true;

		List<String> validEntries = (List<String>) Arrays
				.asList(new String[] { "r", "q", "1", "2", "3", "4", "5", "6" });

		if (!validEntries.contains(instruction.toLowerCase())) {
			valid = false;
		}

		return valid;
	}

}
