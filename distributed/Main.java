package distributed;

import java.util.Random;

public class Main {
    private static Person[] persons;
    private static Item[] items;
    static int N1;
    static int N2;


    private static void init(int nPersons, int nItems, double eps, double[][] weight, double[] price) {
        String host = "127.0.0.1";
        String[] personPeers = new String[nPersons];
        int[] personPorts = new int[nPersons];
        persons = new Person[nPersons];
        for (int i = 0; i < nPersons; i++) {
            personPorts[i] = 1100 + i;
            personPeers[i] = host;
        }

        String[] itemPeers = new String[nItems];
        int[] itemPorts = new int[nItems];
        items = new Item[nItems];
        for (int i = 0; i < nItems; i++) {
            itemPorts[i] = 1200 + i;
            itemPeers[i] = host;
        }
        for (int i = 0; i < nPersons; i++) {
            persons[i] = new Person(i, itemPeers, itemPorts, personPeers, personPorts, nItems, eps, weight[i], price);
        }

        for (int i = 0; i < nItems; i++) {
            items[i] = new Item(i, nPersons, itemPeers, itemPorts, personPeers, personPorts);
        }

    }

    public static void main(String args[]) {
        Random rand = new Random();
        N1 = 10;
        N2 = 10;
        final double eps = 1.0 / (2 * N1);
        int[] l_set = new int[N1];
        double[][] weight = new double[N1][N2];
//        double[][] weight = {
//                {10, 1, 1, 1, 1},
//                {10, 1, 1, 7, 1},
//                {10, 1, 5, 1, 1},
//                {10, 5, 1, 1, 1},
//                {10, 5, 1, 1, 1}
//        };
		for (int i = 0; i < N1; i++) {
			for (int j = 0; j < N2; j++) {
				weight[i][j] = rand.nextDouble() * 5.0;
			}
		}
        double[] price = new double[N2];
        int[] parent = new int[N2];

        for (int i = 0; i < N2; i++) {
            price[i] = 0.0;
            parent[i] = -1;
        }
        init(N1, N2, eps, weight, price);

        for (int i = 0; i < N1; i++) {
            l_set[i] = i;
        }

        Controller cont = new Controller(N1, N2, items, persons);
        cont.Start();
        for (int i = 0; i < N2; i++) {
            items[i].Start();
        }
        for (int i = 0; i < N1; i++) {
            persons[i].Start();
        }
    }


}
