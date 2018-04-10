import java.awt.EventQueue;
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
      add(new Viewport());
      setSize(1280, 720);
      setTitle("From the Dregs");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setResizable(false);
      setLocationRelativeTo(null);
   }

   public static void main(String[] args)
   {
      // TODO Auto-generated method stub
      EventQueue.invokeLater(() ->
      {
         GameWindow window = new GameWindow();
         window.setVisible(true);

         // ex.add(new Viewport());
      });
   }
}
