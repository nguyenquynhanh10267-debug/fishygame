package enity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.KeyHandler;

public class Player extends Enity {
    GamePanel gp;
    KeyHandler keyH;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        x = gp.screenWidth / 2 - gp.tileSize / 2;
        y = gp.screenHeight / 2 - gp.tileSize / 2;
        speed = 7;
        direction = "down";
    }
    public void getPlayerImage() {
        try {
            up1 =ImageIO.read(getClass().getResourceAsStream("/res/eat1.png"));
            up2 =ImageIO.read(getClass().getResourceAsStream("/res/eat2.png"));
            down1 =ImageIO.read(getClass().getResourceAsStream("/res/eat3.png"));
            down2 =ImageIO.read(getClass().getResourceAsStream("/res/eat4.png"));
            left1 =ImageIO.read(getClass().getResourceAsStream("/res/eat5.png"));
            left2 =ImageIO.read(getClass().getResourceAsStream("/res/eat6.png"));
            right1 =ImageIO.read(getClass().getResourceAsStream("/res/eat7.png"));
            right2 =ImageIO.read(getClass().getResourceAsStream("/res/eat8.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void update() {
        if (keyH.upPressed == true) {
            direction = "up";
            y = Math.max(0, y - speed);
        }
        else if (keyH.downPressed == true) {
            direction = "down";
            y = Math.min(gp.screenHeight - gp.tileSize, y + speed);
        }
        else if (keyH.leftPressed == true) {
            direction = "left";
            x = Math.max(0, x - speed);
        }
        else if (keyH.rightPressed == true) {
            direction = "right";
            x = Math.min(gp.screenWidth - gp.tileSize, x + speed);
        }
        spriteCounter++; // vẽ lại hướng đi mỗi 10 frame
        if (spriteCounter >10) {
            if (spriteNum == 1) {
                spriteNum = 2;
            } else if (spriteNum == 2) {
                spriteNum = 1;
            }
            spriteCounter = 0;   
        }
    }
    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        if (direction == null) {
            direction = "down";
        }
        switch(direction) {
            case "up":
                if (spriteNum == 1) {
                    image = up1;
                } else {
                    image = up2;
                }
                break;
            case "down":
                if (spriteNum == 1) {
                    image = down1;
                } else {
                    image = down2;
                }
                break;
            case "left":
                if (spriteNum == 1) {
                    image = left1;
                } else {
                    image = left2;
                }
                break;
            case "right":
                if (spriteNum == 1) {
                    image = right1;
                } else {
                    image = right2;
                }
                break;
        }
        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }
    }
}
