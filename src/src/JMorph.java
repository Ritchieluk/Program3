import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.*;

public class JMorph extends JFrame {

    private int MAX_IMAGE_SIZE = 400;
    private JFrame morphFrame;
    private GriddedImage leftGrid, rightGrid, previewGrid;
    private TriangleGrid oldGrid, newGrid, interGrid;
    private JPanel controls, images, rightPanel, leftPanel, leftImageOptions, rightImageOptions, gridOptions, gridText, frameOptions, frameText;
    private JButton uploadLeft, uploadRight, quit, resetLeft, resetRight, animate, saveMorph, uploadMorph, leftBrighter, leftDarker;
    private BufferedImage leftImage, rightImage, origLeft, origRight, morphFrames[];
    private JSlider timeSlider, frameSlider, rowSlider, colSlider, rightBrightnessSlider, leftBrightnessSlider;
    private JLabel extra, timeLabel, frameLabel, leftBrightnessLabel, rightBrightnessLabel, rowLabel, colLabel;
    static int rows = 11, cols = 11, frame = 0, frames = 30, seconds = 3, frameCount = 0, animateCounter = 0;
    private int startX = 0, startY = 0, endX = 0, endY = 0, rectWidth = 0, rectHeight = 0;
    private Timer frameCounter;
    private  Polygon leftRectangle, rightRectangle;
    private String rectGrid = "";
    boolean timestart = false, leftImageUploaded = false, rightImageUploaded = false, pointClicked = false, startDrag = false, leftRectSelected, rightRectSelected;
    TriangleGrid[] gridFrames;
    final JFileChooser fc = new JFileChooser("./img");
    JMorphListener manager;




    // CLASS: JMorph
    // PURPOSE: An instance of the JMorph class can alter two images and generate an animation of morphing between them
    public JMorph(){
        super("JMorph");
        setupGUI();
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
    // FUNCTION: setupGUI
    // PURPOSE: creates and packs all of the elements in the GUI, adds the action listener, and adds them to the frame
    private void setupGUI(){
        Container c = this.getContentPane();
        leftImage = new BufferedImage(MAX_IMAGE_SIZE, MAX_IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
        rightImage = new BufferedImage(MAX_IMAGE_SIZE, MAX_IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
        images = new JPanel();
        leftImageOptions = new JPanel();
        rightImageOptions = new JPanel();
        gridOptions = new JPanel();
        frameOptions = new JPanel();
        gridText = new JPanel();
        frameText = new JPanel();
        leftPanel = new JPanel();
        rightPanel = new JPanel();
        controls = new JPanel();
        uploadLeft = new JButton("Upload Left Image");
        uploadRight = new JButton("Upload Right Image");
        quit = new JButton("Quit");
        resetLeft = new JButton("Reset Left Points");
        resetRight = new JButton("Reset Right Points");
        animate = new JButton("Animate");
        saveMorph = new JButton("Save Morph");
        uploadMorph = new JButton("Upload Morph");
        timeSlider = new JSlider(1,5,3);
        frameSlider = new JSlider(0, 30, 30);
        leftBrightnessSlider = new JSlider(1, 50, 25);
        rightBrightnessSlider = new JSlider(1, 50, 25);
        rowSlider = new JSlider(5, 20, rows);
        colSlider = new JSlider(5, 20, cols);
        extra = new JLabel("");
        leftBrightnessLabel = new JLabel("Adjust Left Brightness");
        rightBrightnessLabel = new JLabel("Adjust Right Brightness");
        timeLabel = new JLabel("Adjust Duration (Seconds)");
        frameLabel = new JLabel("Adjust Frames per Second");
        rowLabel = new JLabel("Adjust Rows");
        colLabel = new JLabel("Adjust Cols");
        leftRectangle = new Polygon();
        rightRectangle = new Polygon();
        images.setLayout(new GridLayout(1,2));
        images.add(leftPanel);
        images.add(rightPanel);
        controls.setLayout(new GridLayout(5,3));
        leftImageOptions.setLayout(new GridLayout(1,2));
        leftImageOptions.add(uploadLeft);
        leftImageOptions.add(resetLeft);
        rightImageOptions.setLayout(new GridLayout(1,2));
        rightImageOptions.add(uploadRight);
        rightImageOptions.add(resetRight);
        gridOptions.setLayout(new GridLayout(1,2));
        gridOptions.add(rowSlider);
        gridOptions.add(colSlider);
        gridText.setLayout(new GridLayout(1,2));
        gridText.add(rowLabel);
        gridText.add(colLabel);
        frameText.setLayout(new GridLayout(1,2));
        frameText.add(frameLabel);
        frameText.add(timeLabel);
        frameOptions.setLayout(new GridLayout(1,2));
        frameOptions.add(frameSlider);
        frameOptions.add(timeSlider);
        resetRight.setEnabled(false);
        resetLeft.setEnabled(false);
        animate.setEnabled(false);
        manager = new JMorphListener();
        quit.addActionListener(manager);

        frameCounter = new Timer((1000/frames), e -> {
            if(frameCount<gridFrames.length) {
                previewGrid.setGrid(gridFrames[frameCount]);
                frameCount++;
            }
            else {
                frameCount = 0;
                frameCounter.stop();

            }
        });

        uploadRight.addActionListener(manager);

        animate.addActionListener(manager);
        saveMorph.addActionListener(manager);
        uploadMorph.addActionListener(manager);
        uploadLeft.addActionListener(manager);

        resetLeft.addActionListener(manager);

        resetRight.addActionListener(manager);

        timeSlider.setMajorTickSpacing(1);
        timeSlider.setPaintTicks(true);
        timeSlider.setPaintLabels(true);
        frameSlider.setMinorTickSpacing(5);
        frameSlider.setMajorTickSpacing(10);
        frameSlider.setPaintTicks(true);
        frameSlider.setPaintLabels(true);
        leftBrightnessSlider.setMajorTickSpacing(5);
        leftBrightnessSlider.setPaintTicks(true);
        leftBrightnessSlider.setPaintLabels(true);
        rightBrightnessSlider.setMajorTickSpacing(5);
        rightBrightnessSlider.setPaintTicks(true);
        rightBrightnessSlider.setPaintLabels(true);
        rowSlider.setMajorTickSpacing(5);
        rowSlider.setMinorTickSpacing(1);
        rowSlider.setPaintTicks(true);
        rowSlider.setPaintLabels(true);
        colSlider.setMajorTickSpacing(5);
        colSlider.setMinorTickSpacing(1);
        colSlider.setPaintTicks(true);
        colSlider.setPaintLabels(true);


        timeSlider.addChangeListener(manager);
        frameSlider.addChangeListener(manager);
        rowSlider.addChangeListener(manager);
        colSlider.addChangeListener(manager);
        leftBrightnessSlider.addChangeListener(manager);
        rightBrightnessSlider.addChangeListener(manager);

        controls.add(leftImageOptions);
        controls.add(animate);
        controls.add(rightImageOptions);
        controls.add(leftBrightnessLabel);
        controls.add(extra);
        controls.add(rightBrightnessLabel);
        controls.add(leftBrightnessSlider);
        controls.add(saveMorph);
        controls.add(rightBrightnessSlider);
        controls.add(gridText);
        controls.add(uploadMorph);
        controls.add(frameText);
        controls.add(gridOptions);
        controls.add(quit);
        controls.add(frameOptions);
        c.add(images, BorderLayout.NORTH);
        c.add(controls);

        images.setPreferredSize(new Dimension(1000, 500));
        controls.setPreferredSize(new Dimension(100, 250));
        pack();
        addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){System.exit(0);}});
        setResizable(false);
        setVisible(true);
    }
    // FUNCTION: createPreview
    // PURPOSE: opens a new frame to contain a preview animation
    private void createPreview(){
       JFrame previewFrame = new JFrame("Animation");
       Container container = previewFrame.getContentPane();
       container.add(previewGrid, BorderLayout.CENTER);

       previewFrame.setSize(400,400);
       previewFrame.setVisible(true );
       previewFrame.pack();

    }

