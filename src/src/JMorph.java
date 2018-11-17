import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class JMorph extends JFrame {

    private int MAX_IMAGE_SIZE = 400;
    private GriddedImage leftGrid, rightGrid;
    private JPanel panel, controls, images, leftPanel, rightPanel;
    private JButton uploadLeft, uploadRight, quit, reset;
    private BufferedImage leftImage, rightImage;
    private JSlider speedSlider;
    private JLabel speedLabel, pls;
    static int rows = 11, cols = 11;




    public JMorph(){
        Container c = this.getContentPane();

        final JFileChooser fc = new JFileChooser(".");
        panel  = new JPanel();
        images = new JPanel();
        leftPanel = new JPanel();
        rightPanel = new JPanel();
        controls = new JPanel();
        uploadLeft = new JButton("Upload Left Image");
        uploadRight = new JButton("Upload Right Image");
        quit = new JButton("Quit");
        reset = new JButton("Reset");
        speedSlider = new JSlider(0,10,5);
        speedLabel = new JLabel("Adjust the speed  of the animation.    --->");
        images.setLayout(new GridLayout(1,2));
        images.add(leftPanel);
        images.add(rightPanel);
        panel.setLayout(new GridLayout(2,1,5,5));
        controls.setLayout(new GridLayout(3,2));

        quit.addActionListener(e -> System.exit(0));

        reset.addActionListener(e -> {});

        uploadRight.addActionListener(
                new ActionListener() {
                    public void actionPerformed (ActionEvent e) {
                        rightPanel.removeAll();
                        int returnVal = fc.showOpenDialog(null);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            String name = file.getAbsolutePath();
                            try {
                                rightImage = ImageIO.read(new File(name));
                            } catch (IOException e1){};

                            rightImage = resize(rightImage, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE);
                            pls = new JLabel("", new ImageIcon(rightImage), JLabel.CENTER);
                            rightPanel.add(pls, BorderLayout.CENTER);
                            rightPanel.revalidate();
                            rightPanel.repaint();
                        }
                    }
                }
        );
        uploadLeft.addActionListener(
                new ActionListener() {
                    public void actionPerformed (ActionEvent e) {
                        leftPanel.removeAll();
                        int returnVal = fc.showOpenDialog(null);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            String name = file.getAbsolutePath();
                            try {
                                leftImage = ImageIO.read(new File(name));
                            } catch (IOException e1){};

                            leftImage = resize(leftImage, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE);
                            pls = new JLabel("", new ImageIcon(leftImage), JLabel.CENTER);
                            leftPanel.add(pls, BorderLayout.CENTER);
                            leftPanel.revalidate();
                            leftPanel.repaint();

                        }
                    }
                }
        );

        speedSlider.setMinorTickSpacing(1);
        speedSlider.setMajorTickSpacing(5);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);


        controls.add(uploadLeft);
        controls.add(uploadRight);
        controls.add(reset);
        controls.add(quit);
        controls.add(speedLabel);
        controls.add(speedSlider);
        c.add(images, BorderLayout.NORTH);
        c.add(controls);
        //add(c);

        images.setPreferredSize(new Dimension(1000, 500));
        controls.setPreferredSize(new Dimension(100, 150));




        pack();
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });
        setResizable(false);
        setVisible(true);


    }

    public BufferedImage resize(BufferedImage image, int newWidth, int newHeight) {
        float height, width, scale;
        height = image.getHeight();
        width = image.getWidth();
        System.out.println(height +"    "+ width);

        if (height > width) {
            scale = newHeight / height;
            height = height * scale;
            width = width * scale;
        }
        else {
            scale = newWidth / width;
            width = width * scale;
            height = height * scale;
        }

        System.out.println(height +"    "+ width + "      " + scale);

        Image temp = image.getScaledInstance((int)width, (int)height, Image.SCALE_SMOOTH);
        BufferedImage newImage = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = newImage.createGraphics();
        g2d.drawImage(temp, 0, 0, null);
        g2d.dispose();

        return newImage;
    }



    public static void main(String argv[]){
        JMorph please = new JMorph();
    }




}
