package apriori;

import java.io.*;
import java.util.*;

/**
 * This class represents the whole dataset of all supermarket transactions. A
 * transaction is an itemset of items bought by a supermarket client in a single
 * transaction. The class includes functions for loading the dataset from the
 * file, computing support and confidence etc. The <code> main() </code>
 * function of the class loads the dataset from the default file, runs the
 * apriori algorithm and dumps the results to the console.
 * 
 * 
 */

public class Dataset {

	private LinkedList transactionList = new LinkedList();

	/**
	 * Creates and initializes the dataset with the data from a file
	 * 
	 * @param filename
	 *            the name of the file to be loaded
	 * @throws IOException
	 */
	public Dataset(String filename) throws IOException {
		LineNumberReader lineReader = new LineNumberReader(
				new InputStreamReader(new FileInputStream(filename)));
		String line = null;
		while ((line = lineReader.readLine()) != null) {
			Itemset newItemset = new Itemset();
			StringTokenizer tokenizer = new StringTokenizer(line, " ,\t");
			while (tokenizer.hasMoreTokens()) {
				newItemset.addItem(new Item(tokenizer.nextToken()));
			}
			// ignore all empty itemsets
			if (newItemset.size() != 0) {
				transactionList.add(newItemset);
			}
		}
	}

	public void dumpItemsets() {
		Iterator itItemset = getTransactionIterator();
		while (itItemset.hasNext()) {
			Itemset itemset = (Itemset) itItemset.next();
			System.out.println(itemset.toString());
		}
	}

	/**
	 * 
	 * @return the iterator that allows to go over all the transactions in the
	 *         dataset The transactions are <code> Itemset </code> objects
	 */
	public Iterator getTransactionIterator() {
		return transactionList.iterator();
	}

	/**
	 * 
	 * @return the number of transactions in the dataset
	 */
	public int getNumTransactions() {
		return transactionList.size();
	}

	/**
	 * 
	 * @param itemset
	 * @return the support value for a given itemset in the context of the
	 *         current dataset
	 */
	public double computeSupportForItemset(Itemset itemset) {
		int occurrenceCount = 0;
		Iterator itItemset = getTransactionIterator();
		while (itItemset.hasNext()) {
			Itemset shoppingList = (Itemset) itItemset.next();
			if (shoppingList.intersectWith(itemset).size() == itemset.size()) {
				occurrenceCount++;
			}
		}
		return ((double) occurrenceCount) / getNumTransactions();
	}

	/**
	 * 
	 * @param associationRule
	 * @return the confidence value for a given association rule in the context
	 *         of the current dataset
	 */
	public double computeConfidenceForAssociationRule(
			AssociationRule associationRule) {
		Itemset union = associationRule.getItemsetA().unionWith(
				associationRule.getItemsetB());
		return computeSupportForItemset(union)
				/ computeSupportForItemset(associationRule.getItemsetA());
	}

	/**
	 * 
	 * @return all possible itemsets of size one based on the current dataset
	 */
	public Set getAllItemsetsOfSizeOne() {
		Iterator itItemset = getTransactionIterator();
		Itemset bigUnion = new Itemset();
		while (itItemset.hasNext()) {
			Itemset itemset = (Itemset) itItemset.next();
			bigUnion = bigUnion.unionWith(itemset);
		}

		// break up the big unioned itemset into one element itemsets
		HashSet allItemsets = new HashSet();
		Iterator itItem = bigUnion.getItemIterator();
		while (itItem.hasNext()) {
			Item item = (Item) itItem.next();
			Itemset itemset = new Itemset();
			itemset.addItem(item);
			allItemsets.add(itemset);
		}

		return allItemsets;
	}