    private void createMorph(BufferedImage[] morphFrames){
        morphFrame = new JFrame("Morph");
        Container container = morphFrame.getContentPane();
        JPanel pls = new JPanel();
        pls.add(new JLabel(new ImageIcon(morphFrames[0])));
        container.add(pls, BorderLayout.NORTH);
        morphFrame.setSize(400, 400);
        morphFrame.setVisible(true);
        morphFrame.pack();
    }
    // FUNCTION: resize
    // PURPOSE: given an image it will set it to the given width and height
    public BufferedImage resize(BufferedImage image, int newWidth, int newHeight) {
        float height, width, scale;
        height = image.getHeight();
        width = image.getWidth();

        if (height > width) {
            width = height;
            scale = newHeight / height;
            height = height * scale;
            width = width * scale;
        }
        else {
            height = width;
            scale = newWidth / width;
            width = width * scale;
            height = height * scale;
        }

        Image temp = image.getScaledInstance((int)width, (int)height, Image.SCALE_SMOOTH);
        BufferedImage newImage = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = newImage.createGraphics();
        g2d.drawImage(temp, 0, 0, null);
        g2d.dispose();

        return newImage;
    }
    // FUNCTION: animateGrid
    // PURPOSE: generates an array of intermediate grids that can be displayed in sequence to resemble an animation
    private TriangleGrid[] animateGrid() {

        TriangleGrid[] animatedGrid = new TriangleGrid[frames*seconds];
        for(frame = 0; frame < frames * seconds; frame++){
            float alpha = frame * 1 / (float) (frames * seconds - 1);
            TriangleGrid intermediateGrids = intermediateGrid(oldGrid, newGrid, alpha);
            animatedGrid[frame] = intermediateGrids;
            System.out.println(frame);
        }
        frame = 0;
        return animatedGrid;
    }
    // FUNCTION: intermediateGrid
    // PURPOSE: generates a single intermediate grid based upon a fractional division determined by float a
    private static TriangleGrid intermediateGrid(TriangleGrid orig, TriangleGrid targ, float a){
        TriangleGrid inter = new TriangleGrid(targ.getWidth(), targ.getHeight(), targ.getTrueWidth(), targ.getTrueWidth());
        for(int i = 0; i < inter.getWidth(); i++){
            for(int j = 0; j <inter.getHeight(); j++){
                inter.points[i][j].x = (int) (a * targ.points[i][j].x + (1-a) * orig.points[i][j].x);
                inter.points[i][j].y = (int) (a * targ.points[i][j].y + (1-a) * orig.points[i][j].y);
            }

        }
        return inter;
    }

