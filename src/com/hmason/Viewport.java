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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

public class Viewport extends JPanel implements ActionListener, Runnable
{

   public static int viewWidth = 1280;
   public static int viewHeight = 720;

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

   private Font combatFont;

   private static ArrayList<CombatText> combatText;
   public static TurnQueue turnQueue;

   private ResourceBar healthBar;
   private ResourceBar resourceBar;
   private ResourceBar experienceBar;

   private ActionButton firstAction;
   private ActionButton secondAction;
   private ActionButton thirdAction;
   private ActionButton fourthAction;
   
   public static int enemiesDefeated = 0;
   
   private CharacterMenu characterMenu;
   public static boolean paused = true;

   public static ActionBar actionBar;
   
   @SuppressWarnings("serial")
   private void Init()
   {

     // Start a new thread
      if (drawThread == null)
      {
         drawThread = new Thread((Runnable) this);
         drawThread.start();
      }
      
      // Load font
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

      // Initialize the camera
      cameraTargetX = 0;
      cameraTargetY = 0;

      
      combatText = new ArrayList<CombatText>();
      

      // Initialize the map and spawn units
      map = new Map(MAP_WIDTH, MAP_HEIGHT);
      turnQueue = new TurnQueue();
      player = new Unit("Player", true, Unit.Type.Player, 0);
      map.spawnUnit(player, 10, 10);
      turnQueue.addTurn(new Turn(player, 1));
      
      for(int i = 0; i < 5; i++)
      {
         Unit enemy = map.spawnEnemy(player.getLevel());
         if(enemy != null)
         {
            turnQueue.addTurn(new Turn(enemy, enemy.calcTurnSpeed()));
         }
      }

      turnQueue.start();

      map.repaintFog();

      centerOnPlayer();

      // Initialize the resource bars
      healthBar = new ResourceBar(0, 8, ResourceBar.ResourceType.Health);
      resourceBar = new ResourceBar(0, 24, ResourceBar.ResourceType.Resource);
      experienceBar = new ResourceBar(0, 40, ResourceBar.ResourceType.Experience);

      // Initialize player's the action bar
      firstAction = new ActionButton(0, 0, "[ 1 ]  Light", new Action(Action.Moveset.Forward_Slash));
      secondAction = new ActionButton(0, 0, "[ 2 ]  Heavy", new Action(Action.Moveset.Crushing_Swing));
      thirdAction = new ActionButton(0, 0, "[ 3 ]  Block", new Action(Action.Moveset.Defensive_Stance));

      actionBar = new ActionBar(0, 0);
      actionBar.add(firstAction);
      actionBar.add(secondAction);
      actionBar.add(thirdAction);

      actionBar.selectAction(0);
      
      characterMenu = new CharacterMenu();

      setBackground(Color.BLACK);
      setPreferredSize(new Dimension(viewWidth, viewHeight));
      setDoubleBuffered(true);

      // Initialize hotkeys
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
         
         inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "onF1");
         inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "onF2");
         inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "onF3");
         inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), "onF4");

         // When the "Up" key is pressed
         actionMap.put("onUp", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               if(paused)
               {
                  return;
               }
               onScrollUp();
            }
         });

         // When the "Down" key is pressed
         actionMap.put("onDown", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               if(paused)
               {
                  return;
               }
               onScrollDown();
            }
         });

         // When the "Left" key is pressed
         actionMap.put("onLeft", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               if(paused)
               {
                  return;
               }
               onScrollLeft();
            }
         });
         
         // When the "Right" key is pressed
         actionMap.put("onRight", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               if(paused)
               {
                  return;
               }
               onScrollRight();
            }
         });

         // When the "W" key is pressed
         actionMap.put("onW", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               if(paused)
               {
                  return;
               }
               player.onMove(Unit.Direction.Up);
            }
         });

         // When the "S" key is pressed
         actionMap.put("onS", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               if(paused)
               {
                  return;
               }
               player.onMove(Unit.Direction.Down);
            }
         });
         
         // When the "A" key is pressed
         actionMap.put("onA", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               if(paused)
               {
                  return;
               }
               player.onMove(Unit.Direction.Left);
            }
         });
         
         // When the "A" key is pressed
         actionMap.put("onD", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               if(paused)
               {
                  return;
               }
               player.onMove(Unit.Direction.Right);
            }
         });

         // When the "1" key is pressed
         actionMap.put("on1", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               if(paused)
               {
                  return;
               }
               
               if (player.isMyTurn())
               {
                  actionBar.selectAction(0);
               }
            }
         });

         // When the "2" key is pressed
         actionMap.put("on2", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               if(paused)
               {
                  return;
               }
               
               if (player.isMyTurn())
               {
                  actionBar.selectAction(1);
               }
            }
         });

         // When the "3" key is pressed
         actionMap.put("on3", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               if(paused)
               {
                  return;
               }
               
               if (player.isMyTurn())
               {
                  actionBar.selectAction(2);
               }
            }
         });

         // When the "4" key is pressed
         actionMap.put("on4", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               if(paused)
               {
                  return;
               }
               
               if (player.isMyTurn())
               {
                  // No functionality yet
               }
            }
         });
         
         // When the "F1" key is pressed
         actionMap.put("onF1", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               if(!paused)
               {
                  return;
               }
               
               player.spendAttributePoint(Unit.AttributeType.Strength);
            }
         });
         
         // When the "F2" key is pressed
         actionMap.put("onF2", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               if(!paused)
               {
                  return;
               }
               
               player.spendAttributePoint(Unit.AttributeType.Vitality);
            }
         });
         
         // When the "F3" key is pressed
         actionMap.put("onF3", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               if(!paused)
               {
                  return;
               }
               
               player.spendAttributePoint(Unit.AttributeType.Fortitude);
            }
         });
         
         // When the "F4" key is pressed
         actionMap.put("onF4", new AbstractAction()
         {
            public void actionPerformed(ActionEvent e)
            {
               if(!paused)
               {
                  return;
               }
               
               player.spendAttributePoint(Unit.AttributeType.Tenacity);
            }
         });
      }

      timer = new Timer(DELAY, this);
      timer.start();
   }

   // Moves the camera up by SCROLL_SPEED_Y
   private void onScrollUp()
   {
      if (mapPositionY == cameraTargetY)
      {
         cameraTargetY += SCROLL_SPEED_Y * VIEW_ZOOM;
      }
   }

   // Moves the camera down by SCROLL_SPEED_Y
   private void onScrollDown()
   {
      if (mapPositionY == cameraTargetY)
      {
         cameraTargetY -= SCROLL_SPEED_Y * VIEW_ZOOM;
      }
   }

   // Moves the camera left by SCROLL_SPEED_X
   private void onScrollLeft()
   {
      if (mapPositionX == cameraTargetX)
      {
         cameraTargetX += SCROLL_SPEED_X * VIEW_ZOOM;
      }
   }

   // Moves the camera right by SCROLL_SPEED_X
   private void onScrollRight()
   {
      if (mapPositionX == cameraTargetX)
      {
         cameraTargetX -= SCROLL_SPEED_X * VIEW_ZOOM;
      }
   }

   // Set cameraTargetX and cameraTargetY to the player's position
   public static void centerOnPlayer()
   {
      cameraTargetX = (viewWidth / 2 - Tile.WIDTH / 2 * VIEW_ZOOM)
            - player.getTile().getPosition()[0] * Tile.WIDTH * VIEW_ZOOM;
      cameraTargetY = (viewHeight / 2 - Tile.HEIGHT / 2 * VIEW_ZOOM)
            - player.getTile().getPosition()[1] * Tile.HEIGHT / 2 * VIEW_ZOOM;
   }

   // Moves the screen towards (cameraTargetX, cameraTargetY)
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

   // Add combat text to the viewport
   public static void spawnCombatText(String text, int x, int y, Color color)
   {
      combatText.add(new CombatText(text, x, y, color));
   }

   @Override
   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      viewWidth = getSize().width;
      viewHeight = getSize().height;
      
      if(player.getAttributePoints() > 0)
      {
         paused = true;
      } else {
         paused = false;
      }

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
      //AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
      
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
               g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fog.getDensity()));
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

      // Draw the resource bars
      healthBar.draw(player, g, fm);
      resourceBar.draw(player, g, fm);
      experienceBar.draw(player, g, fm);

      // Draw the action bar
      actionBar.draw(player, g, fm);

      // Draw text displaying current unit's turn
      g.setColor(Color.BLACK);
      g.drawString(turnQueue.getTurnName(), 17, viewHeight - 160);
      g.drawString(turnQueue.getTurnName(), 16, viewHeight - 159);

      g.setColor(Color.WHITE);
      g.drawString(turnQueue.getTurnName(), 16, viewHeight - 160);

      
      // Display level up window
      if(paused)
      {
         characterMenu.draw(player, g, fm);
      }
      
      // Display a game over screen
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
         
         g.drawString("You defeated " + enemiesDefeated + " enemies.", viewWidth/2 - fm.stringWidth("You defeated " + enemiesDefeated + " enemies.") + 2, viewHeight/2 - fm.getHeight() + 32);
         g.drawString("You defeated " + enemiesDefeated + " enemies.", viewWidth/2 - fm.stringWidth("You defeated " + enemiesDefeated + " enemies."), viewHeight/2 - fm.getHeight() + 2 + 32);
         
         g.setColor(Color.WHITE);
         g.drawString("Game Over...", viewWidth/2 - fm.stringWidth("Game Over..."), viewHeight/2 - fm.getHeight());
         g.drawString("You defeated " + enemiesDefeated + " enemies.", viewWidth/2 - fm.stringWidth("You defeated " + enemiesDefeated + " enemies."), viewHeight/2 - fm.getHeight() + 32);
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
   
   public static void togglePause()
   {
      paused = !paused;
   }

   public static boolean isPaused()
   {
      return paused;
   }

}
