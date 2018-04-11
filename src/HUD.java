import java.awt.Image;

import javax.swing.ImageIcon;

public class HUD
{
   public Image resourceHealthBar;
   public Image resourceResourceBar;
   public Image resourceExpBar;
   public Image resourceFrameLeft;
   public Image resourceFrameRight;
   public Image resourceFrameCenter;
   
   
   public HUD()
   {
      resourceHealthBar = new ImageIcon("src/sprites/ui/hud/resource_health_bar.png").getImage();
      resourceResourceBar = new ImageIcon("src/sprites/ui/hud/resource_resource_bar.png").getImage();
      resourceExpBar = new ImageIcon("src/sprites/ui/hud/resource_exp_bar.png").getImage();
      resourceFrameLeft = new ImageIcon("src/sprites/ui/hud/resource_frame_left.png").getImage();
      resourceFrameRight = new ImageIcon("src/sprites/ui/hud/resource_frame_right.png").getImage();
      resourceFrameCenter = new ImageIcon("src/sprites/ui/hud/resource_frame_center.png").getImage();
   }
   
   
}