	/**
	 * The core of the association rule mining algorithm. This is what needs to
	 * be implemented. This is the only piece of code that you need to modify to
	 * complete the exercise.
	 *  
	 * @param minSupport
	 *            minimal support value below which itemsets should not be
	 *            considered when generating candidate itemsets
	 * @param minConfidence
	 *            minimal support value for the association rules output by the
	 *            algorithm
	 * @return a collection of <code> AssociationRule </code> instances
	 */
	public Collection runApriori(double minSupport, double minConfidence) {
		Collection discoveredAssociationRules = new LinkedList();

		// generate candidate itemsets
		final int MAX_NUM_ITEMS = 100;
		Set[] candidates = new Set[MAX_NUM_ITEMS];
		candidates[1] = getAllItemsetsOfSizeOne();
		for (int numItems = 1; numItems < MAX_NUM_ITEMS
				&& !candidates[numItems].isEmpty(); numItems++) {
			candidates[numItems + 1] = new HashSet();
			for (Iterator itItemset1 = candidates[numItems].iterator(); itItemset1
					.hasNext();) {
				Itemset itemset1 = (Itemset) itItemset1.next();
				for (Iterator itItemset2 = candidates[numItems].iterator(); itItemset2
						.hasNext();) {
					Itemset itemset2 = (Itemset) itItemset2.next();
					if (itemset1.intersectWith(itemset2).size() == numItems - 1) {
						Itemset candidateItemset = itemset1.unionWith(itemset2);
						assert (candidateItemset.size() == numItems + 1);
						if (computeSupportForItemset(candidateItemset) > minSupport) {
							candidates[numItems + 1].add(candidateItemset);
						}
					}
				}
			}
		}

		// generate association rules from candidate itemsets
		for (int numItems = 1; numItems < MAX_NUM_ITEMS
				&& !candidates[numItems].isEmpty(); numItems++) {
			for (Iterator itItemsetCandidate = candidates[numItems].iterator(); itItemsetCandidate
					.hasNext();) {
				Itemset itemsetCandidate = (Itemset) itItemsetCandidate.next();
				for (Iterator itItemsetSub = itemsetCandidate
						.generateAllNonEmptySubsets().iterator(); itItemsetSub
						.hasNext();) {
					Itemset itemsetSub = (Itemset) itItemsetSub.next();
					Itemset itemsetA = itemsetSub;
					Itemset itemsetB = itemsetCandidate.minusAllIn(itemsetSub);
					AssociationRule candidateAssociationRule = new AssociationRule(
							itemsetA, itemsetB);
					if (computeConfidenceForAssociationRule(candidateAssociationRule) > minConfidence) {
						discoveredAssociationRules
								.add(candidateAssociationRule);

					}
				}
			}
		}
		return discoveredAssociationRules;
	}

	/**
	 * Loads the dataset from a default file, runs the apriori algorithm and outputs the result to the console.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Dataset dataset = new Dataset(
					"c:\\_home\\teaching\\dis\\apriori\\xactions.txt");
			Collection discoveredAssociationRules = dataset
					.runApriori(0.16, 0.81);

			Iterator itAssociationRule = discoveredAssociationRules.iterator();
			while (itAssociationRule.hasNext()) {
				AssociationRule associationRule = (AssociationRule) itAssociationRule
						.next();
				System.out
						.println("assoctiation rule: "
								+ associationRule
								+ "\tsupport: "
								+ dataset
										.computeSupportForItemset(associationRule
												.getItemsetA().unionWith(
														associationRule
																.getItemsetB()))
								+ "\tconfidence: "
								+ dataset
										.computeConfidenceForAssociationRule(associationRule));
				
			}

			/*
			 * Itemset itemset = new Itemset(); itemset.addItem(new Item("1"));
			 * itemset.addItem(new Item("2")); itemset.addItem(new Item("3"));
			 * itemset.addItem(new Item("4"));
			 * 
			 * Iterator itItemset = itemset.generateAllNonEmptySubsets()
			 * .iterator(); while (itItemset.hasNext()) { Itemset itemset2 =
			 * (Itemset) itItemset.next();
			 * System.out.println(itemset2.toString()); }
			 */

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
