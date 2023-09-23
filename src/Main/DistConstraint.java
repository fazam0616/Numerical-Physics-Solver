package Main;

public class DistConstraint extends Constraint{
    public Body o;
    public double r;
    public DistConstraint(Body o,double d) {
        this.o = o;
        this.r = d;
    }

    @Override
    /*Derivative of constraint function:
    * C(p): ((p-o)*(p-o)-r^2)/2
    * Where P is position, o is anchor position, r is radius of link
     */
    public double f(Body p2) throws Exception {
        Point p = p2.pos.minus(o.pos);
        Point q = p2.vel.minus(o.vel);
        return p.dot(q);
    }

    //Second derivative of constraint function
    @Override
    public double fDot(Body p2) throws Exception {
        Point q = p2.pos.minus(o.pos);
        Point v = p2.vel.minus(o.vel);
        Point a = p2.acc.minus(o.acc);
        return q.dot(a)+v.dot(v);
    }
}
