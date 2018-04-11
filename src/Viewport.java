import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

public class Viewport extends JPanel implements ActionListener
{

   private final int VIEW_WIDTH = 400;
   private final int VIEW_HEIGHT = 400;

   private final int SCROLL_SPEED_X = 48;
   private final int SCROLL_SPEED_Y = 48;

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
   private int animationSpeed = 30;
   private boolean allowAnimate = true;

   private Unit player;
   private int playerX;
   private int playerY;

   private Font combatFont;

   private HUD hud;

   ArrayList<CombatText> combatText;

   private void Init()
   {
      // Load font
      try
      {
         combatFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/fonts/6px2bus.ttf")).deriveFont(20f);
         GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
         ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("src/fonts/6px2bus.ttf")));
      } catch (IOException e)
      {
         e.printStackTrace();
      } catch (FontFormatException e)
      {
         e.printStackTrace();
      }

      combatText = new ArrayList<CombatText>();

      map = new Map();

      player = new Unit("You", true);
      player.setViewport(this);
      
      map.getTiles()[1][1].setUnit(player);
      player.moveTo(map.getTiles()[1][1]);
      playerX = 1;
      playerY = 1;
      
      hud = new HUD();

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
      if (playerY > 0)
      {
         Tile toTile = map.getTiles()[playerX][playerY - 1];
         if(!toTile.getTerrain().IsWalkable())
         {
            return;
         }
         
         if (toTile.getUnit() == null)
         {
            toTile.setUnit(player);
            player.moveTo(toTile);
            map.getTiles()[playerX][playerY].setUnit(null);
            playerY--;
         } else
         {
            // Begin combat
            toTile.getUnit().takeDamage(15, player);
         }
      }
   }

   private void OnMoveDown()
   {
      if (playerY < map.height - 1)
      {
         Tile toTile = map.getTiles()[playerX][playerY + 1];
         if(!toTile.getTerrain().IsWalkable())
         {
            return;
         }
         
         if (toTile.getUnit() == null)
         {
            toTile.setUnit(player);
            player.moveTo(toTile);
            map.getTiles()[playerX][playerY].setUnit(null);
            playerY++;
            //OnScrollDown();
         } else
         {
         // Begin combat
            toTile.getUnit().takeDamage(15, player);
         }
      }
   }

   private void OnMoveLeft()
   {
      if (playerX > 0)
      {
         Tile toTile = map.getTiles()[playerX - 1][playerY];
         if(!toTile.getTerrain().IsWalkable())
         {
            return;
         }
         
         if (toTile.getUnit() == null)
         {
            toTile.setUnit(player);
            player.moveTo(toTile);
            map.getTiles()[playerX][playerY].setUnit(null);
            playerX--;
            OnScrollLeft();
         } else
         {
            // Begin combat
            toTile.getUnit().takeDamage(15, player);
         }
      }
   }

   private void OnMoveRight()
   {
      if (playerX < map.width - 1)
      {
         Tile toTile = map.getTiles()[playerX + 1][playerY];
         if(!toTile.getTerrain().IsWalkable())
         {
            return;
         }
         
         if (toTile.getUnit() == null)
         {
            toTile.setUnit(player);
            player.moveTo(toTile);
            map.getTiles()[playerX][playerY].setUnit(null);
            playerX++;
            //OnScrollRight();
         } else
         {
            // Begin combat
            toTile.getUnit().takeDamage(15, player);

         }
      }
   }
   
   // Add components to the viewport
   public void spawnCombatText(String text, int x, int y, Color color)
   {
      combatText.add(new CombatText(text, x, y, color));
   }
   
   public void flashScreen()
   {
      
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
               
               // Draw shadow
               g.drawImage(unit.getShadowSprite(), mapPositionX + Tile.WIDTH * VIEW_ZOOM * x,
                     mapPositionY + Tile.HEIGHT * VIEW_ZOOM * y, this);
               
               // Draw character
               g.drawImage(unit.getSprite(), mapPositionX + Tile.WIDTH * VIEW_ZOOM * x,
                     mapPositionY + Tile.HEIGHT * VIEW_ZOOM * y, this);
            }

         }
      }

      // Display Combat Text
      for (CombatText text : combatText)
      {
         int scaledPositionX = Math.round(mapPositionX + (text.getOrigin()[0] * Tile.WIDTH * VIEW_ZOOM + (Tile.WIDTH * VIEW_ZOOM) / 2
               - text.getText().length()*combatFont.getSize()*0.30f) + text.getPosition()[0] * VIEW_ZOOM);
         int scaledPositionY = Math.round(mapPositionY
               + (text.getOrigin()[1] * Tile.HEIGHT * VIEW_ZOOM + Tile.HEIGHT / 2) + text.getPosition()[1] * VIEW_ZOOM);

         g.setFont(combatFont);

         // Draw font outline
         g.setColor(Color.BLACK);
         g.drawString(text.getText(), scaledPositionX + 2, scaledPositionY + 2);

         // Draw font
         g.setColor(text.getColor());
         g.drawString(text.getText(), scaledPositionX, scaledPositionY);
      }

      // Remove combat text when it has expired
      for (int i = combatText.size() - 1; i >= 0; i--)
      {
         CombatText text = combatText.get(i);
         if (text.getTimeRemaining() > 0)
         {
            text.incrementTime();
         } else
         {
            combatText.remove(i);
         }
      }
      
      // Draw Health bar
      g.drawImage(hud.resourceHealthBar, 320, 592, (int)(player.getHealthPercentage()*640), 16, this);
      g.drawImage(hud.resourceFrameLeft, 305, 592, this);
      g.drawImage(hud.resourceFrameCenter, 320, 592, 640, 16, this);
      g.drawImage(hud.resourceFrameRight, 958, 592, this);
      
      // Draw Resource bar
      g.drawImage(hud.resourceResourceBar, 320, 604, (int)(player.getResourcePercentage()*640), 16, this);
      g.drawImage(hud.resourceFrameLeft, 305, 604, this);
      g.drawImage(hud.resourceFrameCenter, 320, 604, 640, 16, this);
      g.drawImage(hud.resourceFrameRight, 958, 604, this);
      
      // Draw Exp bar
      g.drawImage(hud.resourceExpBar, 320, 616, (int)(player.getExpPercentage()*640), 16, this);
      g.drawImage(hud.resourceFrameLeft, 305, 616, this);
      g.drawImage(hud.resourceFrameCenter, 320, 616, 640, 16, this);
      g.drawImage(hud.resourceFrameRight, 958, 616, this);
      
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