    private BufferedImage[] morph(BufferedImage image, GriddedImage leftgrid, GriddedImage rightrid){
        leftgrid.tGrid.setupGrid();
        rightrid.tGrid.setupGrid();
        BufferedImage[] morphed = new BufferedImage[frames*seconds];
        BufferedImage source = image;
        Polygon[] newTriangles;
        Polygon[] oldTriangles = leftgrid.tGrid.getTriangles();
        for(int i = 0; i < frames * seconds;i++){
            morphed[i] = image;
            newTriangles = gridFrames[i].setupGrid();
            for(int j = 0;j < newTriangles.length;j++){
                warpTriangle(source, morphed[i], oldTriangles[j], newTriangles[j], null, null);
            }

        }



        return morphed;
    }
    // FUNCTION:
    // PURPOSE:
    public void saveJMorph()throws IOException {
        JMorphFileContents jc = new JMorphFileContents();
        jc.setContents();
        String fileName = "", dir = ".";
        int rVal = fc.showSaveDialog(this);
        if(rVal == JFileChooser.APPROVE_OPTION){
            fileName=fc.getSelectedFile().getName();
            dir = fc.getCurrentDirectory().toString();
            File file = new File(dir+"/"+fileName+".txt");
            File newFolder = new File(dir+"/"+fileName);
            boolean created = newFolder.mkdir();
            if(created)
                System.out.println("Folder created");
            else
                System.out.println("Unable to create Folder");
            File leftImg = new File(dir+"/"+fileName+"/"+"leftImg.jpg");
            File rightImg = new File(dir+"/"+fileName+"/"+"rightImg.jpg");
            File leftOrig = new File(dir+"/"+fileName+"/"+"origLeft.jpg");
            File rightOrig = new File(dir+"/" + fileName+"/" + "origRight.jpg");
            jc.setLeftImageLocation(leftImg.getPath());
            jc.setRightImageLocation(rightImg.getPath());
            jc.setOriginalLeftLocation(leftOrig.getPath());
            jc.setOriginalRightLocation(rightOrig.getPath());
            saveImage(leftImage, leftImg);
            saveImage(rightImage,rightImg);
            saveImage(origLeft, leftOrig);
            saveImage(origRight, rightOrig);
            String[] contents = jc.getContents();
            for(int i = 0; i < contents.length; i++){
                System.out.println(contents[i]);
            }
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            writer.print(fileName);
            for(int i = 0; i < contents.length; i++){
                writer.printf(System.getProperty("line.separator"));
                writer.printf("%s",contents[i]);
            }

            writer.close();
        }
        if(rVal == JFileChooser.CANCEL_OPTION){
            fileName = "You pressed cancel";
            dir = ".";
        }


    }

    public boolean saveImage(BufferedImage bi, File location){
        try {
            ImageIO.write(bi, "jpg", location);
            return true;
        } catch(IOException e){
            return false;
        }
    }

    private void uploadJMorph() throws FileNotFoundException, IOException{
        String fileName = "";
        String dir = ".";
        int rVal = fc.showOpenDialog(this);
        if(rVal == JFileChooser.APPROVE_OPTION){
            fileName = fc.getSelectedFile().getName();
            dir = fc.getCurrentDirectory().toString();
            File file = new File(dir+"/"+fileName);
            JMorphFileContents uc = new JMorphFileContents();
            BufferedReader br = new BufferedReader(new FileReader(file));
            System.out.println(br.readLine());
            uc.setLeftImageLocation(br.readLine());
            uc.setRightImageLocation(br.readLine());
            uc.setOriginalLeftLocation(br.readLine());
            uc.setOriginalRightLocation(br.readLine());
            uc.setRowsC(Integer.parseInt(br.readLine()));
            uc.setColsC(Integer.parseInt(br.readLine()));
            uc.setFramesC(Integer.parseInt(br.readLine()));
            uc.setSecondsC(Integer.parseInt(br.readLine()));
            uc.setCurrentBrightnessLeft(Integer.parseInt(br.readLine()));
            uc.setCurrentBrightnessRight(Integer.parseInt(br.readLine()));
            uc.settGridLeft(br.readLine());
            uc.settGridRight(br.readLine());
            uc.adjustJMorph();

        }

    }

