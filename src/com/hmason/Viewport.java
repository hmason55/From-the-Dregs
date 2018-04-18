package com.hmason;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

public class Viewport extends JPanel implements ActionListener, Runnable
{

   private static int viewWidth = 1280;
   private static int viewHeight = 720;

   private final int SCROLL_SPEED_X = 48;
   private final int SCROLL_SPEED_Y = 48;

   private final static int VIEW_ZOOM = 3;

   private final int DELAY = 25;

   private final int CAMERA_SPEED = 4;
   private static int cameraTargetX;
   private static int cameraTargetY;

   public Viewport()
   {
      Init();
   }

   private Thread drawThread;
   private Thread inputThread;
   private Timer timer;
   public static Map map;
   public static int MAP_WIDTH = 16;
   public static int MAP_HEIGHT = 16;
   private int mapPositionX = 0;
   private int mapPositionY = 0;

   private int gameOverFrame = 0;
   private int frameCounter = 0;
   private int animationSpeed = 90;
   private static final int ANIMATION_FRAME_SKIP = 2;
   private boolean allowAnimate = true;

   public static Unit player;
   private static int playerX;
   private static int playerY;

   private Font combatFont;

   private HUD hud;

   private static ArrayList<CombatText> combatText;
   public static TurnQueue turnQueue;

   private ResourceBar healthBar;
   private ResourceBar resourceBar;
   private ResourceBar experienceBar;

   private ActionButton firstAction;
   private ActionButton secondAction;
   private ActionButton thirdAction;
   private ActionButton fourthAction;

   public static ActionBar actionBar;

