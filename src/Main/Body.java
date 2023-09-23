package Main;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.LinkedList;
import GUI.GUI.Window;
import org.ejml.data.Matrix;
import org.ejml.interfaces.decomposition.QRDecomposition;
import org.ejml.simple.SimpleMatrix;

public class Body {
    private static final double G = 9.81;
    public static boolean ERROR = false;
    public static boolean DEBUG = false;
    public static boolean attract;
    public static Point MOUSE = Point.O.clone();
    public Point pos,vel,acc,f;
    double mass;
    public LinkedList<Constraint> constraints;
    boolean gravity = true;

    public Body(Point pos,double mass) {
        this.pos = pos;
        this.mass = mass;
        this.vel = Point.O.clone();
        this.acc = Point.O.clone();
        this.f = Point.O.clone();
        this.constraints = new LinkedList<>();
    }

    public void anchor(Body other,double a){
        this.constraints.add(new DistConstraint(other,a));
        other.constraints.add(new DistConstraint(this,a));
    }
    public void anchor(Body other){
        this.constraints.add(new DistConstraint(other,other.pos.minus(this.pos).magnitude()));
        other.constraints.add(new DistConstraint(this,other.pos.minus(this.pos).magnitude()));
    }

    public void update(double step) {
        // Compute the four derivatives
        Point k1v = this.acc;
        Point k1p = this.vel;
        Point k2v = this.acc.add(k1p.scale(step/2));
        Point k2p = this.vel.add(k1v.scale(step/2));
        Point k3v = this.acc.add(k2p.scale(step/2));
        Point k3p = this.vel.add(k2v.scale(step/2));
        Point k4v = this.acc.add(k3p.scale(step));
        Point k4p = this.vel.add(k3v.scale(step));

        // Update the state using the weighted sum of derivatives
        this.vel = this.vel.add((k1v.add(k2v.scale(2)).add(k3v.scale(2)).add(k4v)).scale(step/6));
        this.pos = this.pos.add((k1p.add(k2p.scale(2)).add(k3p.scale(2)).add(k4p)).scale(step/6));
    }


    public static void calcForce(LinkedList<Body> o,double timestep) {
        SimpleMatrix M,extF,conF,J,A,b,v,Minv,j;
        LinkedList<Constraint> constraints = new LinkedList<>();

        for (int i = 0; i < o.size(); i++) {
            constraints.addAll(o.get(i).constraints);
        }

        v = new SimpleMatrix(o.size()*2,1);
        M = new SimpleMatrix(o.size()*2,o.size()*2);
        extF = new SimpleMatrix(o.size()*2,1);
        for (int i = 0; i < o.size(); i++) {
            //Create velocity (q̇) matrix
            v.set(i*2,0,o.get(i).vel.x);
            v.set(i*2+1,0,o.get(i).vel.y);

            //Create mass matrix
            M.set(i*2,i*2,o.get(i).mass);
            M.set(i*2+1,i*2+1,o.get(i).mass);

            //Create external force matrix
            Point f = o.get(i).getForces(timestep);
            extF.set(i*2,0,f.x);
            extF.set(i*2+1,0,f.y);
        }

        //Create Jacobian Matrix
        J = new SimpleMatrix(constraints.size(),o.size()*2);
        j = new SimpleMatrix(constraints.size(),o.size()*2);
        for (int i = 0; i < constraints.size(); i++) {
            for (int k = 0; k < o.size(); k++) {
                /*
                * This is probably wrong and causing errors
                * */
                if (o.get(k).constraints.contains(constraints.get(i))){
                    //double r = Math.pow(((DistConstraint)constraints.get(i)).r,0.5);
                    Point m1 = ((DistConstraint)constraints.get(i)).o.pos.minus(o.get(k).pos);
                    Point m2 = ((DistConstraint)constraints.get(i)).o.vel.minus(o.get(k).vel);

                    //System.out.println(m1*Math.sin(theta));
                    J.set(i,k*2  ,m1.x);
                    J.set(i,k*2+1,m1.y);

                    j.set(i,k*2  ,m2.x);
                    j.set(i,k*2+1,m2.y);
                }else{
                    J.set(i,k*2,0);
                    J.set(i,k*2+1,0);
                    j.set(i,k*2,0);
                    j.set(i,k*2+1,0);
                }
            }
        }

        Minv = M.invert();

        A = J.mult(Minv).mult(J.transpose());
        b = j.scale(-1).mult(v).minus(J.mult(Minv).mult(extF));

//        System.out.println("A: "+A.toString());
//        System.out.println("b: "+b.toString());
//        System.out.println();

        try{
            conF = A.solve(b);
//            System.out.println(J.toString());
//            System.out.println(conF.toString());
            int conI = 0;
            for (Body body : o) {
                if(body.constraints.size()>0){
                    body.acc = Point.O.clone();
    //                System.out.println(o.get(i).constraints.size());
                    body.f = Point.O.clone();
                }
                for (Constraint c : body.constraints) {
                    Point dir = ((DistConstraint) c).o.pos.minus(body.pos);
//                    dir = dir.scale(1/dir.magnitude());
                    Point F = dir.scale( (conF.get(conI, 0))/ body.mass);
                    body.f = body.f.add(F);
                    body.acc = body.acc.add(F);
                    if(DEBUG){
                        System.out.println(F);
                    }
                    conI++;
                }
                Point f = body.getForces(timestep);
                body.acc = body.acc.add(f.scale(1 / body.mass));
            }
            if(DEBUG){
                System.out.println();
            }
            ERROR = false;
        }catch (Exception e){
            ERROR = true;
//            System.out.println(A.toString());
            if(DEBUG){
                System.out.println(A.toString());
                System.out.println(b.toString());
                System.out.println();
            }
            for (Body body : o) {
                Point f = body.getForces(timestep);
                body.acc = (f.scale(1 / body.mass));
            }
        }
        DEBUG = false;
    }

