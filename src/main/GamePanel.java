package main;

import entity.Aquarium;
import entity.Feature;
import entity.Player;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage; // >> THÊM
import javax.imageio.ImageIO; // >> THÊM
import java.io.IOException; // >> THÊM
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {
    // Screen settings
    public final int originalTileSize = 16;
    public final int scale = 2;
    public final int tileSize = originalTileSize * scale; 
    public final int screenWidth = 960;
    public final int screenHeight = 730;
    
    // GAME STATE
    public int gameState;
    public final int playState = 1;
    public final int gameOverState = 2;
    
    // GAME STATS
    public int score = 0;
    public int lives = 3;

    int FPS = 60;
    
    // SYSTEM
    public BufferedImage background; // >> THÊM: Biến lưu ảnh nền
    public MouseHandler mouseH = new MouseHandler();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public Feature feature = new Feature();
    Thread gameThread;
    
    // ENTITIES
    public Player player = new Player(this, mouseH);
    public Aquarium aquarium = new Aquarium(this);

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addMouseMotionListener(mouseH);
        this.setFocusable(true);
        gameState = playState;

        // >> SỬA: Tải ảnh background
        try {
            background = ImageIO.read(getClass().getResourceAsStream("/res/background.png"));
        } catch (IOException e) {
            System.err.println("Không tìm thấy background!");
            e.printStackTrace();
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;
        while (gameThread != null) {
            update();
            repaint();
            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000;
                if (remainingTime < 0) remainingTime = 0;
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        if (gameState == playState) {
            player.update();
            aquarium.update();
            cChecker.checkPlayerVsEnemies(player, aquarium.entities);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // >> SỬA: Vẽ ảnh background thay vì fillRect màu xanh
        if (background != null) {
            g2.drawImage(background, 0, 0, screenWidth, screenHeight, null);
        } else {
            g2.setColor(new Color(0, 100, 200)); 
            g2.fillRect(0, 0, screenWidth, screenHeight);
        }

        aquarium.draw(g2);
        player.draw(g2);
        drawGameUI(g2);

        if (gameState == gameOverState) {
            g2.setColor(Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 50));
            String text = "GAME OVER";
            int x = (screenWidth - g2.getFontMetrics().stringWidth(text))/2;
            int y = screenHeight/2;
            g2.drawString(text, x, y);
        }
        g2.dispose();
    }
    
    private void drawGameUI(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("Score: " + score, 20, 40);
        g2.drawString("Lives: " + lives, 20, 70);
    }
}