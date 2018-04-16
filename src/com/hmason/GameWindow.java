package com.hmason;
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameWindow extends JFrame
{

   public GameWindow()
   {
      Init();
   }

   private void Init()
   {
      
      //add(new HUD());
      add(new Viewport());
      setBackground(Color.BLACK);
      setSize(1280, 720);
      setTitle("From the Dregs");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setResizable(true);
      setLocationRelativeTo(null);
   }

   public static void main(String[] args)
   {
      // TODO Auto-generated method stub
      EventQueue.invokeLater(() ->
      {
         GameWindow window = new GameWindow();
         window.setVisible(true);
      });
   }
}
