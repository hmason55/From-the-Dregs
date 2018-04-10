import java.awt.BorderLayout;

import javax.swing.*;

public class display_proto
{

   public static void main(String[] args)
   {
      // TODO Auto-generated method stub
      JFrame window = new JFrame("FTD 1.0");
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      window.getContentPane().add(new JLabel("a"), BorderLayout.CENTER);

      // 4. Size the frame.
      window.pack();

      // 5. Show it.
      window.setVisible(true);
   }

}
