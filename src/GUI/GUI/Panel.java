package GUI.GUI;

import Main.Body;
import Main.DistConstraint;
import Main.Point;

import javax.swing.*;
import java.awt.*;

public class Panel extends JPanel {
    private Window parentWindow;

    public Panel(Window p){
        this.parentWindow = p;
    }
    public void paintComponent(Graphics g){
        int i = 0;
        double error = 0;
        for (Body b:parentWindow.bodies) {
            try {
                b.paint(g,parentWindow);
                for (int j = 0; j < b.constraints.size(); j++) {
                    DistConstraint c = (DistConstraint) b.constraints.get(j);
                    Point p = c.o.pos.minus(b.pos);
                    error += p.dot(p)-c.r*c.r;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            g.setColor(Color.BLACK);
            g.drawString("corr_F:"+b.f+"|"+Math.toDegrees(Math.atan2(b.f.y, b.f.x)),0,30+10*i);
            i++;
        }
        g.setColor(Color.BLACK);
        g.drawString("Time: " + parentWindow.time + "\n",0,10);
        g.drawString("Error: " + error + "\n",0,20);
        if (Body.ERROR){
            g.setColor(Color.red);
            g.drawString("MATRIX IS UNSTABLE",parentWindow.getWidth()-150,10);
        }
    }

    public Window getParentWindow() {
        return parentWindow;
    }
}
