package Main;

public abstract class Constraint {
    public abstract double f(Body o) throws Exception;
    public abstract double fDot(Body o) throws Exception;
}
