package distributed;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantLock;

public class Controller implements Runnable, ControllerRMI{

    ReentrantLock mutex;
    Registry registry;
    ControllerRMI stub;

    private int N1;
    private int N2;
    private int[] assignments;
    private double[] prices;
    private Person[] persons;
    private Item[] items;
    private boolean notTerminated;
    HashSet<Integer> person_set;
    HashSet<Integer> item_set;
    private long start;
    public Controller(int N1, int N2, Item[] items, Person[] persons) {
    	start = System.currentTimeMillis();
        this.N1 = N1;
        this.N2 = N2;
        this.notTerminated = true;
        this.persons = persons;
        this.items = items;
        mutex = new ReentrantLock();
        assignments = new int[N1];
        prices = new double[N1];
        person_set = new HashSet<>();
        item_set = new HashSet<>();
        for(int i=0;i<N1; i++) {
            assignments[i] = -1;
            prices[i] = -1;
        }
        try {
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");
            registry = LocateRegistry.createRegistry(1300);
            stub = (ControllerRMI) UnicastRemoteObject.exportObject(this, 1300);
            registry.rebind("Controller", stub);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void Start() {
        Thread mythread = new Thread(this);
        mythread.start();

    }
    @Override
    public Response Report(Request req) throws RemoteException {
        this.mutex.lock();
        person_set.add(Integer.valueOf(req.id));
        if(req.item_id != -1) {  // Its not a termination report
            assignments[req.id] = req.item_id;
            prices[req.id] = req.price;
            item_set.add(Integer.valueOf(req.item_id));
        }
        if(item_set.size() == Math.min(N1,N2) && person_set.size() == N1) {
        	System.out.println("******");
        	System.out.println("final assignments");
            for(int i=0;i<N1; i++) {
                System.out.println("person " + i + " is assigned to object " + assignments[i] + " with price " + prices[i]);
            }
//        	System.out.println("Time: " + (System.currentTimeMillis() - start));
            killAll();
            
        }
        this.mutex.unlock();
        notTerminated = false;
        return null;
    }

    @Override
    public void run() {
        while(notTerminated){
        }
    }

    private void killAll() {
        for (int i = 0; i < N2; i++) {
            items[i].Kill();
        }
        for (int i = 0; i < N1; i++) {
            persons[i].Kill();
        }
    }
}
