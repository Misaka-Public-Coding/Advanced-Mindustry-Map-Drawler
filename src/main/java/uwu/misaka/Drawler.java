package uwu.misaka;

import arc.graphics.Pixmap;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.core.World;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Drawler {
    public Seq<FakeTile> tiles = new Seq<>();
    public int x_size;
    public int y_size;
    public BufferedImage image;
    public World world;
    Pixmap pixmap;

    public Drawler(Seq<FakeTile> tt, int w, int h) {
        x_size = w;
        y_size = h;
        tt.each(t -> tiles.add(t));
        System.out.println(x_size + " " + y_size);
        image = new BufferedImage(x_size * 32, y_size * 32, BufferedImage.TYPE_INT_ARGB);
        pixmap = new Pixmap(x_size * 32, y_size * 32);
        drawFloor();
        drawOverlay();
        drawBlocks();
        try {
            ImageIO.write(image, "png", new File("baka.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("done");
    }

    public void drawFloor() {
        for (FakeTile t : tiles) {
            if (t.floor == Blocks.air) {
                continue;
            }
            try {
                image.getGraphics().drawImage(Service.getMyPic(t.floor.region.toString()), t.x * 32, (y_size * 32) - (t.y * 32), null);
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void drawOverlay() {
        for (FakeTile t : tiles) {
            if (t.overlay == Blocks.air) {
                continue;
            }
            try {
                image.getGraphics().drawImage(Service.getMyPic(t.overlay.region.toString()), t.x * 32, (y_size * 32) - (t.y * 32), null);
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void drawBlocks() {
        for (FakeTile t : tiles) {
            if (t.wall == Blocks.air) {
                continue;
            }
            try {
                image.getGraphics().drawImage(Service.getMyPic(t.wall.region.toString()), t.x * 32, (y_size * 32) - (t.y * 32), null);
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
