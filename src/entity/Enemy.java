package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;
import main.GamePanel;

public class Enemy extends Entity {
    
    GamePanel gp;
    Random rand = new Random();
    
    // AI Variables (Chuyển từ Entity sang đây)
    public int dy; 
    public int actionLockCounter = 0; 
    
    public String name = "";

    public Enemy(GamePanel gp) {
        this.gp = gp;
        
        this.solidArea = new Rectangle(0, 0, this.width, this.height);
    }

    
    public void update(boolean allowMove) {
        
        if (allowMove) {
            // Nếu đang quay đầu (Turn) thì đứng yên, không di chuyển vị trí
            if (!state.equals("turn")) {
                updateAI();
                updatePosition();
            }
        }
        
        updateAnimation();
    }

    private void updateAI() {
        if (gp.currentLevel.levelNum >= 2) {
            if("Anglerfish".equals(this.name) || "shark".equals(this.name) || "JohnDory".equals(this.name)){
                 huntPlayer();
            }
        } else {
           
            normalSwimAI();
        }
    }
    private void normalSwimAI() {
        actionLockCounter++;
        int changeTime = 30 + rand.nextInt(30);
        if (actionLockCounter >= changeTime) {
            dy = rand.nextInt(3) - 1; 
            if (rand.nextInt(100) < 20) {
                if (dy > 0) dy = 2; else if (dy < 0) dy = -2;
            }
            
            if (rand.nextInt(100) < 2) {
                startTurning();
            }
            actionLockCounter = 0;
        }
    }
    

    private void huntPlayer() {
        double distance = Math.sqrt(Math.pow(gp.player.x - this.x, 2) + Math.pow(gp.player.y - this.y, 2));
        if (distance < 220) {
            if (state.equals("swim")) { 
                if (gp.player.x < this.x && direction.equals("right")) {
                    startTurning(); 
                } else if (gp.player.x > this.x && direction.equals("left")) {
                    startTurning();
                }
            }
            
            if (direction.equals("left")) x -= 2 ; 
            else x += 1;

            if (gp.player.y > this.y) dy = 1;
            else if (gp.player.y < this.y) dy = -1;
        } else {
           
            normalSwimAI();
        }
    }


    private void updatePosition() {
        // Di chuyển ngang
        if (direction.equals("left")) x -= speed;
        else x += speed;

        // Di chuyển dọc
        y += dy;

        // Boundary Check (World)
        if (y < 0) { y = 0; dy = 1; }
        if (y > gp.worldHeight - height) { y = gp.worldHeight - height; dy = -1; }
        
        // Chạm biên ngang -> Quay đầu
        //if (x <= 0 && direction.equals("left")) startTurning();
        //if (x >= gp.worldWidth - width && direction.equals("right")) startTurning();

        // Update Hitbox
        solidArea.x = x;
        solidArea.y = y;
    }

    private void updateAnimation() {
        spriteCounter++;
        int animSpeed = 5; 
        
        if (spriteCounter > animSpeed) {
            spriteNum++;
            spriteCounter = 0;

            // --- STATE MACHINE ---
            if (state.equals("eat")) {
                if (eatFrames != null && spriteNum >= eatFrames.length) {
                    state = "swim"; spriteNum = 0;
                }
            } 
            else if (state.equals("turn")) {
                if (turnFrames != null && spriteNum >= turnFrames.length) {
                    flipDirection(); 
                    state = "swim"; spriteNum = 0;
                }
            } 
            else { // swim
                if (swimFrames != null && spriteNum >= swimFrames.length) {
                    spriteNum = 0;
                }
            }
        }
    }



    // Hàm vẽ riêng của Enemy
    public void draw(Graphics2D g2) {
        BufferedImage currentFrame = null;
        
        // Chọn Frame
        if (state.equals("eat") && eatFrames != null) {
            if (spriteNum < eatFrames.length) currentFrame = eatFrames[spriteNum];
        } 
        else if (state.equals("turn") && turnFrames != null) {
            if (spriteNum < turnFrames.length) currentFrame = turnFrames[spriteNum];
        } 
        else { // swim
            if (swimFrames != null && swimFrames.length > 0) {
                int idx = (spriteNum < swimFrames.length) ? spriteNum : 0;
                currentFrame = swimFrames[idx];
            }
        }
        
        if(currentFrame != null) {
            AffineTransform oldTransform = g2.getTransform();
            
            // Tính tọa độ màn hình
            int screenX = x - gp.cameraX;
            int screenY = y - gp.cameraY;
            
            // Culling (Tối ưu: Không vẽ nếu ngoài màn hình)
            if (screenX + width < 0 || screenX > gp.screenWidth ||
                screenY + height < 0 || screenY > gp.screenHeight) {
                return;
            }

            g2.translate(screenX, screenY); 

            // Logic Flip
            boolean needFlip = false;
            if (!state.equals("turn")) {
                if (direction.equals("right")) needFlip = true;
            } else {
                if (direction.equals("right")) needFlip = true;
            }

            if (needFlip) {
                g2.transform(AffineTransform.getScaleInstance(-1, 1));
                g2.translate(-width, 0); 
            }
            
            g2.drawImage(currentFrame, 0, 0, width, height, null);
            g2.setTransform(oldTransform);
        }
    }
}