In this assignment you will practice using an ArrayList. Your class will support a sample program that demonstrates some of the kinds of operations a windowing system must perform. Instead of windows, you will be manipulating “tiles.” A tile consists of a position (specified by the upper-left corner x and y), a width, a height and a color. Positions and distances are specified in terms of pixels, with the upper-left corner being (0, 0) and the x and y coordinates increasing as you move left and down, respectively.
You won’t have to understand a lot about tiles and coordinates. That code has been written for you. You are writing a small part of the overall system that keeps track of the list of tiles. You are required to implement this using an ArrayList. The only method you need to use from the Tile class is the following:
// returns true if the given (x, y) point falls inside this tile
public boolean inside(int x, int y)
Your class is to be called TileList and must implement the following public methods:
// post: constructs an empty tile list
public TileList()
// post: searches through the list of tiles and returns a
//       reference to the last tile for which (x, y) is
//       inside the tile; returns null if (x, y) is not
//       inside any tile of the list; moves the found tile
//       to the back of the list
public Tile moveToBack(int x, int y)
// post: inserts t at the back of the list of tiles
public void insertBack(Tile t)
// post: returns the number of tiles in this list
public int size()
// post: returns the Tile at the given index
public Tile get(int index)
The file TileMain.java contains the main driver program. Once you have written your solution to TileList and it compiles, try compiling and running TileMain. You should be able to create rectangles by clicking and dragging to indicate the endpoints. You should also be able to click on a tile to bring it to the top and you should be able to click and drag to move a tile to a new location

Tile.java
import java.awt.*;

public class Tile {
    private int x;
    private int y;
    private int width;
    private int height;
    private Color color;

    public Tile(int x, int y, int width, int height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        if (width < 0) {
            this.width = -this.width;
            this.x = this.x - this.width;
        }
        if (this.height < 0) {
            this.height = -this.height;
            this.y = this.y - this.height;
        }
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void translate(int deltaX, int deltaY) {
        x += deltaX;
        y += deltaY;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setColor(Color.black);
        g.drawRect(x, y, width, height);
    }

    public boolean inside(int x, int y) {
        if (x < this.x || x > this.x + width) {
            return false;
        } else if (y < this.y || y > this.y + height) {
            return false;
        } else {
            return true;
        }
    }
}	TileFrame.java
// This class is the top-level frame for a set of colored tiles.

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TileFrame extends JFrame {
    private TilePanel panel;

    public TileFrame() {
        setSize(400, 400);
        setTitle("Fun with Tiles");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        panel = new TilePanel(Color.RED);
        add(panel, BorderLayout.CENTER);
        addColorButtons();
    }
        
    private void addColorButtons() {
        JPanel p = new JPanel();
        p.setBackground(Color.CYAN);
        ButtonGroup g = new ButtonGroup();
        addButton(p, g, "Green", false, Color.GREEN);
        addButton(p, g, "Blue", false, Color.BLUE);
        addButton(p, g, "Red", true, Color.RED);
        add(p, BorderLayout.NORTH);
    }
        
    private void addButton(JPanel p, ButtonGroup g, String name,
                           boolean selected, final Color color) {
        JRadioButton button = new JRadioButton(name, selected);
        p.add(button);
        g.add(button);
        button.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    panel.setColor(color);
                }
            }
        });
    }
}

TileMain.java
// This program allows a user to explore a system of overlapping tiles
// that has similar properties to a windowing system.  Click and drag the
// mouse to create rectangles.  Click on tiles to bring them to the front.
// Click and drag a rectangle to move it.

public class TileMain {
    public static void main(String[] args) {
        TileFrame frame = new TileFrame();
        frame.setVisible(true);
    }
}	TileListener.java
// This class responds to mouse events on a tile.

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

public class TileListener extends MouseInputAdapter {
    private TileList tiles;
    private TilePanel parent;
    private Point firstSpot;
    private Point lastSpot;
    private Tile current;

    public TileListener(TileList tiles, TilePanel parent) {
        this.tiles = tiles;
        this.parent = parent;
    }
                        
    public void mouseClicked(MouseEvent e) {
        parent.repaint();
    }
                        
    public void mousePressed(MouseEvent e) {
        firstSpot = lastSpot = e.getPoint();
        current = tiles.moveToBack(e.getX(), e.getY());
    }
                        
    public void mouseReleased(MouseEvent e) {
        int deltaX = lastSpot.x - firstSpot.x;
        int deltaY = lastSpot.y - firstSpot.y;
        Point newSpot = e.getPoint();
        if (current == null) {
            if (deltaX != 0 && deltaY != 0) {
                Tile nextTile = new Tile(firstSpot.x, firstSpot.y, deltaX, 
                                         deltaY, parent.getColor());
                tiles.insertBack(nextTile);
            }
        } else {
            current.translate(newSpot.x - firstSpot.x,
                              newSpot.y - firstSpot.y);
        }
        parent.repaint();
    }
                        
    private void drawBorder(Graphics g) {
        int deltaX = lastSpot.x - firstSpot.x;
        int deltaY = lastSpot.y - firstSpot.y;
        if (current != null) {
            g.drawRect(current.getX() + deltaX, current.getY() + deltaY, 
                       current.getWidth(), current.getHeight());
        } else {
            int cornerX, cornerY;
            if (deltaX < 0) {
                cornerX = firstSpot.x + deltaX;
            } else {
                cornerX = firstSpot.x;
            }
            if (deltaY < 0) {
                cornerY = firstSpot.y + deltaY;
            } else {
                cornerY = firstSpot.y;
            }
            g.drawRect(cornerX, cornerY, Math.abs(deltaX), Math.abs(deltaY));
        }
    }
        
    public void mouseDragged(MouseEvent e) {
        Graphics g = parent.getGraphics();
        g.setXORMode(parent.getBackground());
        drawBorder(g);
        lastSpot = e.getPoint();
        drawBorder(g);
        g.dispose();
    }
}	// This class keeps track of a set of colored tiles in a panel.

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

public class TilePanel extends JPanel {
    private TileList tiles;
    private Color color;

    public TilePanel(Color color) {
        setBackground(Color.WHITE);
        tiles = new TileList();
        this.color = color;
        MouseInputListener listener = new TileListener(tiles, this);
        addMouseListener(listener);
        addMouseMotionListener(listener);
    }
        
    public Color getColor() {
        return color;
    }
        
    public void setColor(Color color) {
        this.color = color;
    }
        
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < tiles.size(); i++) {
            tiles.get(i).draw(g);
        }
    }
}
 
