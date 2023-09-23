package GUI.GUI;

import Main.Body;

import javax.swing.*;
import java.awt.event.*;
import java.util.LinkedList;

public class Window extends JFrame implements KeyListener, MouseListener, MouseMotionListener {
    public double scale = 1/10.0;
    public double timestep = Math.pow(10,-7.5);
    public double time;
    public LinkedList<Body> bodies = new LinkedList<>();
    public Window(int length, int height){
        super();
        Panel p =new Panel(this);
        this.setSize(length, height);
        this.setTitle("Constraint-Based Physics Sim");
        this.setContentPane(p);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(false);

        this.setVisible(true);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == 'p'){
            Body.DEBUG = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        Body.attract = true;
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        Body.attract = false;
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        Body.MOUSE.x = mouseEvent.getX()-getWidth()/2.0;
        Body.MOUSE.y = getHeight()/2.0-mouseEvent.getY();
    }
}
