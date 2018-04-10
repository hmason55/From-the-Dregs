import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

public class Viewport extends JPanel implements ActionListener
{

   private final int VIEW_WIDTH = 400;
   private final int VIEW_HEIGHT = 400;

   private final int SCROLL_SPEED_X = 8;
   private final int SCROLL_SPEED_Y = 8;

   private final int VIEW_ZOOM = 2;

   private final int DELAY = 25;

   public Viewport()
   {
      Init();
   }

   private Timer timer;
   private Map map;
   private int mapPositionX = 0;
   private int mapPositionY = 0;

   private int frameCounter = 0;
   private int animationSpeed = 15;
   private boolean allowAnimate = true;

   private Unit player;
   private int playerX;
   private int playerY;

   private void Init()
   {
      map = new Map();

      player = new Unit();
      map.getTiles()[1][1].setUnit(player);
      playerX = 1;
      playerY = 1;

      setBackground(Color.BLACK);
      setPreferredSize(new Dimension(VIEW_WIDTH, VIEW_HEIGHT));
      setDoubleBuffered(true);

      InputMap inputMap = getInputMap(WHEN_FOCUSED);
      ActionMap actionMap = getActionMap();

      // Camera Controls
      inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "onUp");
      inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "onDown");
      inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "onLeft");
      inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "onRight");

      // Movement Controls
      inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), "onW");
      inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "onS");
      inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "onA");
      inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "onD");

      actionMap.put("onUp", new AbstractAction()
      {
         public void actionPerformed(ActionEvent e)
         {
            // Enter pressed
            OnScrollUp();
         }
      });

      actionMap.put("onDown", new AbstractAction()
      {
         public void actionPerformed(ActionEvent e)
         {
            // Enter pressed
            OnScrollDown();
         }
      });

      actionMap.put("onLeft", new AbstractAction()
      {
         public void actionPerformed(ActionEvent e)
         {
            // Enter pressed
            OnScrollLeft();
         }
      });

      actionMap.put("onRight", new AbstractAction()
      {
         public void actionPerformed(ActionEvent e)
         {
            // Enter pressed
            OnScrollRight();
         }
      });

      actionMap.put("onW", new AbstractAction()
      {
         public void actionPerformed(ActionEvent e)
         {
            // Enter pressed
            OnMoveUp();
         }
      });

      actionMap.put("onS", new AbstractAction()
      {
         public void actionPerformed(ActionEvent e)
         {
            // Enter pressed
            OnMoveDown();
         }
      });

      actionMap.put("onA", new AbstractAction()
      {
         public void actionPerformed(ActionEvent e)
         {
            // Enter pressed
            OnMoveLeft();
         }
      });

      actionMap.put("onD", new AbstractAction()
      {
         public void actionPerformed(ActionEvent e)
         {
            // Enter pressed
            OnMoveRight();
         }
      });

      timer = new Timer(DELAY, this);
      timer.start();
   }

   private void OnScrollUp()
   {
      mapPositionY += SCROLL_SPEED_Y * VIEW_ZOOM;
   }

   private void OnScrollDown()
   {
      mapPositionY -= SCROLL_SPEED_Y * VIEW_ZOOM;
   }

   private void OnScrollLeft()
   {
      mapPositionX += SCROLL_SPEED_X * VIEW_ZOOM;
   }

   private void OnScrollRight()
   {
      mapPositionX -= SCROLL_SPEED_X * VIEW_ZOOM;
   }

   private void OnMoveUp()
   {
      // mapPositionY += SCROLL_SPEED_Y;
      if (playerY > 0)
      {
         Tile toTile = map.getTiles()[playerX][playerY - 1];
         if (toTile.getUnit() == null)
         {
            toTile.setUnit(player);
            map.getTiles()[playerX][playerY].setUnit(null);
            playerY--;
         } else
         {
            // Begin combat
         }
      }
   }

   private void OnMoveDown()
   {
      if (playerY < map.height-1)
      {
         Tile toTile = map.getTiles()[playerX][playerY + 1];
         if (toTile.getUnit() == null)
         {
            toTile.setUnit(player);
            map.getTiles()[playerX][playerY].setUnit(null);
            playerY++;
         } else
         {
            // Begin combat
         }
      }
   }

   private void OnMoveLeft()
   {
      // mapPositionX += SCROLL_SPEED_X;
      if (playerX > 0)
      {
         Tile toTile = map.getTiles()[playerX - 1][playerY];
         if (toTile.getUnit() == null)
         {
            toTile.setUnit(player);
            map.getTiles()[playerX][playerY].setUnit(null);
            playerX--;
         } else
         {
            // Begin combat
         }
      }
   }

   private void OnMoveRight()
   {
      if (playerX < map.width-1)
      {
         Tile toTile = map.getTiles()[playerX + 1][playerY];
         if (toTile.getUnit() == null)
         {
            toTile.setUnit(player);
            map.getTiles()[playerX][playerY].setUnit(null);
            playerX++;
         } else
         {
            // Begin combat
         }
      }
   }

   @Override
   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);

      drawMap(g);

   }

   private void drawMap(Graphics g)
   {

      // Draw Tiles
      for (int y = 0; y < map.height; y++)
      {
         for (int x = 0; x < map.width; x++)
         {
            Tile tile = map.getTiles()[x][y];

            // Draw Terrain
            Terrain terrain = tile.getTerrain();
            if (terrain != null)
            {
               terrain.setMipLevel(VIEW_ZOOM);
               g.drawImage(terrain.getSprite(), mapPositionX + Tile.WIDTH * VIEW_ZOOM * x,
                     mapPositionY + Tile.HEIGHT * VIEW_ZOOM * y, this);
            }

            // Draw Unit
            Unit unit = tile.getUnit();
            if (unit != null)
            {
               // Move to next frame
               if (allowAnimate)
               {
                  unit.incrementIdleAnimation();
               }

               unit.setMipLevel(VIEW_ZOOM);
               g.drawImage(unit.getSprite(), mapPositionX + Tile.WIDTH * VIEW_ZOOM * x,
                     mapPositionY + Tile.HEIGHT * VIEW_ZOOM * y, this);
            }

         }
      }

      // Limit animation speed
      if (frameCounter++ > 1000 / animationSpeed)
      {
         frameCounter = 0;
         allowAnimate = true;
      } else
      {
         allowAnimate = false;
      }

      Toolkit.getDefaultToolkit().sync();
      repaint();
   }

   @Override
   public void actionPerformed(ActionEvent e)
   {

   }

}
