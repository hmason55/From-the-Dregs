package com.hmason;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Unit
{
   private Tile tile;
   private String name;
   private boolean playerControlled;
   private Turn myTurn;

   private boolean hasLight = false;
   private int lightStrength;

   // Base Attributes
   private int baseStrength;
   private int baseVitality;
   private int baseFortitude;
   private int baseTenacity;
   private int baseSpeed;

   private int level;
   private int experience;

   // Total Attributes (after combat effects are applied)
   private int strength; // damage

   private int vitality; // health
   private int fortitude; // resources
   private int tenacity; // resistances
   private int speed; // unit speed

   private int healthRecovery;
   private int maxHealth;
   private int currentHealth;

   private int resourceRecovery;
   private int maxResource;
   private int currentResource;

   // Combat variables
   ArrayList<StatusModifier> statusModifiers;
   private boolean inCombat = false;
   private int blockStacks = 0;

   // Appearance
   private Image shadowSource;
   private Image idleSource;

   private Image currentSprite;
   private Image shadowSprite;
   private Image hitSprite;
   private Image attackSprite;
   private Image[] idleAnimation;

   private final int IDLE_FRAMES = 4;
   private final int IDLE_ANIM_LENGTH = 26;
   private final float IDLE_ANIM_SPEED = 0.4f;
   private int idleAnimationFrame = 0;

   private Image[] shadowMips;
   private Image[] hitMips;
   private Image[] attackMips;
   private Image[][] idleMips;
   public static final float[] MIP_SCALE =
   { 0.5f, 1f, 2f, 3f };
   public static final int MIP_LEVEL = 1;

   public static final int MOVE_FRAME_SKIP = 6;
   public static final int HIT_FRAME_SKIP = 1;
   public static final int ATTACK_FRAME_SKIP = 4;
   private int currentMoveFrame = 0;
   private int currentHitFrame = 0;
   private int currentAttackFrame = 0;

   public enum Direction
   {
      Up, Down, Left, Right
   }

   private Direction direction = Direction.Up;

   public enum Type
   {
      Player, Bat
   }

   private Type type;

   public static final int[] attackOffsetFrames =
   { -4, 12, 8, 4 };

   public static final int[] moveOffsetFrames =
   { -48, -44, -36, -24, -12, -4 };

   public Unit(String unitName, boolean isPlayer, Type type, int level)
   {
      name = unitName;
      playerControlled = isPlayer;
      this.type = type;
      this.level = level;
      myTurn = null;

      if (isPlayer)
      {
         hasLight = true;
         lightStrength = 6;
      }

      statusModifiers = new ArrayList<StatusModifier>();

      switch (type)
      {
      case Player:
         baseStrength = 5;
         baseFortitude = 5;
         baseVitality = 5;
         baseTenacity = 5;
         baseSpeed = 5;
         break;

      case Bat:
         baseStrength = 2 + level;
         baseFortitude = 2 + level;
         baseVitality = 2 + level;
         baseTenacity = 2 + level;
         baseSpeed = 2 + level;
         break;
      }

      resetAttributes();

      calcMaxHealth();
      calcMaxResource();

      calcHealthRecovery();
      calcResourceRecovery();

      resetHealth();
      resetResource();

      experience = 0;

      loadShadowSprite();
      loadIdleAnimation();
      loadHitSprite();
      loadAttackSprite();

      generateMips();

   }

   public String getName()
   {
      return name;
   }

   public Tile getTile()
   {
      return tile;
   }

   public Image[] getAnimation()
   {
      return idleAnimation;
   }

   public Image getSprite()
   {
      return currentSprite;
   }

   public Image getShadowSprite()
   {
      return shadowSprite;
   }

   public Image getHitSprite()
   {
      return hitSprite;
   }

   public Image getAttackSprite()
   {
      return attackSprite;
   }

   public int getIdleAnimationFrame()
   {
      return idleAnimationFrame;
   }

   public void incrementAnimation()
   {
      idleAnimationFrame++;
      idleAnimationFrame = idleAnimationFrame % ((int) (IDLE_ANIM_LENGTH / IDLE_ANIM_SPEED));

      if (currentMoveFrame > -1 && currentMoveFrame < MOVE_FRAME_SKIP)
      {
         currentMoveFrame++;
      } else if (currentHitFrame > -1 && currentHitFrame < HIT_FRAME_SKIP)
      {
         currentHitFrame++;
      } else if (currentAttackFrame > -1 && currentAttackFrame < ATTACK_FRAME_SKIP)
      {
         currentAttackFrame++;
      } else
      {
         currentAttackFrame = -1;
         currentHitFrame = -1;

         if (idleAnimationFrame < (int) (10 / IDLE_ANIM_SPEED))
         {
            currentSprite = idleAnimation[0];
         } else if (idleAnimationFrame < (int) (11 / IDLE_ANIM_SPEED))
         {
            currentSprite = idleAnimation[1];
         } else if (idleAnimationFrame < (int) (22 / IDLE_ANIM_SPEED))
         {
            currentSprite = idleAnimation[2];
         } else
         {
            currentSprite = idleAnimation[3];
         }
         // currentSprite = idleAnimation[idleAnimationFrame];
      }
   }

   public void setMipLevel(int mipLevel)
   {
      if (idleMips != null)
      {

         shadowSprite = shadowMips[mipLevel];

         for (int i = 0; i < IDLE_FRAMES; i++)
         {
            idleAnimation[i] = idleMips[i][mipLevel];
         }

         hitSprite = hitMips[mipLevel];
         attackSprite = attackMips[mipLevel];
      }
   }

   private void generateMips()
   {
      if (shadowSource != null)
      {
         shadowMips = new Image[MIP_SCALE.length];
         for (int i = 0; i < MIP_SCALE.length; i++)
         {
            shadowMips[i] = shadowSource.getScaledInstance((int) (Tile.WIDTH * MIP_SCALE[i]),
                  (int) (Tile.HEIGHT * MIP_SCALE[i]), Image.SCALE_FAST);
         }
      }

      if (idleSource != null)
      {
         idleMips = new Image[IDLE_FRAMES][MIP_SCALE.length];
         for (int i = 0; i < IDLE_FRAMES; i++)
         {
            for (int j = 0; j < MIP_SCALE.length; j++)
            {

               idleMips[i][j] = idleAnimation[i].getScaledInstance((int) (Tile.WIDTH * MIP_SCALE[j]),
                     (int) (Tile.HEIGHT * MIP_SCALE[j]), Image.SCALE_FAST);
            }
         }

         hitMips = new Image[MIP_SCALE.length];
         for (int i = 0; i < MIP_SCALE.length; i++)
         {
            hitMips[i] = hitSprite.getScaledInstance((int) (Tile.WIDTH * MIP_SCALE[i]),
                  (int) (Tile.HEIGHT * MIP_SCALE[i]), Image.SCALE_FAST);
         }

         attackMips = new Image[MIP_SCALE.length];
         for (int i = 0; i < MIP_SCALE.length; i++)
         {
            attackMips[i] = attackSprite.getScaledInstance((int) (Tile.WIDTH * MIP_SCALE[i]),
                  (int) (Tile.HEIGHT * MIP_SCALE[i]), Image.SCALE_FAST);
         }
      } else
      {
         System.out.println("Can't generate mips without a source image.");
      }

   }

   private void loadShadowSprite()
   {
      // Initialize Shadow Sprite
      shadowSource = new ImageIcon(FileLoader.loadImage("sprites/units/unit_shadow.png")).getImage();
      BufferedImage bufferedImage = new BufferedImage(shadowSource.getWidth(null), shadowSource.getWidth(null),
            BufferedImage.TYPE_INT_ARGB);
      Graphics2D bGr = bufferedImage.createGraphics();
      bGr.drawImage(shadowSource, 0, 0, null);
      bGr.dispose();
      shadowSprite = bufferedImage.getScaledInstance(Tile.WIDTH, Tile.HEIGHT, Image.SCALE_FAST);
   }

   private void loadIdleAnimation()
   {
      // Initialize Idle Animation
      idleAnimation = new Image[IDLE_FRAMES];
      idleSource = new ImageIcon(FileLoader.loadImage("sprites/units/knight/knight_idle_sheet.png")).getImage();
      if (playerControlled)
      {

      } else
      {
         idleSource = new ImageIcon(FileLoader.loadImage("sprites/units/bat/bat_small_idle_sheet.png")).getImage();
      }

      for (int i = 0; i < IDLE_FRAMES; i++)
      {
         BufferedImage bufferedImage = new BufferedImage(idleSource.getWidth(null), idleSource.getHeight(null),
               BufferedImage.TYPE_INT_ARGB);
         Graphics2D bGr = bufferedImage.createGraphics();
         bGr.drawImage(idleSource, 0, 0, null);
         bGr.dispose();

         idleAnimation[i] = bufferedImage.getSubimage(i * Tile.WIDTH, 0, Tile.WIDTH, Tile.HEIGHT)
               .getScaledInstance(Tile.WIDTH, Tile.HEIGHT, Image.SCALE_FAST);
      }
   }

   private void loadHitSprite()
   {
      // Initialize Hit Sprite
      BufferedImage bufferedImage = new BufferedImage(idleAnimation[0].getWidth(null), idleAnimation[0].getWidth(null),
            BufferedImage.TYPE_INT_ARGB);
      Graphics2D bGr = bufferedImage.createGraphics();
      bGr.drawImage(idleAnimation[0], 0, 0, null);
      bGr.dispose();

      for (int y = 0; y < bufferedImage.getHeight(); y++)
      {
         for (int x = 0; x < bufferedImage.getWidth(); x++)
         {
            // Turn pixel white if it's not transparent
            if ((bufferedImage.getRGB(x, y) >> 24) != 0x00)
            {
               bufferedImage.setRGB(x, y, Color.WHITE.getRGB());
            }
         }
      }

      hitSprite = bufferedImage.getScaledInstance(Tile.WIDTH, Tile.HEIGHT, Image.SCALE_FAST);
   }

   private void loadAttackSprite()
   {
      // Initialize Attack Sprite
      BufferedImage bufferedImage = new BufferedImage(idleAnimation[0].getWidth(null), idleAnimation[0].getWidth(null),
            BufferedImage.TYPE_INT_ARGB);
      Graphics2D bGr = bufferedImage.createGraphics();
      bGr.drawImage(idleAnimation[0], 0, 0, null);
      bGr.dispose();

      attackSprite = bufferedImage.getScaledInstance(Tile.WIDTH, Tile.HEIGHT, Image.SCALE_FAST);
   }

   private int calcSheetIndex(int index)
   {
      if (index < 10)
      {
         return 0;
      } else if (index < 11)
      {
         return 1;
      } else if (index < 22)
      {
         return 2;
      }
      return 3;
   }

   public void grantExp(int exp)
   {
      experience += exp;
      if (playerControlled)
      {
         System.out.println("Gained " + exp + " exp (" + experience + " total)");
         Viewport.spawnCombatText("+" + exp + " exp", tile.getPosition()[0], tile.getPosition()[1], Color.MAGENTA);
      }

      boolean done = false;
      while (!done)
      {
         if (calcExpToLevel() <= 0)
         {
            levelUp();
         } else
         {
            done = true;
         }

      }
   }

   public void beginMoveAnimation(Direction direction)
   {
      this.direction = direction;
      currentMoveFrame = 0;
   }

   public void beginAttackAnimation(Direction direction)
   {
      this.direction = direction;
      currentAttackFrame = 0;
      currentSprite = attackSprite;
   }

   public void beginHitAnimation()
   {
      currentHitFrame = 0;
      currentSprite = hitSprite;
   }

   public int[] getAnimationOffsetPosition()
   {
      int[] offset =
      { 0, 0 };

      if (currentMoveFrame > -1 && currentMoveFrame < MOVE_FRAME_SKIP)
      {
         switch (direction)
         {
         case Up:
            offset[0] = 0;
            offset[1] = -moveOffsetFrames[currentMoveFrame % moveOffsetFrames.length];
            break;

         case Down:
            offset[0] = 0;
            offset[1] = moveOffsetFrames[currentMoveFrame % moveOffsetFrames.length];
            break;

         case Left:
            offset[0] = -moveOffsetFrames[currentMoveFrame % moveOffsetFrames.length];
            offset[1] = 0;
            break;

         case Right:
            offset[0] = moveOffsetFrames[currentMoveFrame % moveOffsetFrames.length];
            offset[1] = 0;
            break;
         }
      } else if (currentHitFrame > -1 && currentHitFrame < HIT_FRAME_SKIP)
      {
         offset[0] = (int) (Math.random() * 9) - 4;
         offset[1] = (int) (Math.random() * 9) - 4;
      } else if (currentAttackFrame > -1 && currentAttackFrame < ATTACK_FRAME_SKIP)
      {
         switch (direction)
         {
         case Up:
            offset[0] = 0;
            offset[1] = -attackOffsetFrames[currentAttackFrame % attackOffsetFrames.length];
            break;

         case Down:
            offset[0] = 0;
            offset[1] = attackOffsetFrames[currentAttackFrame % attackOffsetFrames.length];
            break;

         case Left:
            offset[0] = -attackOffsetFrames[currentAttackFrame % attackOffsetFrames.length];
            offset[1] = 0;
            break;

         case Right:
            offset[0] = attackOffsetFrames[currentAttackFrame % attackOffsetFrames.length];
            offset[1] = 0;
            break;
         }
      }
      return offset;
   }

   public void takeDamage(int damage, Unit dealer)
   {

      currentHealth -= damage;
      beginHitAnimation();
      System.out.println(name + " took " + damage + " damage.");

      Viewport.spawnCombatText(damage + "", tile.getPosition()[0], tile.getPosition()[1], Color.WHITE);

      if (currentHealth <= 0)
      {
         dealer.grantExp((1 + level) * 5);
         currentHealth = 0;
         kill();
      }
   }

   void kill()
   {

      Viewport.turnQueue.removeTurns(this);

      tile.setUnit(null);
      System.out.println(name + " died.");
   }

   public void levelUp()
   {
      level++;
      if (playerControlled)
      {
         System.out.println("Reached level " + level);
         Viewport.spawnCombatText("Level Up!", tile.getPosition()[0], tile.getPosition()[1], Color.CYAN);
      }
   }

   public int calcExpToLevel()
   {
      int expRequired = calcExpRequired(level + 1) - experience;
      if (playerControlled)
      {
         System.out.println(expRequired + " exp until next level.");
      }
      return expRequired;
   }

   public int calcExpRequired(int lv)
   {
      return ((int) (lv * 5 + Math.pow((double) (lv), 2.25)));
   }

   public void calcMaxHealth()
   {
      maxHealth = vitality * 4;
   }

   public void calcHealthRecovery()
   {
      healthRecovery = 1 + (vitality / 8);
   }

   public void calcMaxResource()
   {
      maxResource = fortitude * 3;
   }

   public void calcResourceRecovery()
   {
      resourceRecovery = 1 + (fortitude / 6);
   }

   public int getResistance()
   {
      return tenacity / 5;
   }

   public void resetHealth()
   {
      currentHealth = maxHealth;
   }

   public void resetResource()
   {
      currentResource = maxResource;
   }

   public void moveTo(Tile t)
   {
      if (tile != null)
      {
         tile.setLightStrength(-1);
      }

      tile = t;

      if (hasLight)
      {
         tile.setLightStrength(lightStrength);
      }
   }

   public float getHealthPercentage()
   {
      return ((float) currentHealth) / ((float) maxHealth);
   }

   public int getCurrentHealth()
   {
      return currentHealth;
   }

   public int getMaxHealth()
   {
      return maxHealth;
   }

   public float getResourcePercentage()
   {
      return ((float) currentResource) / ((float) maxResource);
   }

   public int getCurrentResource()
   {
      return currentResource;
   }

   public int getMaxResource()
   {
      return maxResource;
   }

   public int getLevel()
   {
      return level;
   }

   public float getExpPercentage()
   {
      return ((float) (getExpIntoLevel())) / ((float) calcExpRequired(level + 1));
   }

   public int getExpIntoLevel()
   {
      return experience - calcExpRequired(level);
   }

   public int getLevelExp()
   {
      return calcExpRequired(level + 1) - calcExpRequired(level);
   }

   public int getExperience()
   {
      return experience;
   }

   public boolean isMyTurn()
   {
      if (myTurn == null)
      {
         return false;
      }
      return true;
   }

   private int calcTurnSpeed()
   {
      return 1 + (int) (10f - (speed * 0.40f));
   }

   public Turn getTurn()
   {
      myTurn.setPriority(calcTurnSpeed());
      return myTurn;
   }

   public void beginTurn()
   {
      System.out.println("Begin " + name + "'s turn.");
      myTurn = new Turn(this, calcTurnSpeed());
      if (isPlayerControlled())
      {
         currentHealth += healthRecovery;
         currentResource += resourceRecovery;

         if (currentHealth > maxHealth)
         {
            currentHealth = maxHealth;
         }

         if (currentResource > maxResource)
         {
            currentResource = maxResource;
         }
      }
   }

   public void endTurn()
   {
      System.out.println("End " + name + "'s turn.");
      tickActionEffects();
      recalculateAttributes();
      Viewport.turnQueue.endTurn();
      myTurn = null;
   }

   public boolean isPlayerControlled()
   {
      return playerControlled;
   }

   public String toString()
   {
      return name;
   }

   public int getStrength()
   {
      return strength;
   }

   public int getBlockStacks()
   {
      return blockStacks;
   }

   public void setBlockStacks(int stacks)
   {
      blockStacks = stacks;
   }

   public boolean blockDamage(Unit dealer)
   {
      int blockCost = 1 + (int) (maxResource * 0.20f);
      if (blockStacks > 0 && currentResource >= blockCost)
      {
         beginHitAnimation();
         blockStacks--;
         currentResource -= blockCost;
         Viewport.spawnCombatText("BLOCK", tile.getPosition()[0], tile.getPosition()[1], Color.WHITE);
         return true;
      } else
      {
         return false;
      }
   }

   public void applyActionEffects(Action action)
   {
      ArrayList<StatusModifier> mods = action.getModifiers();
      for (StatusModifier mod : mods)
      {
         if (mod.isSelfEffect())
         {
            StatusModifier m = new StatusModifier(mod.isSelfEffect(), mod.getType(), mod.getValue(), mod.getDuration());
            statusModifiers.add(m);
         }
      }
   }

   public void tickActionEffects()
   {
      // Remove expired effects
      for (int i = statusModifiers.size() - 1; i >= 0; i--)
      {
         StatusModifier mod = statusModifiers.get(i);
         if (mod.getDuration() > 0)
         {
            mod.setDuration(mod.getDuration() - 1);
         } else if (mod.getDuration() == 0)
         {
            statusModifiers.remove(i);
         }
      }
   }

   public void recalculateAttributes()
   {
      resetAttributes();

      // Multiplying effects
      for (StatusModifier mod : statusModifiers)
      {
         switch (mod.getType())
         {
         case multSpeed:
            speed *= (int) (speed * mod.getValue());
            break;
         case multStrength:
            strength = (int) (strength * mod.getValue());
            break;
         }
      }

      // Incremental effects
      for (StatusModifier mod : statusModifiers)
      {
         switch (mod.getType())
         {
         case incrementBlockStacks:
            blockStacks += (int) mod.getValue();
            break;
         case incrementSpeed:
            speed += (int) mod.getValue();
            break;
         case incrementStrength:
            strength += (int) mod.getValue();
            break;
         }
      }

      // Set effects
      for (StatusModifier mod : statusModifiers)
      {
         switch (mod.getType())
         {
         case setBlockStacks:
            blockStacks = (int) mod.getValue();
            break;
         case setSpeed:
            speed = (int) mod.getValue();
            break;
         case setStrength:
            strength = (int) mod.getValue();
            break;
         }
      }
   }

   public void resetAttributes()
   {
      strength = baseStrength;
      vitality = baseVitality;
      fortitude = baseFortitude;
      tenacity = baseTenacity;
      speed = baseSpeed;
      blockStacks = 0;
   }

   public void printAttributes()
   {
      System.out.println("Strength: " + strength);
      System.out.println("Vitality: " + vitality);
      System.out.println("Fortitude: " + fortitude);
      System.out.println("Tenacity: " + tenacity);
      System.out.println("Speed: " + speed);
      System.out.println("Blocks: " + blockStacks);
   }

   public void onMove(Direction direction)
   {
      if (!isMyTurn())
      {
         return;
      }

      int x = tile.getPosition()[0];
      int y = tile.getPosition()[1];

      switch (direction)
      {
      case Down:
         if (y >= Viewport.map.getHeight() - 1)
         {
            Viewport.turnQueue.addTurn(myTurn);
            endTurn();
            return;
         } else
         {
            y++;
         }
         break;
      case Left:
         if (x <= 0)
         {
            Viewport.turnQueue.addTurn(myTurn);
            endTurn();
            return;
         } else
         {
            x--;
         }
         break;
      case Right:
         if (x >= Viewport.map.getWidth() - 1)
         {
            Viewport.turnQueue.addTurn(myTurn);
            endTurn();
            return;
         } else
         {
            x++;
         }
         break;
      case Up:
         if (y <= 0)
         {
            Viewport.turnQueue.addTurn(myTurn);
            endTurn();
            return;
         } else
         {
            y--;
         }
         break;
      default:
         Viewport.turnQueue.addTurn(myTurn);
         endTurn();
         return;
      }

      this.direction = direction;
      Tile toTile = Viewport.map.getTiles()[x][y];
      if (!toTile.getTerrain().isWalkable())
      {
         Viewport.turnQueue.addTurn(myTurn);
         endTurn();
         return;
      }

      if (toTile.getUnit() == null)
      {
         // Move unit
         toTile.setUnit(this);
         tile.setUnit(null);
         moveTo(toTile);
         beginMoveAnimation(direction);
         recalculateAttributes();
         if (isPlayerControlled())
         {
            Viewport.centerOnPlayer();
         }
      } else
      {
         onAttack(toTile);
      }

      Viewport.turnQueue.addTurn(myTurn);
      Viewport.map.repaintFog();
      endTurn();
   }

   public void onAttack(Tile targetTile)
   {
      // Combat
      Unit target = targetTile.getUnit();
      if(target == null)
      {
         return;
      }
      
      Action action = new Action(Action.Moveset.Bite);
      if (isPlayerControlled())
      {
         action = Viewport.actionBar.getSelectedAction();
      }

      applyActionEffects(action);
      recalculateAttributes();

      if (!target.blockDamage(this))
      {
         int damage = (int) (strength * action.getStrengthScaling()) - target.getResistance();

         if (damage < 0)
         {
            damage = 0;
         }

         target.takeDamage(damage, this);
      }

      beginAttackAnimation(direction);
      recalculateAttributes();
   }

   public void aiTurn()
   {
      if (!isMyTurn())
      {
         return;
      }

      if (isPlayerControlled())
      {
         return;
      }

      int x = tile.getPosition()[0];
      int y = tile.getPosition()[1];
      int targetX = Viewport.player.getTile().getPosition()[0];
      int targetY = Viewport.player.getTile().getPosition()[1];

      // Melee attack
      if(Math.abs(targetX - x) + Math.abs(targetY - y) == 1)
      {
         if (targetX == x + 1 && targetY == y)
         {
            direction = Direction.Right;
         } else if (targetX == x - 1 && targetY == y)
         {
            direction = Direction.Left;
         } else if (targetX == x && targetY == y + 1)
         {
            direction = Direction.Down;
         } else if (targetX == x && targetY == y - 1)
         {
            direction = Direction.Up;
         }
         onAttack(Viewport.player.getTile());
         Viewport.turnQueue.addTurn(myTurn);
         endTurn();
      } else {
   
         ArrayList<PathNode> path = findPath(x, y, targetX, targetY);
   
         if (path.size() < 2)
         {
            Viewport.turnQueue.addTurn(myTurn);
            endTurn();
            return;
         }
         
         targetX = path.get(path.size()-2).getPosition()[0];
         targetY = path.get(path.size()-2).getPosition()[1];
         
         if (targetX == x + 1 && targetY == y)
         {
            direction = Direction.Right;
         } else if (targetX == x - 1 && targetY == y)
         {
            direction = Direction.Left;
         } else if (targetX == x && targetY == y + 1)
         {
            direction = Direction.Down;
         } else if (targetX == x && targetY == y - 1)
         {
            direction = Direction.Up;
         }
   
         onMove(direction);
      }
   }

   // BEGIN AI PATHING
   // ------------------------------------------------------------------------------------------------
   private ArrayList<PathNode> findPath(int startX, int startY, int endX, int endY)
   {
      PathNode current = null;
      PathNode start = new PathNode(startX, startY);
      PathNode end = new PathNode(endX, endY);
      ArrayList<PathNode> openNodes = new ArrayList<PathNode>();
      ArrayList<PathNode> closedNodes = new ArrayList<PathNode>();
      boolean[][] walkableTiles = getWalkableTiles(startX, startY, endX, endY);
      int distanceFromStart = 0;
      openNodes.add(start);

      openLoop: while (openNodes.size() > 0)
      {
         // Find node with the lowest total distance
         int lowest = Integer.MAX_VALUE;
         for (PathNode node : openNodes)
         {
            int distance = node.getTotalDistance();
            if (distance < lowest)
            {
               lowest = distance;
               current = node;
            }
         }

         closedNodes.add(current);
         openNodes.remove(current);

         // Check if the end node is in the closed list to end early
         for (PathNode node : closedNodes)
         {
            if (node.getPosition()[0] == end.getPosition()[0] && node.getPosition()[1] == end.getPosition()[1])
            {
               // end early
               break openLoop;
            }
         }

         ArrayList<PathNode> neighborNodes = getWalkableNeighborNodes(current.getPosition()[0],
               current.getPosition()[1], walkableTiles);
         distanceFromStart++;

         outer: for (PathNode neighborNode : neighborNodes)
         {
            // If closed nodes already contain this node then skip it
            for (PathNode node : closedNodes)
            {
               if (node.getPosition()[0] == neighborNode.getPosition()[0]
                     && node.getPosition()[1] == neighborNode.getPosition()[1])
               {
                  continue outer;
               }
            }

            // Check if it's in the open list
            boolean inOpenNodes = false;
            for (PathNode node : openNodes)
            {
               if (node.getPosition()[0] == neighborNode.getPosition()[0]
                     && node.getPosition()[1] == neighborNode.getPosition()[1])
               {
                  inOpenNodes = true;
                  continue;
               }
            }

            if (inOpenNodes)
            {
               // Check if this is a better path
               if (distanceFromStart + neighborNode.getTrueDistanceFromEnd() < neighborNode.getTotalDistance())
               {
                  neighborNode.setDistanceFromStart(distanceFromStart);
                  neighborNode
                        .setTotalDistance(neighborNode.getDistanceFromStart() + neighborNode.getTrueDistanceFromEnd());
                  neighborNode.setParentNode(current);
               }
            } else
            {
               neighborNode.setDistanceFromStart(distanceFromStart);
               neighborNode.setTrueDistanceFromEnd(computeTrueDistanceFromEnd(neighborNode.getPosition()[0],
                     neighborNode.getPosition()[1], endX, endY));
               neighborNode
                     .setTotalDistance(neighborNode.getDistanceFromStart() + neighborNode.getTrueDistanceFromEnd());
               neighborNode.setParentNode(current);
               openNodes.add(0, neighborNode);
            }
         }
      }

      // Create path
      ArrayList<PathNode> path = new ArrayList<PathNode>();
      while (current != null)
      {
         path.add(current);
         current = current.getParentNode();
      }

      return path;
   }

   private int computeTrueDistanceFromEnd(int x, int y, int targetX, int targetY)
   {
      return Math.abs(targetX - x) + Math.abs(targetY - y);
   }

   boolean[][] getWalkableTiles(int startX, int startY, int endX, int endY)
   {

      int width = Viewport.map.getWidth();
      int height = Viewport.map.getHeight();
      Tile[][] tiles = Viewport.map.getTiles();

      boolean[][] walkableTiles = new boolean[width][height];

      for (int y = 0; y < height; y++)
      {
         for (int x = 0; x < width; x++)
         {
            if (tiles[x][y] == null)
            {
               walkableTiles[x][y] = false;
               continue;
            }

            if (!tiles[x][y].getTerrain().isWalkable())
            {
               walkableTiles[x][y] = false;
               continue;
            }

            if (tiles[x][y].getUnit() == null)
            {
               walkableTiles[x][y] = true;
               continue;
            }

            if ((x == startX && y == startY) || (x == endX && y == endY))
            {
               walkableTiles[x][y] = true;
               continue;
            }

         }
      }
      return walkableTiles;
   }

   ArrayList<PathNode> getWalkableNeighborNodes(int x, int y, boolean[][] walkableTiles)
   {
      ArrayList<PathNode> testNodes = new ArrayList<PathNode>();

      if (y > 0)
      {
         testNodes.add(new PathNode(x, y - 1));
      }

      if (y < Viewport.MAP_HEIGHT - 1)
      {
         testNodes.add(new PathNode(x, y + 1));
      }

      if (x > 0)
      {
         testNodes.add(new PathNode(x - 1, y));
      }

      if (x < Viewport.MAP_WIDTH - 1)
      {
         testNodes.add(new PathNode(x + 1, y));
      }

      for (int i = testNodes.size() - 1; i >= 0; i--)
      {
         PathNode node = testNodes.get(i);
         if (!walkableTiles[node.getPosition()[0]][node.getPosition()[1]])
         {
            testNodes.remove(i);
         }
      }

      return testNodes;
   }

   // END AI PATHING
   // --------------------------------------------------------------------------------------------------
}
