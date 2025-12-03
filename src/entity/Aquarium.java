package entity;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import main.GamePanel;

public class Aquarium {
    GamePanel gp;
    Random rand = new Random();
    
    // Danh sách chứa các thực thể (quái vật)
    public ArrayList<Entity> entities = new ArrayList<>();
    
    // Bộ đếm thời gian spawn (60 frames = 1 giây nếu 60FPS)
    int spawnCounter = 0;

    public Aquarium(GamePanel gp) {
        this.gp = gp;
    }

    /**
     * Hàm sinh quái vật ngẫu nhiên
     * Logic: Random loại -> Random hướng -> Thiết lập vị trí xuất phát
     */
    public void spawnEntity() {
        int dice = rand.nextInt(100); 
        Entity monster;
        
        // Factory Pattern: Tạo quái vật từ Feature
        if (dice < 30) monster = gp.feature.createMonster(gp.feature.oyster);      // 30%
        else if (dice < 55) monster = gp.feature.createMonster(gp.feature.jellyPink); // 25%
        else if (dice < 75) monster = gp.feature.createMonster(gp.feature.john);      // 20%
        else if (dice < 90) monster = gp.feature.createMonster(gp.feature.lion);      // 15%
        else monster = gp.feature.createMonster(gp.feature.puffShark);                // 10%

        // Random hướng di chuyển: trái hoặc phải
        boolean isRight = rand.nextBoolean();
        monster.direction = isRight ? "right" : "left";
        
        // Random độ cao (Y) trong màn hình
        monster.y = rand.nextInt(gp.screenHeight - monster.height); // Trừ height để không bị cắt dưới đáy
        
        // Thiết lập vị trí X xuất phát (Ngoài màn hình để tạo hiệu ứng bơi vào)
        if (isRight) {
            monster.x = -monster.width; // Xuất phát từ bên trái (ẩn) -> bơi sang phải
        } else {
            monster.x = gp.screenWidth; // Xuất phát từ bên phải (ẩn) -> bơi sang trái
        }
        
        entities.add(monster);
    }

    /**
     * Cập nhật logic vị trí và xóa quái vật
     */
    public void update() {
        spawnCounter++;
        // Tốc độ spawn: 60 frame (1s) sinh 1 con
        if (spawnCounter > 60) { 
            spawnEntity();
            spawnCounter = 0;
        }

        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            if (e != null) {
                // 1. Di chuyển
                if (e.direction.equals("left")) {
                    e.x -= e.speed;
                } else {
                    e.x += e.speed;
                }
                
                // 2. Cập nhật vị trí Hitbox (QUAN TRỌNG: để collisionChecker hoạt động đúng)
                // Lưu ý: Việc thu nhỏ hitbox (padding) được xử lý trong CollisionChecker
                // Ở đây ta chỉ cập nhật tọa độ gốc.
                e.solidArea.x = e.x;
                e.solidArea.y = e.y;
                
                // 3. Garbage Collection logic: Xóa nếu bơi quá xa khỏi màn hình
                // Tạo vùng đệm 200 pixel để chắc chắn cá đã khuất hẳn mới xóa
                if (e.x < -200 || e.x > gp.screenWidth + 200) {
                    entities.remove(i);
                    i--; // Lùi index để không bỏ sót phần tử kế tiếp do danh sách bị dồn
                }
            }
        }
    }

    /**
     * Vẽ quái vật với phép biến đổi Affine để lật hình
     */
    public void draw(Graphics2D g2) {
        for (Entity e : entities) {
            // Lấy ảnh gốc. Giả sử ảnh trong Feature (up1/imageLeft) là hướng sang TRÁI.
            BufferedImage img = e.up1; 
            
            if(img != null) {
                // Lưu trạng thái ma trận hiện tại của Graphics2D
                AffineTransform oldTransform = g2.getTransform();
                
                // 1. Dời gốc tọa độ (0,0) về vị trí của con cá (Translation)
                g2.translate(e.x, e.y); 

                // 2. Kiểm tra hướng để lật hình (Reflection/Flip)
                // Nếu cá bơi sang PHẢI (ngược với ảnh gốc hướng TRÁI) -> Cần lật
                if (e.direction.equals("right")) {
                    // Tạo ma trận lật: Scale X = -1 (lật ngang), Scale Y = 1 (giữ nguyên)
                    g2.transform(AffineTransform.getScaleInstance(-1, 1));
                    // Sau khi lật, tọa độ bị đảo ngược, cần dịch lùi lại bằng chiều rộng ảnh
                    g2.translate(-e.width, 0); 
                }

                // 3. Vẽ ảnh
                // Vẽ tại (0,0) vì ta đã translate gốc tọa độ đến (e.x, e.y) ở bước 1
                g2.drawImage(img, 0, 0, e.width, e.height, null);
                
                // 4. Khôi phục trạng thái ma trận cũ để không ảnh hưởng đến các đối tượng khác vẽ sau
                g2.setTransform(oldTransform);
            }
        }
    }
}