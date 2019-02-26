import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener{

    private final int B_WIDTH = 600;
    private final int B_HEIGHT = 600;
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = 900;
    private final int RAND_POS = 59;
    private final int DELAY = 50;

    private final int[] x = new int[ALL_DOTS];
    private final int[] y = new int[ALL_DOTS];

    private int dots;
    private int apple_x;
    private int apple_y;

    private int counter = 0;
    private String counterString;

    private Random rand;

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;

    private boolean inGame = false;
    private boolean isInMenu = true;
    private boolean highscores = false;
    private boolean gameOver = false;

    private Clip clip1, clip2, clip3;
    private File file1, file2, file3;
    private AudioInputStream audioIn1, audioIn2, audioIn3;

    private String[] options = {"Start", "Highscores", "Exit"};
    private int currentSelection = 0;

    private ArrayList<Image> images = new ArrayList<>();
    private Timer timer;
    private Image ball;
    private Image ball1, ball2, ball3, ball4;
    private Image apple;
    private Image head;
    private BufferedImage snake;


    public Board() {

        initBoard();
    }
    
    private void initBoard() {

        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadSounds();
        loadImages();
        initGame();
    }

    private void loadSounds() {

        file1 = new File("resources/gamemusic.wav");
        file2 = new File("resources/eat.wav");
        file3 = new File("resources/gameover.wav");


        try {

            audioIn1 = AudioSystem.getAudioInputStream(file1.toURI().toURL());
            clip1 = AudioSystem.getClip();
            clip1.open(audioIn1);

            audioIn2 = AudioSystem.getAudioInputStream(file2.toURI().toURL());
            clip2 = AudioSystem.getClip();
            clip2.open(audioIn2);

            audioIn3 = AudioSystem.getAudioInputStream(file3.toURI().toURL());
            clip3 = AudioSystem.getClip();
            clip3.open(audioIn3);

        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    private void loadImages() {

        ImageIcon iid = new ImageIcon("resources/dot.png");
        ball1 = iid.getImage();

        ImageIcon iid2 = new ImageIcon("resources/dot2.png");
        ball2 = iid2.getImage();

        ImageIcon iid3 = new ImageIcon("resources/dot3.png");
        ball3 = iid3.getImage();

        ImageIcon iid4 = new ImageIcon("resources/dot4.png");
        ball4 = iid4.getImage();

        images.add(ball1);
        images.add(ball2);
        images.add(ball3);
        images.add(ball4);

        ImageIcon iia = new ImageIcon("resources/apple.png");
        apple = iia.getImage();

        ImageIcon iih = new ImageIcon("resources/head.png");
        head = iih.getImage();

        try {
            snake = ImageIO.read(new File("resources/menu.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initGame() {

        dots = 3;
        counter = 0;
        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * 10;
            y[z] = 50;
        }
        
        locateApple();
        clip1.start();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }
    
    private void doDrawing(Graphics g) {
        if (isInMenu) {
            g.drawImage(snake, B_WIDTH*7/30, B_HEIGHT/15, null);
            for (int i = 0; i < options.length; i++) {
                if (i == currentSelection) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(Color.BLUE);
                }
                g.setFont(new Font("Arial", Font.PLAIN, 36));
                g.drawString(options[i], B_WIDTH * 3 / 9, B_HEIGHT/3 + i * B_HEIGHT*1/5);

            }
        } else if (highscores) {

            showHighscores(g);

        } else if (inGame) {

            g.drawImage(apple, apple_x, apple_y, this);
            counter(g);
            rand = new Random();


            for (int z = 0; z < dots; z++) {
                ball = images.get(rand.nextInt(images.size()));
                if (z == 0) {
                    g.drawImage(head, x[z], y[z], this);
                } else {
                    g.drawImage(ball, x[z], y[z], this);
                }
            }

            Toolkit.getDefaultToolkit().sync();

        } else {


            gameOver(g);
        }
    }

    private void gameOver(Graphics g) {

        clip1.setFramePosition(0);
        clip1.stop();
        clip3.setFramePosition(0);
        clip3.start();

        String msg = "Game Over";
        String back = "Press ESC to back to the menu";
        Font small = new Font("Helvetica", Font.BOLD, 104);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
        g.setFont(new Font("Helvatica", Font.BOLD, 16));
        g.drawString(back, B_WIDTH*1/30, B_HEIGHT*29/30);

    }

    private void counter(Graphics g) {

        counterString = String.valueOf(counter);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score", B_WIDTH*7/10, B_HEIGHT*1/10);
        g.drawString(counterString, B_WIDTH*4/5, B_HEIGHT*1/10);

    }

    private void checkApple() {

        if ((x[0] == apple_x) && (y[0] == apple_y)) {

            dots++;
            counter++;

            locateApple();
            clip2.setFramePosition(0);
            clip2.start();
        }
    }

    private void move() {

        for (int z = dots; z > 0; z--) {

            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];

        }

        if (leftDirection) {
            x[0] -= DOT_SIZE;
        }

        if (rightDirection) {
            x[0] += DOT_SIZE;
        }

        if (upDirection) {
            y[0] -= DOT_SIZE;
        }

        if (downDirection) {
            y[0] += DOT_SIZE;
        }
    }

    private void saveHighscore() {

        String highscore = JOptionPane.showInputDialog("Podaj nick");
        String[] splits = highscore.split(" ");
        String[] connector = {splits[0], String.valueOf(counter)};

        String path1 = "resources/wyniki.txt";
        String path2 = "resources/sorted.txt";

        ArrayList<String[]> player = new ArrayList<>();
        String line;
        int lines = 0;

        try {

            BufferedReader reader = new BufferedReader(new FileReader(path1));
            BufferedWriter writer1 = new BufferedWriter(new FileWriter(path1, true));
            BufferedWriter writer2 = new BufferedWriter(new FileWriter(path2, true));
            PrintWriter tempWriter = new PrintWriter(path2);
            line = reader.readLine();

            while(line != null) {

                lines++;
                String[] playerDetail = line.split(" ");
                player.add(playerDetail);
                line = reader.readLine();

            }

            player.add(connector);
            writer1.write(splits[0] + " " + counter + "\n");

            tempWriter.write("");
            tempWriter.close();

            Collections.sort(player, new Comparator<String[]>() {
                @Override
                public int compare(String[] o1, String[] o2) {
                    int oo1 = Integer.valueOf(o1[1]);
                    int oo2 = Integer.valueOf(o2[1]);
                    return oo2 - oo1;
                }
            });


            lines = 0;
            for(String[] sa : player) {
                if(lines < 10) {
                    writer2.write(sa[0] + " " + sa[1] + "\n");
                    lines++;
                }
            }

            writer1.close();
            reader.close();
            writer2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkCollision() {

        for (int z = dots; z > 0; z--) {

            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
                gameOver = true;

            }
        }

        if (y[0] >= B_HEIGHT) {
            inGame = false;
            gameOver = true;

        }

        if (y[0] < 0) {
            inGame = false;
            gameOver = true;

        }

        if (x[0] >= B_WIDTH) {
            inGame = false;
            gameOver = true;

        }

        if (x[0] < 0) {
            inGame = false;
            gameOver = true;

        }
        
        if (gameOver) {
            timer.stop();
            saveHighscore();

        }
    }

    private void locateApple() {

        int r = (int) (Math.random() * RAND_POS);
        apple_x = ((r * DOT_SIZE));

        r = (int) (Math.random() * RAND_POS);
        apple_y = ((r * DOT_SIZE));
    }

    private void showHighscores(Graphics g) {

        int i = 0;
        File file = new File("resources/sorted.txt");

        try {
            Scanner scan = new Scanner(file);
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 52));
            g.drawString("HIGHSCORES", B_WIDTH*2/9, B_HEIGHT*1/8);
            g.setFont(new Font("Arial", Font.ITALIC, 30));
            g.setColor(Color.CYAN);

            while(scan.hasNext()) {

                String string = scan.nextLine();
                String[] splits = string.split(" ");
                i += B_HEIGHT / 3;
                g.drawString(splits[0], B_WIDTH*1/7, (B_HEIGHT+i)/5);
                g.drawString(splits[1], B_WIDTH*4/5, (B_HEIGHT+i)/5);

            }

            g.setColor(Color.LIGHT_GRAY);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Press ESC to back to the menu", B_WIDTH*1/30, B_HEIGHT*29/30);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {

            checkApple();
            checkCollision();
            move();
        }

        repaint();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();
            if(isInMenu) {
                inGame = false;
                highscores = false;
                gameOver = false;
                if(key == KeyEvent.VK_DOWN) {
                    currentSelection++;
                    if(currentSelection >= options.length) currentSelection = 0;
                }
                else if(key == KeyEvent.VK_UP) {
                    currentSelection--;
                    if(currentSelection < 0) currentSelection = options.length - 1;
                }
                else if(currentSelection == 0 && key == KeyEvent.VK_ENTER) {
                    isInMenu = false;
                    inGame = true;
                }
                else if(currentSelection == 1 && key == KeyEvent.VK_ENTER) {
                    highscores = true;
                    isInMenu = false;

                }
                else if(currentSelection == 2 && key == KeyEvent.VK_ENTER) {
                    System.exit(0);
                }

            }
            else if(highscores) {
                isInMenu = false;
                inGame = false;
                gameOver = false;
                if(key == KeyEvent.VK_ESCAPE) {
                    highscores = false;
                    isInMenu = true;
                }
            }
            else if(inGame) {
                isInMenu = false;
                gameOver = false;
                highscores = false;
                if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                    leftDirection = true;
                    upDirection = false;
                    downDirection = false;
                }

                if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                    rightDirection = true;
                    upDirection = false;
                    downDirection = false;
                }

                if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                    upDirection = true;
                    rightDirection = false;
                    leftDirection = false;
                }

                if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                    downDirection = true;
                    rightDirection = false;
                    leftDirection = false;
                }
            }
            else if(gameOver) {
                inGame = false;
                highscores = false;
                isInMenu = true;
                    if(key == KeyEvent.VK_ESCAPE) {
                        gameOver = false;
                        initGame();
                    }
            }
        }
    }
}