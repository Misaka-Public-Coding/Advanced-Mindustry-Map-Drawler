package uwu.misaka;

import arc.graphics.Pixmap;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.core.World;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

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
        //pixmap = new Pixmap(x_size*32,y_size*32);
        drawFloor();
        drawBlocks();
        try {
            File file = new File("output.png");
            file.createNewFile();
            ImageIO.write(image, "png", file);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Save err");
        }
        System.out.println("done");
    }

    public void drawFloor() {
        for (FakeTile t : tiles) {
            if (t.floor == Blocks.air) {
                continue;
            }
            //image.getGraphics().drawImage(Service.get(t.floor.teamRegion),t.x*32,t.y*32,null);
            image.getGraphics().drawImage(Service.get(t.floor.name), t.x * 32, t.y * 32, null);
        }
    }

    public void drawOverlay() {
        for (FakeTile t : tiles) {
            if (t.overlay == Blocks.air) {
                continue;
            }
            //image.getGraphics().drawImage(Service.get(t.overlay.teamRegion),t.x*32,t.y*32,null);
            image.getGraphics().drawImage(Service.get(t.overlay.name), t.x * 32, t.y * 32, null);
        }
    }

    public void drawBlocks() {
        for (FakeTile t : tiles) {
            if (t.wall == Blocks.air) {
                continue;
            }
            try {
                //image.getGraphics().drawImage(Service.get(t.wall.teamRegion), t.x * 32, t.y * 32, null);
                image.getGraphics().drawImage(Service.get(t.wall.name), t.x * 32, t.y * 32, null);
            } catch (NullPointerException e) {
                System.out.println(t.wall.name);
            }
        }
    }
}
