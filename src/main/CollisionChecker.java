package main;

import entity.Entity;
import entity.Player;
import java.awt.Rectangle;
import java.util.ArrayList;

public class CollisionChecker {
    
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkPlayerVsEnemies(Player player, ArrayList<Entity> enemies) {
        // Thu nhỏ hitbox Player (ví dụ: thụt vào 10px mỗi bên)
        int pPad = 10;
        player.solidArea.setBounds(player.x + pPad, player.y + pPad, player.width - 2*pPad, player.height - 2*pPad);

        for (int i = 0; i < enemies.size(); i++) {
            Entity enemy = enemies.get(i);
            if (enemy == null) continue;

            // >> SỬA: Hitbox Logic (Padding 20%)
            // Tạo khoảng đệm để hitbox nhỏ hơn ảnh thật
            int paddingX = (int)(enemy.width * 0.2); 
            int paddingY = (int)(enemy.height * 0.2); 

            // Cập nhật vị trí và kích thước hitbox đã thu nhỏ
            enemy.solidArea.setBounds(
                enemy.x + paddingX, 
                enemy.y + paddingY, 
                enemy.width - 2 * paddingX, 
                enemy.height - 2 * paddingY
            );

            if (player.solidArea.intersects(enemy.solidArea)) {
                processCollision(player, enemy, i);
            }
        }
    }

    private void processCollision(Player player, Entity enemy, int index) {
        // Tính diện tích (đại diện cho khối lượng/kích thước)
        int playerSize = player.width * player.height;
        int enemySize = enemy.width * enemy.height;

        if (playerSize > enemySize) {
            // Ăn được
            gp.aquarium.entities.remove(index);
            gp.score += 10 + (enemySize / 100); // Điểm thưởng dựa trên size
            // Có thể thêm logic: Ăn xong người chơi to lên một chút
            // player.width += 2; player.height += 2;
        } else {
            // Bị ăn (hoặc va chạm gây sát thương)
            gp.lives--;
            gp.aquarium.entities.remove(index); // Xóa kẻ thù sau khi va chạm để tránh trừ máu liên tục
            System.out.println("Ouch! Lives left: " + gp.lives);
            
            if (gp.lives <= 0) {
                gp.gameState = gp.gameOverState; // Cần implement state này trong GamePanel
                System.out.println("GAME OVER");
            }
        }
    }
}