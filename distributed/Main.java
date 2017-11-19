package distributed;

import java.util.Random;

public class Main {
    private static Person[] persons;
    private static Item[] items;
    static int N1;
    static int N2;

    private static Person.retStatus isDecided(Person[] pxa, int personIndex) {
        return pxa[personIndex].Status();
    }

    private static Person.retStatus waitForDecide(Person[] pxa, int personIndex) throws Exception {
        int to = 10;
        for (int i = 0; i < 30; i++) {
            Person.retStatus ret = isDecided(pxa, personIndex);
            if (ret.state != State.Pending) {
                return ret;
            }
            try {
                Thread.sleep(to);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (to < 1000) {
                to = to * 2;
            }
        }

        throw new Exception("time out error!");

    }

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
//	private Person[] initPerson(int nperson, double eps, double[][] weight) {
//		String host = "127.0.0.1";
//		String[] peers = new String[nperson];
//		int[] ports = new int[nperson];
//		Person[] pxa = new Person[nperson];
//		for (int i = 0; i < nperson; i++) {
//			ports[i] = 1100 + i;
//			peers[i] = host;
//		}
//		for (int i = 0; i < nperson; i++) {
//			pxa[i] = new Person(i, peers, ports, nperson, eps, weight[i]);
//		}
//		return pxa;
//	}
//
//	private Item[] initItem(int nItems) {
//		String host = "127.0.0.1";
//		String[] peers = new String[nItems];
//		int[] ports = new int[nItems];
//		Item[] pxa = new Item[nItems];
//		for (int i = 0; i < nItems; i++) {
//			ports[i] = 1200 + i;
//			peers[i] = host;
//		}
//		for (int i = 0; i < nItems; i++) {
//			pxa[i] = new Item(i, peers, ports);
//		}
//		return pxa;
//	}

    private static boolean isTerminated() {
        for (int i = 0; i < N1; i++) {
            if (persons[i].Status().state == State.Pending) {
                return false;
            }
        }
        int numPending = 0;
        for (int i = 0; i < N2; i++) {
            if (items[i].Status().state == State.Pending) {
                numPending++;
            }
        }
        if (Math.min(N1, N2) + numPending != N2) {
            return false;
        }
        return true;
    }

    public static void main(String args[]) {
        Random rand = new Random();
        N1 = 4;
        N2 = 4;
        final double eps = 1.0 / (2 * N1);
//		final double eps = 0.0;
        int n_set = N1;
        int[] l_set = new int[N1];
        double[][] weight = {
                {10, 1, 1, 1, 1},
                {10, 1, 1, 7, 1},
                {10, 1, 5, 1, 1},
                {10, 5, 1, 1, 1},
                {10, 5, 1, 1, 1}
        };
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
//        while (true) {
//            if (isTerminated()) {
//                System.out.println("\n\nTerminated");
//                for (int i = 0; i < N2; i++) {
//                    Item.retStatus ret = items[i].Status();
//                    System.out.println(ret.personIdx + ": " + ret.price);
//                }
//                killAll();
//                break;
//            }
//            try {
//                Thread.sleep(30);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }


}