    public static void main(String argv[]){
        JMorph morph = new JMorph();
    }
    // CLASS: JMorphListener
    // PURPOSE: combines ActionListener, ChangeListener, MouseMotionListener, and MoustListener
    //          to accomplish all necessary action management that the JMorph requires
    public class JMorphListener implements ActionListener, ChangeListener, MouseMotionListener, MouseListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == animate) {
                gridFrames = animateGrid();
                previewGrid = new GriddedImage(leftImage, manager);
                createPreview();
                previewGrid.setGrid(gridFrames[0]);
                frameCounter.start();
                BufferedImage temp = copyImage(leftImage);
                resize(temp, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE);
                morphFrames = morph(temp, leftGrid, rightGrid);
                createMorph(morphFrames);
            } else if (e.getSource() == quit) {
                System.exit(0);
            }
            else if(e.getSource() == resetRight){
                rightGrid.reset();
            }
            else if(e.getSource() == resetLeft){
                leftGrid.reset();
            }
            else if(e.getSource() == uploadLeft) {
                leftPanel.removeAll();
                int returnVal = fc.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    String name = file.getAbsolutePath();
                    try {
                        origLeft = ImageIO.read(new File(name));
                    } catch (IOException e1) {
                    }
                    ;
                    leftImage = origLeft;
                    origLeft = resize(origLeft, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE);

                    leftImageUploaded = true;
                    leftImage = resize(leftImage, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE);
                    leftGrid = new GriddedImage(leftImage, manager);
                    leftGrid.setName("l");
                    oldGrid = leftGrid.getTriangleGrid();
                    leftPanel.add(leftGrid);
                    leftPanel.revalidate();
                    leftPanel.repaint();
                    resetLeft.setEnabled(true);
                    animateCounter++;
                    if (animateCounter == 2) {
                        animate.setEnabled(true);
                    }
                    System.out.println(leftImage.getType());
                }
            }
            else if(e.getSource() == uploadRight) {
                rightPanel.removeAll();
                int returnVal = fc.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    String name = file.getAbsolutePath();
                    try {
                        origRight = ImageIO.read(new File(name));
                    } catch (IOException e1) {
                    }
                    ;
                    rightImage = origRight;
                    origRight = resize(origRight, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE);
                    rightImage = resize(rightImage, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE);
                    rightImageUploaded = true;
                    rightGrid = new GriddedImage(rightImage, manager);
                    rightGrid.setName("r");
                    rightGrid.tGrid.setupGrid();
                    newGrid = rightGrid.getTriangleGrid();
                    rightPanel.add(rightGrid);
                    rightPanel.revalidate();
                    rightGrid.repaint();
                    resetRight.setEnabled(true);
                    animateCounter++;
                    if (animateCounter == 2) {
                        animate.setEnabled(true);
                    }
                }
            }
            else if(e.getSource()==saveMorph){
                try {
                    System.out.println("Saving Selected");
                    saveJMorph();
                }
                catch(IOException ioe){System.out.println("Exception");}
            }
            else if(e.getSource() ==  uploadMorph){
                try{
                    uploadJMorph();
                }
                catch(IOException ie){System.out.println("Exeption");}
            }
        }
        public void stateChanged(ChangeEvent e){
            if(e.getSource() == timeSlider){
                seconds = timeSlider.getValue();
            }
            else if(e.getSource() == frameSlider){
                if (frameSlider.getValue() == 0) {
                    frames = 1;
                    frameSlider.setValue(frames);
                }
                else {
                    frames = frameSlider.getValue();
                    frameCounter.setDelay(1000/frames);
                }
            }
            else if(e.getSource() == leftBrightnessSlider){
                if(leftImageUploaded) {
                    float value = (float) leftBrightnessSlider.getValue();
                    float scalefactor = 2 * value / leftBrightnessSlider.getMaximum();
                    RescaleOp op = new RescaleOp(scalefactor, 0, null);
                    leftImage = op.filter(origLeft, leftImage);
                    repaint();
                }
            }
            else if(e.getSource() == rightBrightnessSlider){
                if(rightImageUploaded) {
                    float value = (float) rightBrightnessSlider.getValue();
                    float scalefactor = 2 * value / rightBrightnessSlider.getMaximum();
                    RescaleOp op = new RescaleOp(scalefactor, 0, null);
                    rightImage = op.filter(origRight, rightImage);
                    repaint();
                }
            }
            else if(e.getSource() == rowSlider){
                if(!rowSlider.getValueIsAdjusting()){
                    if(rowSlider.getValue() == 0){
                        rows = 1;
                        rowSlider.setValue(rows);
                        rows = rowSlider.getValue();
                        if(leftImageUploaded) {
                            leftGrid = new GriddedImage(leftImage, manager);
                            leftGrid.setName("l");
                            oldGrid = leftGrid.getTriangleGrid();
                            leftPanel.removeAll();
                            leftPanel.add(leftGrid);
                            leftPanel.revalidate();
                            leftPanel.repaint();
                        }
                        if(rightImageUploaded){
                            rightGrid = new GriddedImage(rightImage, manager);
                            rightGrid.setName("r");
                            newGrid = rightGrid.getTriangleGrid();
                            rightPanel.removeAll();
                            rightPanel.add(rightGrid);
                            rightPanel.revalidate();
                            rightPanel.repaint();
                        }

                    }
                    else {
                        rows = rowSlider.getValue();
                        if(leftImageUploaded) {
                            leftGrid = new GriddedImage(leftImage, manager);
                            leftGrid.setName("l");
                            oldGrid = leftGrid.getTriangleGrid();
                            leftPanel.removeAll();
                            leftPanel.add(leftGrid);
                            leftPanel.revalidate();
                            leftPanel.repaint();
                        }
                        if(rightImageUploaded){
                            rightGrid = new GriddedImage(rightImage, manager);
                            rightGrid.setName("r");
                            newGrid = rightGrid.getTriangleGrid();
                            rightPanel.removeAll();
                            rightPanel.add(rightGrid);
                            rightPanel.revalidate();
                            rightPanel.repaint();
                        }
                    }
                }
            }
            else if(e.getSource() == colSlider){
                if(!colSlider.getValueIsAdjusting()){
                    if(colSlider.getValue() == 0){
                        cols = 2;
                        colSlider.setValue(cols);
                        cols = colSlider.getValue();
                        if(leftImageUploaded) {
                            leftGrid = new GriddedImage(leftImage, manager);
                            leftGrid.setName("l");
                            oldGrid = leftGrid.getTriangleGrid();
                            leftPanel.removeAll();
                            leftPanel.add(leftGrid);
                            leftPanel.revalidate();
                            leftPanel.repaint();
                        }
                        if(rightImageUploaded){
                            rightGrid = new GriddedImage(rightImage, manager);
                            rightGrid.setName("r");
                            newGrid = rightGrid.getTriangleGrid();
                            rightPanel.removeAll();
                            rightPanel.add(rightGrid);
                            rightPanel.revalidate();
                            rightPanel.repaint();
                        }

                    }
                    else {
                        cols = colSlider.getValue();
                        if(leftImageUploaded) {
                            leftGrid = new GriddedImage(leftImage, manager);
                            leftGrid.setName("l");
                            oldGrid = leftGrid.getTriangleGrid();
                            leftPanel.removeAll();
                            leftPanel.add(leftGrid);
                            leftPanel.revalidate();
                            leftPanel.repaint();
                        }
                        if(rightImageUploaded){
                            rightGrid = new GriddedImage(rightImage, manager);
                            rightGrid.setName("r");
                            rightPanel.removeAll();
                            rightPanel.add(rightGrid);
                            rightPanel.revalidate();
                            rightPanel.repaint();
                        }
                    }
                }
            }
        }
        public void mousePressed(MouseEvent e){
            startDrag = true;
            int xPos = e.getX();
            int yPos = e.getY();
            startX = xPos;
            startY = yPos;
            GriddedImage temp = (GriddedImage) e.getSource();
            if(temp.getName()=="l"){
                for(int i = 0; i < leftGrid.getMidpointWidth(); i++){
                    for(int j = 0; j < leftGrid.getMidpointHeight(); j++){
                        Point curPoint = leftGrid.tGrid.points[i][j];
                        if(curPoint.distance(xPos, yPos) <= leftGrid.getRadius()){
                            pointClicked = true;
                            if(i != 0 && i!= leftGrid.getMidpointWidth() -1 && j!=0 && j!= leftGrid.getMidpointHeight() -1){
                                leftGrid.setsRow(i);
                                leftGrid.setsCol(j);
                                System.out.println("Selected Row: "+i+" Selected Col: "+j);
                                return;
                            }
                        }


                    }
                }
                if(!leftRectangle.contains(startX, startY)) {
                    if (temp.getName() == "l") {
                        leftRectangle = new Polygon();
                        leftGrid.setRectangle(leftRectangle);
                        repaint();
                    }
                }
                else if(leftRectangle.contains(xPos, yPos)){
                    leftRectSelected = true;
                }
            }
            else if(temp.getName()=="r"){
                for(int i = 0; i < rightGrid.getMidpointWidth(); i++){
                    for(int j = 0; j < rightGrid.getMidpointHeight(); j++){
                        Point curPoint = rightGrid.tGrid.points[i][j];
                        if(curPoint.distance(xPos, yPos) <= rightGrid.getRadius()){
                            pointClicked = true;
                            if(i != 0 && i!= rightGrid.getMidpointWidth() -1 && j!=0 && j!= rightGrid.getMidpointHeight() -1){
                                rightGrid.setsRow(i);
                                rightGrid.setsCol(j);
                                return;
                            }

                        }
                    }
                }
                if(!rightRectangle.contains(startX, startY)) {
                    if (temp.getName() == "l") {
                        rightRectangle = new Polygon();
                        leftGrid.setRectangle(rightRectangle);
                        repaint();
                    }
                }
                else if(rightRectangle.contains(xPos, yPos)){
                    rightRectSelected = true;
                }
            }



        }
        public void mouseReleased(MouseEvent e){
            if(rectGrid == "l") {
                leftGrid.setRectangle(leftRectangle);
                repaint();
            }
            else if(rectGrid == "r") {
                rightGrid.setRectangle(rightRectangle);
                repaint();

            }
            if(leftImageUploaded) {
                leftGrid.setsRow(-1);
                leftGrid.setsCol(-1);
                leftGrid.repaint();
            }
            if(rightImageUploaded) {
                rightGrid.setsRow(-1);
                rightGrid.setsCol(-1);
                rightGrid.repaint();
            }
            pointClicked = false;
        }
        public void mouseDragged(MouseEvent e) {
            if (pointClicked) {
                int xPos = e.getX();
                int yPos = e.getY();
                GriddedImage temp = (GriddedImage) e.getSource();
                System.out.println(temp.getName());
                if (temp.getName() == "l") {
                    System.out.println("Detected in Left Panel");
                    if (rightImageUploaded) {
                        rightGrid.setsRow(leftGrid.getsRow());
                        rightGrid.setsCol(leftGrid.getsCol());
                    }
                    int[] xVals = leftGrid.getXBounds();
                    int[] yVals = leftGrid.getYBounds();
                    Polygon boundary = new Polygon(xVals, yVals, 6);
                    if (leftGrid.getsRow() != -1 && leftGrid.getsCol() != -1 && (xPos > 0 && yPos > 0 && xPos < leftImage.getWidth() && yPos < leftImage.getHeight())) {
                        if (boundary.contains(xPos, yPos)) {
                            leftGrid.tGrid.points[leftGrid.getsRow()][leftGrid.getsCol()].x = xPos;
                            leftGrid.tGrid.points[leftGrid.getsRow()][leftGrid.getsCol()].y = yPos;
                            repaint();
                        }
                    }
                } else if (temp.getName() == "r") {
                    System.out.println("Detected in Right Panel");
                    if (leftImageUploaded) {
                        leftGrid.setsRow(rightGrid.getsRow());
                        leftGrid.setsCol(rightGrid.getsCol());
                    }
                    int[] xVals = rightGrid.getXBounds();
                    int[] yVals = rightGrid.getYBounds();
                    Polygon boundary = new Polygon(xVals, yVals, 6);
                    if (rightGrid.getsRow() != -1 && rightGrid.getsCol() != -1 && (xPos > 0 && yPos > 0 && xPos < rightImage.getWidth() && yPos < rightImage.getHeight())) {
                        if (boundary.contains(xPos, yPos)) {
                            rightGrid.tGrid.points[rightGrid.getsRow()][rightGrid.getsCol()].x = xPos;
                            rightGrid.tGrid.points[rightGrid.getsRow()][rightGrid.getsCol()].y = yPos;
                            repaint();
                        }
                    }
                }
            }
            else{
                GriddedImage temp = (GriddedImage) e.getSource();
                if (temp.getName() == "l") {
                    if (!leftRectangle.contains(startX, startY)) {
                        rectGrid = "l";
                        endX = e.getX();
                        endY = e.getY();
                        Graphics g = leftGrid.getGraphics();
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setColor(Color.BLUE);
                        int xVals[] = {startX, endX, endX, startX};
                        int yVals[] = {startY, startY, endY, endY};
                        leftRectangle = new Polygon(xVals, yVals, 4);
                        leftGrid.setRectangle(leftRectangle);
                        repaint();
                    }

                } else if (temp.getName() == "r") {
                    if (!rightRectangle.contains(startX, startY)) {
                        rectGrid = "r";
                        endX = e.getX();
                        endY = e.getY();
                        Graphics g = rightGrid.getGraphics();
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setColor(Color.BLUE);
                        int xVals[] = {startX, endX, endX, startX};
                        int yVals[] = {startY, startY, endY, endY};
                        rightRectangle = new Polygon(xVals, yVals, 4);
                        rightGrid.setRectangle(rightRectangle);
                        repaint();
                    }
                }
            }
        }




        public void mouseMoved(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }


    }

    public class JMorphFileContents {
        private String leftImageLocation, rightImageLocation, originalLeftLocation, originalRightLocation, filename;
        private int rowsC, colsC, framesC, secondsC, currentBrightnessLeft, currentBrightnessRight;
        private TriangleGrid tGridLeft, tGridRight;

        public void adjustJMorph(){
            rows = rowsC;
            rowSlider.setValue(rowsC);
            cols = colsC;
            colSlider.setValue(colsC);
            frames = framesC;
            frameSlider.setValue(framesC);
            seconds = secondsC;
            timeSlider.setValue(secondsC);
            oldGrid = tGridLeft;
            newGrid = tGridRight;


            try {
                leftImage = ImageIO.read(new File(leftImageLocation));
                rightImage = ImageIO.read(new File(rightImageLocation));
                origLeft = ImageIO.read(new File(originalLeftLocation));
                origRight = ImageIO.read(new File(rightImageLocation));
                leftImageUploaded = true;
                rightImageUploaded = true;
            } catch (IOException e1) {
            }
            rightPanel.removeAll();
            leftPanel.removeAll();
            rightGrid = new GriddedImage(rightImage, manager);
            leftGrid = new GriddedImage(leftImage, manager);
            leftGrid.setGrid(tGridLeft);
            oldGrid = leftGrid.getTriangleGrid();
            newGrid = rightGrid.getTriangleGrid();
            rightGrid.setGrid(tGridRight);
            rightGrid.setName("r");
            leftGrid.setName("l");
            leftPanel.add(leftGrid);
            rightPanel.add(rightGrid);
            leftPanel.revalidate();
            leftPanel.repaint();
            rightPanel.revalidate();
            rightGrid.repaint();
            resetLeft.setEnabled(true);
            resetRight.setEnabled(true);
            animate.setEnabled(true);
            float value = (float) currentBrightnessLeft;
            float scalefactor = 2 * value / leftBrightnessSlider.getMaximum();
            RescaleOp op = new RescaleOp(scalefactor, 0, null);
            leftImage = op.filter(origLeft, leftImage);
            rightPanel.removeAll();
            value = (float) currentBrightnessRight;
            scalefactor = 2 * value / rightBrightnessSlider.getMaximum();
            op = new RescaleOp(scalefactor, 0, null);
            rightImage = op.filter(origRight, rightImage);
            repaint();


        }

        public void setContents(){
            rowsC = rows;
            colsC = cols;
            framesC = frames;
            secondsC = seconds;
            currentBrightnessLeft = leftBrightnessSlider.getValue();
            currentBrightnessRight = rightBrightnessSlider.getValue();
            tGridLeft = oldGrid;
            tGridRight = newGrid;
        }

        public String[] getContents(){
            String[] contents ={
                    leftImageLocation,
                    rightImageLocation,
                    originalLeftLocation,
                    originalRightLocation,
                    Integer.toString(rowsC),
                    Integer.toString(colsC),
                    Integer.toString(framesC),
                    Integer.toString(secondsC),
                    Integer.toString(currentBrightnessLeft),
                    Integer.toString(currentBrightnessRight),
                    tGridLeft.toString(),
                    tGridRight.toString(),
            };
            return contents;
        }
        public void setFilename(String s){
            filename = s;
        }
        public void setRowsC(int r){
            rowsC = r;
        }
        public void setColsC(int c){
            colsC = c;
        }
        public void setFramesC(int c){
            framesC = c;
        }
        public void setSecondsC(int s){
            secondsC = s;
        }
        public void setCurrentBrightnessLeft(int b){
            currentBrightnessLeft = b;
        }
        public void setCurrentBrightnessRight(int b){
            currentBrightnessRight = b;
        }
        public void settGridLeft(String s){
            int[] xValues = new int[s.length()/2];
            int[] yValues = new int[s.length()/2];
            int index = 0;
            tGridLeft = new TriangleGrid(MAX_IMAGE_SIZE, MAX_IMAGE_SIZE, cols, rows);
            for(int i = 0; i < s.length(); i+=2){
                xValues[index] = s.charAt(i);
                yValues[index] = s.charAt(i+1);
                index++;
            }
            index = 0;
            for(int i = 0; i<rowsC; i++){
                for(int j = 0; j<colsC; j++){
                    tGridLeft.points[i][j] = new Point(xValues[index], yValues[index]);
                    index++;
                }
            }
        }
        public void settGridRight(String s){
            int[] xValues = new int[s.length()/2];
            int[] yValues = new int[s.length()/2];
            System.out.println(s.length()/2);
            int index = 0;
            tGridRight = new TriangleGrid(MAX_IMAGE_SIZE, MAX_IMAGE_SIZE, cols, rows);
            for(int i = 0; i < s.length() && index < s.length()/2; i+=2){
                xValues[index] = s.charAt(i);
                yValues[index] = s.charAt(i+1);
                System.out.println(index);
                index++;
            }
            index = 0;
            for(int i = 0; i<rowsC; i++){
                for(int j = 0; j<colsC; j++){
                    tGridRight.points[i][j] = new Point(xValues[index], yValues[index]);
                    index++;
                }
            }
        }
        public void setLeftImageLocation(String l){
            leftImageLocation = l;
        }
        public void setRightImageLocation(String r){
            rightImageLocation = r;
        }
        public void setOriginalLeftLocation(String s){
            originalLeftLocation = s;
        }
        public void setOriginalRightLocation(String s){
            originalRightLocation = s;
        }

    }

    public static void warpTriangle(
            BufferedImage src,
            BufferedImage dest,
            Polygon S,
            Polygon D,
            Object ALIASING,
            Object INTERPOLATION)
    {/*
        Object ALIASING,
        Object INTERPOLATION
        */

        /*****************************************************
         solve Xi = sx*xi + shx*yi + tx    for i = 1,2,3 where xi is a point on
         the source triangle and Xi = the corresponding point on the
         destination  triangle. Do the same thing for Yi = shy*y + sy*x + ty.
         shx is the shearing of x and sx is the scaling of x and tx is the
         translation of x needed to map one triangle to the other.
         r
         Gaussian Elimination with scaled partial pivoting is the method
         used solve the two systems of linear equations.
         ********************************************************/

        if (ALIASING == null)
            ALIASING = RenderingHints.VALUE_ANTIALIAS_ON;
        if (INTERPOLATION == null)
            INTERPOLATION = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
        double[][] a = new double[3][3];
        for (int i = 0; i < 3; ++i) {
            a[i][0] = S.xpoints[i];

            // System.out.println("P" + i + "(" + S.getX(i) + ", " + S.getY(i) +
            // ")" );

            a[i][1] = S.ypoints[i];
            a[i][2] = 1.0;
        }

        int l[] = new int[3];
        Gauss(3, a, l);

        double[] b = new double[3];
        for (int i = 0; i < 3; ++i) {
            b[i] = D.xpoints[i];
        }

        double[] x = new double[3];
        solve(3, a, l, b, x);

        double[] by = new double[3];
        for (int i = 0; i < 3; ++i) {
            by[i] = D.ypoints[i];
        }

        double[] y = new double[3];
        solve(3, a, l, by, y);

        // System.out.println("Affine:\t" + x[0] + ", " + x[1] + ", " + x[2] );
        // System.out.println("\t" + y[0] + ", " + y[1] + ", " + y[2] );

        AffineTransform af =
                new AffineTransform(x[0], y[0], x[1], y[1], x[2], y[2]);
        GeneralPath destPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);

        destPath.moveTo((float)D.xpoints[0], (float)D.ypoints[0]);
        destPath.lineTo((float)D.xpoints[1], (float)D.ypoints[1]);
        destPath.lineTo((float)D.xpoints[2], (float)D.ypoints[2]);
        destPath.lineTo((float)D.xpoints[0], (float)D.ypoints[0]);
        Graphics2D g2 = dest.createGraphics();

        // set up an alpha value for compositing as an example
        AlphaComposite ac =
                AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)0.5);
        g2.setComposite(ac);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, ALIASING);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, INTERPOLATION);
        g2.clip(destPath);
        g2.setTransform(af);
        g2.drawImage(src, 0, 0, null);
        g2.dispose();
    }

    private static void Gauss(int n, double[][] a, int[] l)
    {
        /****************************************************
         a is a n x n matrix and l is an int array of length n
         l is used as an index array that will determine the order of
         elimination of coefficients
         All array indexes are assumed to start at 0
         ******************************************************/
        double[] s = new double[n];  // scaling factor
        int i, j = 0, k;
        double r, rmax, smax, xmult;
        for (i = 0; i < n; ++i) {
            l[i] = i;
            smax = 0;
            for (j = 0; j < n; ++j)
                smax = Math.max(smax, Math.abs(a[i][j]));
            s[i] = smax;
        }

        i = n - 1;
        for (k = 0; k < (n - 1); ++k) {
            --j;
            rmax = 0;
            for (i = k; i < n; ++i) {
                r = Math.abs(a[l[i]][k] / s[l[i]]);
                if (r > rmax) {
                    rmax = r;
                    j = i;
                }
            }
            int temp = l[j];
            l[j] = l[k];
            l[k] = temp;
            for (i = k + 1; i < n; ++i) {
                xmult = a[l[i]][k] / a[l[k]][k];
                a[l[i]][k] = xmult;
                for (j = k + 1; j < n; ++j) {
                    a[l[i]][j] = a[l[i]][j] - xmult * a[l[k]][j];
                }
            }
        }
    }

    private static void solve(
            int n, double[][] a, int[] l, double[] b, double[] x)
    {
        /*********************************************************
         a and l have previously been passed to Gauss() b is the product of
         a and x. x is the 1x3 matrix of coefficients to solve for
         *************************************************************/
        int i, k;
        double sum;
        for (k = 0; k < (n - 1); ++k) {
            for (i = k + 1; i < n; ++i) {
                b[l[i]] -= a[l[i]][k] * b[l[k]];
            }
        }
        x[n - 1] = b[l[n - 1]] / a[l[n - 1]][n - 1];

        for (i = n - 2; i >= 0; --i) {
            sum = b[l[i]];
            for (int j = i + 1; j < n; ++j) {
                sum = sum - a[l[i]][j] * x[j];
            }
            x[i] = sum / a[l[i]][i];
        }
    }

    public static BufferedImage copyImage(BufferedImage source){
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

}

