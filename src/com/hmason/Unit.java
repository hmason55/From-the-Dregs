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

   public enum AnimationDirection
   {
      Up, Down, Left, Right
   }

   private AnimationDirection animationDirection = AnimationDirection.Up;

   public static final int[] attackOffsetFrames =
   { -4, 12, 8, 4 };

   public static final int[] moveOffsetFrames =
   { -48, -44, -36, -24, -12, -4 };

   // References
   Viewport viewport;

   public Unit(String unitName, boolean isPlayer, Viewport focusedViewport)
   {
      name = unitName;
      playerControlled = isPlayer;
      viewport = focusedViewport;
      myTurn = null;

      if (isPlayer)
      {
         hasLight = true;
         lightStrength = 10;
      }

      statusModifiers = new ArrayList<StatusModifier>();

      baseStrength = 10;
      baseFortitude = 10;
      baseVitality = 10;
      baseTenacity = 10;
      baseSpeed = 10;
      
      resetAttributes();

      calcMaxHealth();
      calcMaxResource();

      calcHealthRecovery();
      calcResourceRecovery();

      resetHealth();
      resetResource();

      level = 0;
      experience = 0;

      loadShadowSprite();
      loadIdleAnimation();
      loadHitSprite();
      loadAttackSprite();

      generateMips();

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

   public void setViewport(Viewport v)
   {
      viewport = v;
   }

   public Viewport getViewport()
   {
      return viewport;
   }

   public void grantExp(int exp)
   {
      experience += exp;
      if (playerControlled)
      {
         System.out.println("Gained " + exp + " exp (" + experience + " total)");
         if (viewport != null)
         {
            viewport.spawnCombatText("+" + exp + " exp", tile.getPosition()[0], tile.getPosition()[1], Color.MAGENTA);
         }
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

   public void beginMoveAnimation(AnimationDirection direction)
   {
      animationDirection = direction;
      currentMoveFrame = 0;
   }

   public void beginAttackAnimation(AnimationDirection direction)
   {
      animationDirection = direction;
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
         switch (animationDirection)
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
         switch (animationDirection)
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

      if (dealer.getViewport() != null)
      {
         dealer.getViewport().spawnCombatText(damage + "", tile.getPosition()[0], tile.getPosition()[1], Color.WHITE);
      }

      if (currentHealth <= 0)
      {
         dealer.grantExp((1 + level) * 5);
         kill();
      }
   }

   void kill()
   {

      viewport.getTurnQueue().removeTurns(this);

      tile.setUnit(null);
      System.out.println(name + " died.");
   }

   public void levelUp()
   {
      level++;
      if (playerControlled)
      {
         System.out.println("Reached level " + level);
         if (viewport != null)
         {
            viewport.spawnCombatText("Level Up!", tile.getPosition()[0], tile.getPosition()[1], Color.CYAN);
         }
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
      healthRecovery = vitality / 8;
   }

   public void calcMaxResource()
   {
      maxResource = fortitude * 3;
   }

   public void calcResourceRecovery()
   {
      resourceRecovery = fortitude / 6;
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

   public Turn getTurn()
   {
      myTurn.setPriority(speed);
      return myTurn;
   }

   public void beginTurn()
   {
      System.out.println("Begin " + name + "'s turn.");
      myTurn = new Turn(this, speed);
   }

   public void endTurn()
   {
      System.out.println("End " + name + "'s turn.");
      tickActionEffects();
      recalculateAttributes();
      viewport.getTurnQueue().endTurn();
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

   public void blockDamage(Unit dealer)
   {

      beginHitAnimation();

      if (dealer.getViewport() != null)
      {
         dealer.getViewport().spawnCombatText("BLOCK", tile.getPosition()[0], tile.getPosition()[1], Color.WHITE);
      }

      if (blockStacks > 0)
      {
         blockStacks--;
      } else
      {
         blockStacks = 0;
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
}
