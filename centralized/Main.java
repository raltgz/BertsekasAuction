package centralized;

import java.util.Random;

import centralized.Person.retStatus;

public class Main {

	public static void main(String[] args) {

		Random rand = new Random();
		int N1 = 5;
		int N2 = 5;
		double eps = 1.0 / (2 * N1);
		// final double eps = 0.0;
		int n_set = N1;
		int[] l_set = new int[N1];
//		double[][] weight = new double[N1][N2];
		 double[][] weight = {
		 {1, 4, 3, 2, 1},
		 {2, 1, 1, 7, 1},
		 {4, 1, 5, 1, 1},
		 {7, 5, 1, 1, 1},
		 {5, 5, 1, 1, 1}
		 };
		double m = 0.0;
//		for (int i = 0; i < N1; i++) {
//			for (int j = 0; j < N2; j++) {
//				weight[i][j] = rand.nextDouble() * 5.0;
//				if (weight[i][j] > m) {
//					m = weight[i][j];
//				}
//
//			}
//		}
//		eps = m / 4;
//		if (eps < 1.0 / (2 * N1)) {
//			eps = 1.0 / (2 * N1);
//		}
		Person[] ps = initPerson(N1, N2, eps, weight);
		double[] price = new double[N2];
		int[] parent = new int[N2];

		for (int i = 0; i < N2; i++) {
			price[i] = 0.0;
			parent[i] = -1;
		}
		for (int i = 0; i < N1; i++) {
			l_set[i] = i;
		}
		while (n_set > 0) {
			// System.out.println(n_set);
			int currPerson = l_set[n_set - 1];
			System.out.println("Starting round with person " + currPerson);
			ps[currPerson].Start(price);
			retStatus ret = null;
			try {
				ret = waitForDecide(ps, currPerson);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (ret.state == State.Terminated) {
				System.out.println("Terminating person " + currPerson);
				n_set--;
			} else {
				if (parent[ret.objectIdx] == -1) {
					n_set--;

				} else {
					System.out.println("Person " + parent[ret.objectIdx] + " is now without an assignment");
					l_set[n_set - 1] = parent[ret.objectIdx];
				}
				System.out.println(
						"Assigning person " + currPerson + " to object " + ret.objectIdx + " with price " + ret.price);
				parent[ret.objectIdx] = currPerson;
				price[ret.objectIdx] = ret.price;
			}
			System.out.println("****************");
//			if (eps * 2 / 3.0 >= 1.0 / (2 * N1)) {
//				eps = eps * 2 / 3.0;
//			}
		}
		System.out.println("****************");
		System.out.println("Final assignments:");
		for(int i = 0; i < N2; i++) {
			System.out.println("Object " + i + " is assigned to person " + parent[i] + " with price " + price[i]);
		}
	}

	private static retStatus isDecided(Person[] pxa, int personIndex) {
		return pxa[personIndex].Status();
	}

	private static retStatus waitForDecide(Person[] pxa, int personIndex) throws Exception {
		int to = 10;
		for (int i = 0; i < 30; i++) {
			retStatus ret = isDecided(pxa, personIndex);
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

	private static Person[] initPerson(int nperson, int N2, double eps, double[][] weight) {
		String host = "127.0.0.1";
		String[] peers = new String[nperson];
		int[] ports = new int[nperson];
		Person[] pxa = new Person[nperson];
		for (int i = 0; i < nperson; i++) {
			ports[i] = 1100 + i;
			peers[i] = host;
		}
		for (int i = 0; i < nperson; i++) {
			pxa[i] = new Person(i, peers, ports, N2, eps, weight[i]);
		}
		return pxa;
	}

}
