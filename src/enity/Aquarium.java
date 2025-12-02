package enity;

import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import main.GamePanel;

public class Aquarium extends Entity {
    GamePanel gp;
    Random rand = new Random();
    
    // Danh sách chứa tất cả cá (trừ người chơi)
    public ArrayList<Entity> entities = new ArrayList<>();
    
    // Bộ đếm thời gian
    int spawnCounter = 0;

    public Aquarium(GamePanel gp) {
        this.gp = gp;
    }

    // Hàm sinh ngẫu nhiên 1 con cá
    public void spawnEntity() {
        Entity obj = new Entity();

        // 1. Random vị trí (trong phạm vi World)
        obj.x = 0; // Bắt đầu từ bên trái
        obj.y = rand.nextInt(gp.screenHeight - gp.tileSize); // Vị trí y ngẫu nhiên
        
        // 2. Random loại cá
        int rate = rand.nextInt(100) + 1; // 1 đến 100
        
        if (rate <= 70) {
            obj.name = "Food"; // 70% là thức ăn
            obj.speed = 5;
            try {
                obj.up1 = ImageIO.read(getClass().getResourceAsStream("/res/eat1.png"));
            } catch (IOException e) {
                System.out.println("Lỗi khi tải ảnh thức ăn!");
                e.printStackTrace();
            }
            // obj.image = ... (Load ảnh cá bé ở đây hoặc trong class Entity)
        } else {
            obj.name = "Enemy"; // 30% là kẻ thù
            obj.speed = 7;
            try {
                obj.up1 = ImageIO.read(getClass().getResourceAsStream("/res/eat2.png"));
            } catch (IOException e) {
                System.out.println("Lỗi khi tải ảnh kẻ thù!");
                e.printStackTrace();
            }
            // obj.image = ... (Load ảnh cá mập)
        }

        entities.add(obj); // Thêm vào danh sách
    }

    // Hàm cập nhật (Gọi trong GamePanel.update)
    public void update() {
        // --- LOGIC SINH CÁ ---
        spawnCounter++;
        if (spawnCounter > 60) { // Cứ 60 khung hình (khoảng 1s) thì sinh 1 con
            spawnEntity();
            spawnCounter = 0;
        }

        // --- CẬP NHẬT VỊ TRÍ TỪNG CON ---
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            if (e != null) {
                e.x += e.speed; // Di chuyển sang phải
            }
            // (Tùy chọn) Xóa cá nếu bơi ra xa quá hoặc danh sách quá dài
        }
    }

    // Hàm vẽ (Gọi trong GamePanel.paintComponent)
    public void draw(Graphics2D g2) {
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            if (e != null) { 
                g2.drawImage(e.up1, e.x, e.y, gp.tileSize, gp.tileSize, null);
            }
        }
    }
}