    public Point getForces(double timestep) {
        Point f = Point.O.clone();
        if (this.gravity){
            f.y -= G*this.mass;
        }
        Point d = Body.MOUSE.minus(this.pos);
        double S = 1000000;
        if (Body.attract && d.dot(d) - 10000 < 0){
            f = f.add(d.scale(S*this.mass/d.dot(d)));
        }
        return f;
    }

    public void paint(Graphics g, Window w) throws Exception {
        Graphics2D g2 = (Graphics2D) g;
        Point newPos = this.pos.scale(1/w.scale).mathToScreen(w);
        Point vel = new Point(this.vel.x,-this.vel.y).add(newPos);
        Point acc = new Point(this.acc.x,-this.acc.y).add(newPos);
        Point f = new Point(this.f.x,-this.f.y).add(newPos);
        Point o;
        g.setColor(new Color(215,120,0));
        g.fillOval((int)(newPos.x-(10)),(int)(newPos.y-(10)), (int) (20), (int) (20));
        for (Constraint c1:this.constraints) {
            if (c1 instanceof DistConstraint){
                DistConstraint c = (DistConstraint)c1;
                Point p = c.o.pos.minus(this.pos);
                o = ((DistConstraint)c).o.pos.scale(1/w.scale).mathToScreen(w);
                g.setColor(Color.black);
                if (p.dot(p)-c.r*c.r < 0){
                    g.setColor(new Color((int) Math.min(750*((c.r*c.r-p.dot(p))/(c.r*c.r)),254),0,0));
                }else{
                    g.setColor(new Color(0,0,(int) Math.min(750*((p.dot(p)-c.r*c.r)/(c.r*c.r)),254)));
                }
                g2.setStroke(new BasicStroke((float) (4)));
                g2.draw(new Line2D.Float((int)o.x,(int)o.y,(int)newPos.x,(int)newPos.y));
//                g.setColor(Color.yellow);
//                g.fillOval((int)(o.x-(5)),(int)(o.y-(5)), (int) (10), (int) (10));
            }
        }

        g.setColor(Color.red);
        g2.setStroke(new BasicStroke((float) (2)));
        g2.draw(new Line2D.Float((int)newPos.x,(int)newPos.y,(int)vel.x,(int)vel.y));
        g.setColor(Color.blue);
        g2.draw(new Line2D.Float((int)newPos.x,(int)newPos.y,(int)acc.x,(int)acc.y));
        g.setColor(Color.green);
        g2.draw(new Line2D.Float((int)newPos.x,(int)newPos.y,(int)f.x,(int)f.y));

    }
}
