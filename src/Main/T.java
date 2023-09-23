package Main;

import java.util.Arrays;
import java.util.LinkedList;

public class T extends Thread{
    LinkedList<Body> bodies;
    double timestep;
    double t = 0;
    public T(LinkedList<Body> bodies, double timestep) {
        this.bodies = bodies;
        this.timestep = timestep;
    }

    @Override
    public void run() {
        //Thread.sleep(2000);
        while (true){
//                System.out.println(bodies.get(0).pos);
            for(Body b:bodies){
                b.update(timestep);
                Body.calcForce(bodies,timestep);
            }
//                Thread.sleep(200);
            t += timestep;
        }

    }
}
