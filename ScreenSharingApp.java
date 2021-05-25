import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

class MenuActionListener implements ActionListener {
  public void actionPerformed(ActionEvent e) {
    System.out.println("Selected: " + e.getActionCommand());
  }
}

public class ScreenSharingApp {
	static boolean stremaing=false;
	static int viewers=0;
	public static void main(final String args[]) {
    JFrame frame = new JFrame("MenuSample Example");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JMenuBar menuBar = new JMenuBar();

    JMenu File = new JMenu("File");
    File.setMnemonic(KeyEvent.VK_F);
    menuBar.add(File);
    
    JMenu Help = new JMenu("Help");
    Help.setMnemonic(KeyEvent.VK_F);
    menuBar.add(Help);

    JMenuItem connectItem = new JMenuItem("Connect");
    connectItem.addActionListener(new MenuActionListener());
    File.add(connectItem);
    
    JMenuItem disconnectItem = new JMenuItem("Disconnect");
    disconnectItem.addActionListener(new MenuActionListener());
    File.add(disconnectItem);
    
    JMenuItem newMenuItem2 = new JMenuItem("Help");
    newMenuItem2.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        	JOptionPane.showMessageDialog(null, "Coded by Hakan Tunç.\n    20130702016 ");
        }
     });
    
    Help.add(newMenuItem2);
    
    
    
    JButton shareButton=new JButton("Share my screen");  
    shareButton.setBounds(0,0,960,300);  
    frame.add(shareButton);    
    frame.setLayout(null);  
    boolean isStart = true;
    shareButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        	stremaing=true;
        	new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Robot rob = new Robot();
                        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
                        while (true) {
                            ServerSocket serverSocket = new ServerSocket(6767);
                            Socket socket = serverSocket.accept();
                            BufferedImage img = rob.createScreenCapture(new Rectangle(0, 0, (int) d.getWidth(), (int) d.getHeight()));

                            ByteArrayOutputStream sendingImage = new ByteArrayOutputStream();
                            ImageIO.write(img, "png", sendingImage);
                            socket.getOutputStream().write(sendingImage.toByteArray());
                            serverSocket.close();
                            try {
                                Thread.sleep(10);
                            } catch (Exception e) {
                            }
                        }
                    } catch (Exception e) {e.printStackTrace();
                        JOptionPane.showMessageDialog(null, e);
                    }
                }
            }).start();
        	JOptionPane.showMessageDialog(null, "You are streaming. Viewers: "+viewers);
        }
     });
    
    JButton connectButton=new JButton("Connect someone's screen");  
    connectButton.setBounds(0,300,960,300);  
    frame.add(connectButton);    
    frame.setLayout(null); 
    connectButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        	if (stremaing) {
        		JOptionPane.showMessageDialog(null,"You cannot connect while you are streaming");
        	}
        	else {
        	String ip = JOptionPane.showInputDialog("Please enter IP address you want to connect");
        	viewers++;
        	new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            Socket soc = new Socket(ip, 6767);
                            BufferedImage img = ImageIO.read(soc.getInputStream());
                            frame.getGraphics().drawImage(img, 0, 0, frame.getWidth(), frame.getHeight(), null);
                            soc.close();

                            try {
                                Thread.sleep(5);
                            } catch (Exception e) {
                            }
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e);
                    }
                }
            }).start();
        }
        }
     });


    frame.setJMenuBar(menuBar);
    frame.setSize(960, 600);
    frame.setVisible(true);
  }
}