   private void Init()
   {

      // Load font

      if (drawThread == null)
      {
         drawThread = new Thread((Runnable) this);
         drawThread.start();
      }

      try
      {
         Font font = Font.createFont(Font.TRUETYPE_FONT, FileLoader.loadResource("fonts/6px2bus.ttf"));
         combatFont = font.deriveFont(20f);
         GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
         ge.registerFont(font);
      } catch (IOException e)
      {
         e.printStackTrace();
      } catch (FontFormatException e)
      {
         e.printStackTrace();
      }

      cameraTargetX = 0;
      cameraTargetY = 0;

      combatText = new ArrayList<CombatText>();
      turnQueue = new TurnQueue();

      map = new Map(MAP_WIDTH, MAP_HEIGHT);

      player = new Unit("Player", true, Unit.Type.Player, 0);
      map.spawnUnit(player, 5, 6);

      Unit bat1 = new Unit("Bat", false, Unit.Type.Bat, 0);
      map.spawnUnit(bat1, 2, 3);

      Unit bat2 = new Unit("Bat", false, Unit.Type.Bat, 0);
      map.spawnUnit(bat2, 2, 6);

      Unit bat3 = new Unit("Bat", false, Unit.Type.Bat, 0);
      map.spawnUnit(bat3, 1, 7);
      
      Unit bat4 = new Unit("Bat", false, Unit.Type.Bat, 1);
      map.spawnUnit(bat4, 5, 8);
      
      Unit bat5 = new Unit("Bat", false, Unit.Type.Bat, 1);
      map.spawnUnit(bat5, 6, 6);

      turnQueue.addTurn(new Turn(player, 1));
      turnQueue.addTurn(new Turn(bat1, 1));
      turnQueue.addTurn(new Turn(bat2, 1));
      turnQueue.addTurn(new Turn(bat3, 1));
      turnQueue.addTurn(new Turn(bat4, 1));
      turnQueue.addTurn(new Turn(bat5, 1));

      turnQueue.start();

      map.repaintFog();

      centerOnPlayer();

      healthBar = new ResourceBar(0, 8, ResourceBar.ResourceType.Health);
      resourceBar = new ResourceBar(0, 24, ResourceBar.ResourceType.Resource);
      experienceBar = new ResourceBar(0, 40, ResourceBar.ResourceType.Experience);

      firstAction = new ActionButton(0, 0, "[ 1 ]  Light", new Action(Action.Moveset.Forward_Slash));
      secondAction = new ActionButton(0, 0, "[ 2 ]  Heavy", new Action(Action.Moveset.Crushing_Swing));
      thirdAction = new ActionButton(0, 0, "[ 3 ]  Block", new Action(Action.Moveset.Defensive_Stance));
      // fourthAction = new ActionButton(0, 0, "4", new
      // Action(Action.Moveset.Defensive_Stance));

      actionBar = new ActionBar(0, 0, 4);
      actionBar.add(firstAction);
      actionBar.add(secondAction);
      actionBar.add(thirdAction);
      // actionBar.add(fourthAction);
      actionBar.selectAction(0);

      setBackground(Color.BLACK);
      setPreferredSize(new Dimension(viewWidth, viewHeight));
      setDoubleBuffered(true);

      InputMap inputMap = getInputMap(WHEN_FOCUSED);
      ActionMap actionMap = getActionMap();

      // Camera Controls
      inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "onUp");
      inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "onDown");
      inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "onLeft");
      inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "onRight");

      if (player.isMyTurn())
      {
         // Movement Controls
         inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), "onW");
         inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "onS");
         inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "onA");
         inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "onD");

         inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_1, 0), "on1");
         inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_2, 0), "on2");
         inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_3, 0), "on3");
         inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_4, 0), "on4");

         actionMap.put("onUp", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               // Enter pressed
               onScrollUp();
            }
         });

         actionMap.put("onDown", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               // Enter pressed
               onScrollDown();
            }
         });

         actionMap.put("onLeft", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               // Enter pressed
               onScrollLeft();
            }
         });

         actionMap.put("onRight", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               // Enter pressed
               onScrollRight();
            }
         });

         actionMap.put("onW", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               player.onMove(Unit.Direction.Up);
            }
         });

         actionMap.put("onS", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               player.onMove(Unit.Direction.Down);
            }
         });

         actionMap.put("onA", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               player.onMove(Unit.Direction.Left);
            }
         });

         actionMap.put("onD", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               player.onMove(Unit.Direction.Right);
            }
         });

         actionMap.put("on1", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               if (player.isMyTurn())
               {
                  actionBar.selectAction(0);
               }
            }
         });

         actionMap.put("on2", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               if (player.isMyTurn())
               {
                  actionBar.selectAction(1);
               }
            }
         });

         actionMap.put("on3", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               if (player.isMyTurn())
               {
                  actionBar.selectAction(2);
               }
            }
         });

         actionMap.put("on4", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               if (player.isMyTurn())
               {
                  // actionBar.selectAction(3);
               }
            }
         });
      }

      timer = new Timer(DELAY, this);
      timer.start();
   }

   private void onScrollUp()
   {
      if (mapPositionY == cameraTargetY)
      {
         cameraTargetY += SCROLL_SPEED_Y * VIEW_ZOOM;
      }
   }

   private void onScrollDown()
   {
      if (mapPositionY == cameraTargetY)
      {
         cameraTargetY -= SCROLL_SPEED_Y * VIEW_ZOOM;
      }
   }

   private void onScrollLeft()
   {
      if (mapPositionX == cameraTargetX)
      {
         cameraTargetX += SCROLL_SPEED_X * VIEW_ZOOM;
      }
   }

   private void onScrollRight()
   {
      if (mapPositionX == cameraTargetX)
      {
         cameraTargetX -= SCROLL_SPEED_X * VIEW_ZOOM;
      }
   }

   public static void centerOnPlayer()
   {
      cameraTargetX = (viewWidth / 2 - Tile.WIDTH / 2 * VIEW_ZOOM)
            - player.getTile().getPosition()[0] * Tile.WIDTH * VIEW_ZOOM;
      cameraTargetY = (viewHeight / 2 - Tile.HEIGHT / 2 * VIEW_ZOOM)
            - player.getTile().getPosition()[1] * Tile.HEIGHT / 2 * VIEW_ZOOM;
   }

   private void moveCamera()
   {
      // Move towards X position
      if (Math.abs(cameraTargetX - mapPositionX) < CAMERA_SPEED)
      {
         mapPositionX = cameraTargetX;
      } else if (mapPositionX < cameraTargetX)
      {
         mapPositionX += CAMERA_SPEED;
      } else if (mapPositionX > cameraTargetX)
      {
         mapPositionX -= CAMERA_SPEED;
      }

      // Move towards Y position
      if (Math.abs(cameraTargetY - mapPositionY) < CAMERA_SPEED / 2)
      {
         mapPositionY = cameraTargetY;
      } else if (mapPositionY < cameraTargetY)
      {
         mapPositionY += CAMERA_SPEED / 2;
      } else if (mapPositionY > cameraTargetY)
      {
         mapPositionY -= CAMERA_SPEED / 2;
      }
   }

   private void onMoveUp()
   {
      if (playerY > 0)
      {
         Tile toTile = map.getTiles()[playerX][playerY - 1];
         if (!toTile.getTerrain().isWalkable())
         {
            return;
         }

         if (toTile.getUnit() == null)
         {
            toTile.setUnit(player);
            player.moveTo(toTile);
            map.getTiles()[playerX][playerY].setUnit(null);
            player.beginMoveAnimation(Unit.Direction.Up);
            playerY--;
            centerOnPlayer();
         } else
         {
            if (player.isMyTurn())
            {
               toTile.getUnit().takeDamage(2, player);
               player.beginAttackAnimation(Unit.Direction.Up);
               // turnQueue.addTurn(player.getTurn());
               // player.endTurn();
            }
         }
      }
      map.repaintFog();
   }

   private void onMoveDown()
   {
      if (!player.isMyTurn())
      {
         return;
      }

      if (playerY < map.getHeight() - 1)
      {
         Tile toTile = map.getTiles()[playerX][playerY + 1];
         if (!toTile.getTerrain().isWalkable())
         {
            return;
         }

         if (toTile.getUnit() == null)
         {
            // Move unit
            toTile.setUnit(player);
            player.moveTo(toTile);
            map.getTiles()[playerX][playerY].setUnit(null);
            player.beginMoveAnimation(Unit.Direction.Down);
            playerY++;

            player.recalculateAttributes();

            turnQueue.addTurn(player.getTurn());
            player.endTurn();

            centerOnPlayer();
         } else
         {
            // Combat
            Unit target = toTile.getUnit();
            Action action = actionBar.getSelectedAction();
            player.applyActionEffects(action);
            player.recalculateAttributes();

            if (target.getBlockStacks() > 0)
            {
               target.blockDamage(player);
            } else
            {
               int damage = (int) (player.getStrength() * action.getStrengthScaling()) - target.getResistance();

               if (damage < 0)
               {
                  damage = 0;
               }

               target.takeDamage(damage, player);
            }

            player.beginAttackAnimation(Unit.Direction.Down);
            player.recalculateAttributes();

            turnQueue.addTurn(player.getTurn());
            player.endTurn();

         }
         // player.printAttributes();
      }
      map.repaintFog();
   }

   private void onMoveLeft()
   {
      if (playerX > 0)
      {
         Tile toTile = map.getTiles()[playerX - 1][playerY];
         if (!toTile.getTerrain().isWalkable())
         {
            return;
         }

         if (toTile.getUnit() == null)
         {
            toTile.setUnit(player);
            player.moveTo(toTile);
            map.getTiles()[playerX][playerY].setUnit(null);
            player.beginMoveAnimation(Unit.Direction.Left);
            playerX--;
            centerOnPlayer();
         } else
         {
            if (player.isMyTurn())
            {
               toTile.getUnit().takeDamage(2, player);
               player.beginAttackAnimation(Unit.Direction.Left);
               // turnQueue.addTurn(player.getTurn());
               // player.endTurn();
            }
         }
      }
      map.repaintFog();
   }

   private void onMoveRight()
   {
      if (playerX < map.getWidth() - 1)
      {
         Tile toTile = map.getTiles()[playerX + 1][playerY];
         if (!toTile.getTerrain().isWalkable())
         {
            return;
         }

         if (toTile.getUnit() == null)
         {
            toTile.setUnit(player);
            player.moveTo(toTile);
            map.getTiles()[playerX][playerY].setUnit(null);
            player.beginMoveAnimation(Unit.Direction.Right);
            playerX++;
            centerOnPlayer();
         } else
         {
            if (player.isMyTurn())
            {
               toTile.getUnit().takeDamage(2, player);
               player.beginAttackAnimation(Unit.Direction.Right);
               // turnQueue.addTurn(player.getTurn());
               // player.endTurn();
            }

         }
      }
      map.repaintFog();
   }

   // Add components to the viewport
   public static void spawnCombatText(String text, int x, int y, Color color)
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
      viewWidth = getSize().width;
      viewHeight = getSize().height;

      // primaryAction.setPosition(16, viewHeight-primaryAction.getHeight() - 32);
      actionBar.setPosition(16, viewHeight - 112);

      if ((frameCounter % ANIMATION_FRAME_SKIP) == ANIMATION_FRAME_SKIP - 1)
      {
         allowAnimate = true;
         frameCounter = 0;

      } else
      {
         allowAnimate = false;
         frameCounter++;
      }

      try
      {
         Thread.sleep(1000 / animationSpeed);
      } catch (InterruptedException e)
      {
         e.printStackTrace();
      }
      moveCamera();
      

      drawMap(g);
   }

   private void drawMap(Graphics g)
   {

      Graphics2D g2d = (Graphics2D) g.create();
      FontMetrics fm = g2d.getFontMetrics();
      AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
      
      // Draw Terrain
      for (int y = 0; y < map.getHeight(); y++)
      {
         for (int x = 0; x < map.getWidth(); x++)
         {
            Tile tile = map.getTiles()[x][y];
            if (tile.getFog().getDensity() < 1.0f)
            {
               Terrain terrain = tile.getTerrain();
               if (terrain != null)
               {
                  terrain.setMipLevel(VIEW_ZOOM);
                  g.drawImage(terrain.getSprite(), mapPositionX + Tile.WIDTH * VIEW_ZOOM * x,
                        mapPositionY + Tile.HEIGHT / 2 * VIEW_ZOOM * y, this);
               }
            }
         }
      }

      // Draw Units
      for (int y = 0; y < map.getHeight(); y++)
      {
         for (int x = 0; x < map.getWidth(); x++)
         {
            Tile tile = map.getTiles()[x][y];

            // Draw Unit
            Unit unit = tile.getUnit();
            if (unit != null)
            {
               // Move to next frame
               if (allowAnimate)
               {
                  unit.incrementAnimation();
               }

               unit.setMipLevel(VIEW_ZOOM);

               // Draw shadow
               g.drawImage(unit.getShadowSprite(),
                     mapPositionX + Tile.WIDTH * VIEW_ZOOM * x + (unit.getAnimationOffsetPosition()[0] * VIEW_ZOOM),
                     mapPositionY + Tile.HEIGHT / 2 * VIEW_ZOOM * y
                           + (unit.getAnimationOffsetPosition()[1] * VIEW_ZOOM) / 2 - (Tile.HEIGHT / 2 * VIEW_ZOOM),
                     this);

               // Draw character
               g.drawImage(unit.getSprite(),
                     mapPositionX + Tile.WIDTH * VIEW_ZOOM * x + (unit.getAnimationOffsetPosition()[0] * VIEW_ZOOM),
                     mapPositionY + Tile.HEIGHT / 2 * VIEW_ZOOM * y
                           + (unit.getAnimationOffsetPosition()[1] * VIEW_ZOOM) / 2 - (Tile.HEIGHT / 2 * VIEW_ZOOM),
                     this);
            }

         }
      }

      // Draw Fog
      for (int y = 0; y < map.getHeight(); y++)
      {
         for (int x = 0; x < map.getWidth(); x++)
         {

            Tile tile = map.getTiles()[x][y];
            Fog fog = tile.getFog();
            if (fog != null)
            {

               fog.setMipLevel(VIEW_ZOOM);
               g2d.setComposite(ac.getInstance(AlphaComposite.SRC_OVER, fog.getDensity()));
               g2d.drawImage(fog.getSprite(), mapPositionX + Tile.WIDTH * VIEW_ZOOM * x,
                     mapPositionY + Tile.HEIGHT / 2 * VIEW_ZOOM * y, this);
            }

         }
      }

      // Display Combat Text
      for (CombatText text : combatText)
      {
         g.setFont(combatFont);
         int scaledPositionX = Math.round(mapPositionX
               + (text.getOrigin()[0] * Tile.WIDTH * VIEW_ZOOM
                     + (Tile.WIDTH / 2 - ((float) fm.stringWidth(text.getText()) / 2)) * VIEW_ZOOM)
               + text.getPosition()[0] * VIEW_ZOOM - text.getOrigin()[0] * VIEW_ZOOM);
         int scaledPositionY = Math
               .round(mapPositionY + (text.getOrigin()[1] * Tile.HEIGHT / 2 * VIEW_ZOOM - Tile.HEIGHT / 2)
                     + text.getPosition()[1] * VIEW_ZOOM + text.getOrigin()[1] * VIEW_ZOOM);

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

      healthBar.draw(player, g, fm);
      resourceBar.draw(player, g, fm);
      experienceBar.draw(player, g, fm);

      actionBar.draw(player, g, fm);

      g.setColor(Color.BLACK);
      g.drawString(turnQueue.getTurnName(), 17, viewHeight - 160);
      g.drawString(turnQueue.getTurnName(), 16, viewHeight - 159);

      g.setColor(Color.WHITE);
      g.drawString(turnQueue.getTurnName(), 16, viewHeight - 160);

      if(player.getCurrentHealth() <= 0)
      {
         if(allowAnimate)
         {
            gameOverFrame++;
         }
         
         float frame = ((float)(gameOverFrame))/100f;
         if(frame > 1.0f)
         {
            frame = 1.0f;
         }
         g2d.setColor(Color.BLACK);
         g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, frame));
         g2d.fillRect(0, 0, viewWidth, viewHeight);
         g.setFont(combatFont);
         
         g.setColor(Color.BLACK);
         g.drawString("Game Over...", viewWidth/2 - fm.stringWidth("Game Over...") + 2, viewHeight/2 - fm.getHeight());
         g.drawString("Game Over...", viewWidth/2 - fm.stringWidth("Game Over..."), viewHeight/2 - fm.getHeight() + 2);
         
         g.setColor(Color.WHITE);
         g.drawString("Game Over...", viewWidth/2 - fm.stringWidth("Game Over..."), viewHeight/2 - fm.getHeight());
         turnQueue.clearTurns();
      }

      Toolkit.getDefaultToolkit().sync();
      repaint();
   }

   @Override
   public void actionPerformed(ActionEvent e)
   {

   }

   public TurnQueue getTurnQueue()
   {
      return turnQueue;
   }

   @Override
   public void run()
   {
      // TODO Auto-generated method stub
   }

}
