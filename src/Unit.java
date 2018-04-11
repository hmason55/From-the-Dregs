import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public class Unit
{
   private Tile tile;
   private String name;
   private boolean playerControlled;

   // Base Attributes
   private int strength; // damage

   private int vitality; // health
   private int fortitude; // resources
   private int tenacity; // resistances
   private int agility; // unit speed

   private int healthRecovery;
   private int maxHealth;
   private int currentHealth;

   private int resourceRecovery;
   private int maxResource;
   private int currentResource;

   private int level;
   private int experience;

   // Combat variables
   private boolean inCombat = false;

   // Appearance
   private Image shadowSource;
   private Image idleSource;

   private Image currentSprite;
   private Image shadowSprite;
   private Image hitSprite;
   private Image[] idleAnimation;

   private final int ANIM_LENGTH = 26;
   private int idleAnimationFrame = 0;

   private Image[] shadowMips;
   private Image[] hitMips;
   private Image[][] idleMips;
   public static final float[] MIP_SCALE =
   { 0.5f, 1f, 2f, 3f };
   public static final int MIP_LEVEL = 1;

   public static final int HIT_FRAME_SKIP = 0;
   private int currentHitFrame = 0;

   // References
   Viewport viewport;

   public Unit(String unitName, boolean isPlayer)
   {
      name = unitName;
      playerControlled = isPlayer;

      strength = 10;
      fortitude = 10;
      vitality = 10;
      tenacity = 10;
      agility = 10;

      calcMaxHealth();
      calcMaxResource();

      calcHealthRecovery();
      calcResourceRecovery();

      resetHealth();
      resetResource();

      level = 1;
      experience = 0;

      loadShadowSprite();
      loadIdleAnimation();
      loadHitSprite();

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

   public int getIdleAnimationFrame()
   {
      return idleAnimationFrame;
   }

   public void incrementIdleAnimation()
   {
      idleAnimationFrame++;
      idleAnimationFrame = idleAnimationFrame % ANIM_LENGTH;

      if (currentHitFrame > -1 && currentHitFrame < HIT_FRAME_SKIP)
      {
         currentHitFrame++;
      } else
      {
         currentHitFrame = -1;
         currentSprite = idleAnimation[idleAnimationFrame];
      }
   }

   public void setMipLevel(int mipLevel)
   {
      if (idleMips != null)
      {
         
         shadowSprite = shadowMips[mipLevel];
         
         for (int i = 0; i < ANIM_LENGTH; i++)
         {
            idleAnimation[i] = idleMips[i][mipLevel];
         }
         
         hitSprite = hitMips[mipLevel];
      }
   }

   private void generateMips()
   {
      if(shadowSource != null)
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
         idleMips = new Image[ANIM_LENGTH][MIP_SCALE.length];
         for (int i = 0; i < ANIM_LENGTH; i++)
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
      } else
      {
         System.out.println("Can't generate mips without a source image.");
      }

   }

   private void loadShadowSprite()
   {
      // Initialize Shadow Sprite
      shadowSource = new ImageIcon("src/sprites/units/unit_shadow.png").getImage();
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
      idleAnimation = new Image[ANIM_LENGTH];
      idleSource = new ImageIcon("src/sprites/units/knight/knight_idle_sheet.png").getImage();
      if (playerControlled)
      {

      } else
      {
         idleSource = new ImageIcon("src/sprites/units/bat/bat_small_idle_sheet.png").getImage();
      }

      for (int i = 0; i < ANIM_LENGTH; i++)
      {
         BufferedImage bufferedImage = new BufferedImage(idleSource.getWidth(null), idleSource.getHeight(null),
               BufferedImage.TYPE_INT_ARGB);
         Graphics2D bGr = bufferedImage.createGraphics();
         bGr.drawImage(idleSource, 0, 0, null);
         bGr.dispose();

         int ndx = calcSheetIndex(i);

         idleAnimation[i] = bufferedImage.getSubimage(ndx * Tile.WIDTH, 0, Tile.WIDTH, Tile.HEIGHT)
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

   public void takeDamage(int damage, Unit dealer)
   {

      currentHealth -= damage;
      currentHitFrame = 0;
      currentSprite = hitSprite;
      System.out.println(name + " took " + damage + " damage.");

      if (dealer.getViewport() != null)
      {
         dealer.getViewport().spawnCombatText(damage + "", tile.getPosition()[0], tile.getPosition()[1], Color.WHITE);
      }

      if (currentHealth <= 0)
      {
         dealer.grantExp(level * 5);
         kill();
      }
   }

   void kill()
   {
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
      int expRequired = (1 + (int) (level * 5 + Math.pow((double) level, 2.25))) - experience;
      if (playerControlled)
      {
         System.out.println(expRequired + " exp until next level.");
      }
      return expRequired;
   }
   
   public int calcExpRequired(int lv)
   {
      return (1 + (int) (lv * 5 + Math.pow((double) level, 2.25)));
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
      tile = t;
   }
   
   public float getHealthPercentage()
   {
      return ((float)currentHealth)/((float)maxHealth);
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
      return ((float)currentResource)/((float)maxResource);
   }
   
   public int getCurrentResource()
   {
      return currentResource;
   }
   
   public int getMaxResource()
   {
      return maxResource;
   }
   
   public float getExpPercentage()
   {
      if(level == 1)
      {
        return ((float)experience)/((float)calcExpRequired(level));
      }
      return ((float)(experience-calcExpRequired(level)))/((float)calcExpRequired(level));
   }
   
   public int getExperience()
   {
      return experience;
   }

}
