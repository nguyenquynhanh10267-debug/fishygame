package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import main.GamePanel;
import main.MouseHandler;

public class Player extends Entity {
    GamePanel gp;
    MouseHandler mouseH;
    
    // Animation Constants
    final int SWIM_IDLE_FRAMES = 12; // Cần khai báo lại nếu không thừa kế từ Entity cha đủ
    final int TURN_FRAMES = 6;
    
    // State variables
    private String currentFacing = "right"; 
    
    public Player(GamePanel gp, MouseHandler mouseH) {
        this.gp = gp;
        this.mouseH = mouseH;
        
        // Khởi tạo mảng frames (QUAN TRỌNG: Phải khởi tạo mảng trước khi load ảnh)
        idleFrames = new BufferedImage[SWIM_IDLE_FRAMES];
        swimFrames = new BufferedImage[SWIM_IDLE_FRAMES];
        turnFrames = new BufferedImage[TURN_FRAMES];
        
        setDefaultValues();
        getPlayerImageByLoop(); // Phải gọi hàm này để load ảnh
    }

    public void setDefaultValues() {
        x = gp.screenWidth / 2 - gp.tileSize / 2;
        y = gp.screenHeight / 2 - gp.tileSize / 2;
        speed = 5;
        width = 64; 
        height = 64;
        state = "idle";
        direction = "right";
        currentFacing = "right";
        solidArea = new Rectangle(x, y, width, height);
    }

    public void getPlayerImageByLoop() {
        try {
            for (int i = 0; i < SWIM_IDLE_FRAMES; i++) {
                idleFrames[i] = ImageIO.read(getClass().getResourceAsStream("/res/idle" + (i + 1) + ".png"));
                swimFrames[i] = ImageIO.read(getClass().getResourceAsStream("/res/swim" + (i + 1) + ".png"));
            }
            for (int i = 0; i < TURN_FRAMES; i++) {
                turnFrames[i] = ImageIO.read(getClass().getResourceAsStream("/res/turn" + (i + 1) + ".png"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        // --- 1. MOVEMENT LOGIC (VECTOR) ---
        double dx = mouseH.mouseX - (x + width / 2);
        double dy = mouseH.mouseY - (y + height / 2);
        double distance = Math.sqrt(dx*dx + dy*dy);
        
        boolean isMoving = distance > 10; // Ngưỡng chết để tránh rung lắc
        String newFacing = currentFacing;

        if (isMoving) {
            // Chuẩn hóa vector và di chuyển
            double moveX = (dx / distance) * speed;
            double moveY = (dy / distance) * speed;
            x += moveX;
            y += moveY;

            // Xác định hướng dựa trên dx
            if (dx > 0) newFacing = "right";
            else if (dx < 0) newFacing = "left";
        }

        // --- 2. STATE & FACING LOGIC ---
        // Logic quay đầu (Turn)
        if (!newFacing.equals(currentFacing)) {
            state = "turn";
            currentFacing = newFacing;
            spriteNum = 0; // Reset animation quay đầu
        } 
        
        // Nếu không đang quay đầu
        if (!state.equals("turn")) {
            if (isMoving) {
                state = "swim";
            } else {
                state = "idle";
            }
        }

        // --- 3. BOUNDARY CHECK ---
        if(x < 0) x = 0;
        if(x > gp.screenWidth - width) x = gp.screenWidth - width;
        if(y < 0) y = 0;
        if(y > gp.screenHeight - height) y = gp.screenHeight - height;

        // --- 4. HITBOX UPDATE ---
        solidArea.x = (int)x;
        solidArea.y = (int)y;

        // --- 5. ANIMATION COUNTER (Rất quan trọng) ---
        spriteCounter++;
        int animationSpeed = 4; // Tốc độ chuyển frame

        if (spriteCounter > animationSpeed) {
            spriteNum++;
            spriteCounter = 0;

            if (state.equals("turn")) {
                if (spriteNum >= TURN_FRAMES) {
                    spriteNum = 0;
                    state = "swim"; // Quay xong thì bơi tiếp
                }
            } else {
                // Swim hoặc Idle
                if (spriteNum >= SWIM_IDLE_FRAMES) {
                    spriteNum = 0;
                }
            }
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage currentFrame = null;

        // An toàn: Kiểm tra null array trước khi truy cập
        if (idleFrames == null || swimFrames == null || turnFrames == null) return;

        // Chọn frame
        if (state.equals("turn")) {
            // Clamp spriteNum để tránh lỗi ArrayOutOfBounds
            if (spriteNum < TURN_FRAMES) currentFrame = turnFrames[spriteNum];
        } else if (state.equals("swim")) {
            if (spriteNum < SWIM_IDLE_FRAMES) currentFrame = swimFrames[spriteNum];
        } else { // idle
            if (spriteNum < SWIM_IDLE_FRAMES) currentFrame = idleFrames[spriteNum];
        }

        if (currentFrame != null) {
            int targetWidth = gp.tileSize; 
            int targetHeight = gp.tileSize;
            
            AffineTransform oldTransform = g2.getTransform();
            g2.translate(x, y);

            // Logic Flip: Nếu đang nhìn sang phải, lật hình (giả sử ảnh gốc hướng sang trái)
            // LƯU Ý: Kiểm tra ảnh gốc của bạn. 
            // Nếu ảnh gốc hướng TRÁI -> code dưới đúng.
            // Nếu ảnh gốc hướng PHẢI -> xóa logic transform scale(-1,1) hoặc đảo điều kiện.
            if (currentFacing.equals("right") && !state.equals("turn")) {
                g2.transform(AffineTransform.getScaleInstance(-1, 1));
                g2.translate(-targetWidth, 0);
            }

            g2.drawImage(currentFrame, 0, 0, targetWidth, targetHeight, null);
            g2.setTransform(oldTransform);
        }
    }
}