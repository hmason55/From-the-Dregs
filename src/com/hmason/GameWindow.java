package com.hmason;
import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.JFrame;

public class GameWindow extends JFrame
{

   public GameWindow()
   {
      Init();
   }

   private void Init()
   {
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
      EventQueue.invokeLater(() ->
      {
         GameWindow window = new GameWindow();
         window.setVisible(true);
      });
   }
}
