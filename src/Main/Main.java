package Main;

import GUI.GUI.Window;

import java.util.Random;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Window w = new Window(800,800);

        Body anchor = new Body(new Point(0,0),0);

        w.bodies.add(new Body(new Point(10,0),10));
        w.bodies.add(new Body(new Point(20,0),10));
        w.bodies.add(new Body(new Point(30,0),10));
//        w.bodies.add(new Body(new Point(40,0),10));

        anchor.anchor(w.bodies.get(0));
//        w.bodies.get(0).anchor(anchor);

        w.bodies.get(0).anchor(w.bodies.get(1));
//        w.bodies.get(1).anchor(w.bodies.get(0));
        w.bodies.get(2).anchor(w.bodies.get(1));
//        w.bodies.get(1).anchor(w.bodies.get(2));
        w.bodies.get(0).vel = new Point(-0,-2);
        w.bodies.get(1).vel = new Point(-0,-4);
        w.bodies.get(2).vel = new Point(-0,-8);
//        w.bodies.get(1).vel = new Point(0,-40);
        int frame = 0;
        long time = 0;
        T thread = new T(w.bodies,w.timestep);
        thread.start();
        //Thread.sleep(1000);
        Random r = new Random();
//        w.bodies.get(3).gravity = false;
        while (true){
//            Body b = w.bodies.get(0);
//            System.out.println(b.acc);
//            b = w.bodies.get(1);
//            System.out.println(b.acc);
//            System.out.println();
            time = System.nanoTime();
            w.time = thread.t;
            w.repaint();
            Thread.sleep(1000/30);
            //w.bodies.get(3).acc=w.bodies.get(3).acc.add(new Point(r.nextDouble()-0.5,r.nextDouble()-0.5).scale(100));
        }
    }

}