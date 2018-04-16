package com.hmason;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class HUD extends JLayeredPane implements ActionListener
{
   public Image resourceHealthBar;
   public Image resourceResourceBar;
   public Image resourceExpBar;
   public Image resourceFrameLeft;
   public Image resourceFrameRight;
   public Image resourceFrameCenter;

   private JPanel bottomBar;
   public Image skillIcon;
   public HUD()
   {/*
      resourceHealthBar = new ImageIcon("sprites/ui/hud/resource_health_bar.png").getImage();
      resourceResourceBar = new ImageIcon("sprites/ui/hud/resource_resource_bar.png").getImage();
      resourceExpBar = new ImageIcon("sprites/ui/hud/resource_exp_bar.png").getImage();
      resourceFrameLeft = new ImageIcon("sprites/ui/hud/resource_frame_left.png").getImage();
      resourceFrameRight = new ImageIcon("sprites/ui/hud/resource_frame_right.png").getImage();
      resourceFrameCenter = new ImageIcon("sprites/ui/hud/resource_frame_center.png").getImage();
      
      skillIcon = new ImageIcon("src/sprites/ui/hud/sword_icon.png").getImage();*/

      //add(new JButton("ACC"));
      
      
   }


   @Override
   public void actionPerformed(ActionEvent e)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);

   }
   
   private void drawHUD(Graphics g)
   {
      g.drawImage(resourceHealthBar, 640, 360, this);
   }
   
   //public 
   
}
