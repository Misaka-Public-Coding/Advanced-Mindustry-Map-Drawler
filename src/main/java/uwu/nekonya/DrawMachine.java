package uwu.nekonya;

import arc.graphics.g2d.Draw;
import arc.struct.Seq;
import arc.struct.StringMap;
import arc.util.Log;
import mindustry.content.Blocks;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Schematic;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class DrawMachine {
    public static int offset = 32;
    public Seq<Tile> tiles = new Seq<>();
    public int y_size;

    public BufferedImage image_output;

    public DrawMachine(Seq<Tile> t) {
        this.tiles = t;
        int x_ = 0;
        int y_ = 0;
        for(Tile ti : tiles){
            if (ti.x > x_) {
                x_ = ti.x;
            }
            if (ti.y > y_) {
                y_ = ti.y;
            }
        }
        y_size=y_;
        image_output = new BufferedImage(x_ * offset, y_ * offset, BufferedImage.TYPE_INT_RGB);
    }

    private static int getXOffset(int blockSize) {
        if (blockSize == 3) {
            return offset;
        }
        if (blockSize == 4) {
            return offset;
        }
        if (blockSize == 5) {
            return 2 * offset;
        }
        if (blockSize == 6) {
            return 2 * offset;
        }
        if (blockSize == 7) {
            return 3 * offset;
        }
        if (blockSize == 8 || blockSize == 9) {
            return 4 * offset;
        }
        if (blockSize == 10) {
            return 5 * offset;
        }
        return 0;
    }

    private static int getYOffset(int blockSize) {
        if (blockSize == 2) {
            return offset;
        }
        if (blockSize == 3) {
            return offset;
        }
        if (blockSize == 4) {
            return 2 * offset;
        }
        if (blockSize == 5) {
            return 2 * offset;
        }
        if (blockSize == 6 || blockSize == 7) {
            return 3 * offset;
        }
        if (blockSize == 8 || blockSize == 9) {
            return 4 * offset;
        }
        if (blockSize == 10) {
            return 5 * offset;
        }
        return 0;
    }

    public BufferedImage processing() {
        drawFloor();
        drawOverlay();
        drawBlocks();
        return image_output;
    }

    public void drawFloor() {
        for (Tile t : tiles) {
            if (t.floor == Blocks.air) {
                continue;
            }
            try {
                if(t.floor.region.toString().equalsIgnoreCase("error")){
                    Log.err(t.floor.name);
                }
                image_output.getGraphics().drawImage(Service.getImage(t.floor.region.toString()), t.x * offset, (y_size * offset) - (t.y * offset), null);
            } catch (NullPointerException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void drawOverlay() {
        for (Tile t : tiles) {
            if (t.overlay == Blocks.air) {
                continue;
            }
            try {
                image_output.getGraphics().drawImage(Service.getImage(t.overlay.region.toString()), t.x * offset, (y_size * offset) - (t.y * offset), null);
            } catch (NullPointerException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void drawBlocks(){
        int x = 0, y = 0;
        Seq<Schematic.Stile> stiles = new Seq<>();
        for (Tile t : tiles) {
            if (t.wall == Blocks.air) {
                continue;
            }
            if (!t.wall.synthetic()) {
                drawStaticObject(t);
            } else {
                if (t.x > x) {
                    x = t.x;
                }
                if (t.y > y) {
                    y = t.y;
                }
                stiles.add(new Schematic.Stile(t.wall, t.x, t.y, t.config(), t.rotation()));
            }
        }
        drawBuildings(new Schematic(stiles, new StringMap(), x, y));
    }

    public void drawBuildings(Schematic schem) {
        Draw.reset();
        Seq<BuildPlan> requests = schem.tiles.map(t -> new BuildPlan(t.x, t.y, t.rotation, t.block, t.config));
        uwu.nekonya.Service.currentImage = image_output;
        uwu.nekonya.Service.currentGraphics = image_output.createGraphics();
        requests.each(req -> {
            req.animScale = 1f;
            req.worldContext = false;
            req.block.drawPlanRegion(req, requests);
            Draw.reset();
        });

        requests.each(req -> req.block.drawPlanConfigTop(req, requests));
    }

    public void drawStaticObject(Tile t){
        try {
            image_output.getGraphics().drawImage(Service.getImage(t.wall.region.toString()), t.x * offset - getXOffset(t.wall.size), (y_size * offset) - (t.y * offset) - getYOffset(t.wall.size), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BufferedImage addTeamMark(Tile t, BufferedImage texture) throws IOException {
        if ((t.wall.teamRegion != null || t.team != null)) {
            if (t.wall.teamRegion != null && Objects.equals(t.wall.teamRegion.toString(), "error")) {
                texture.getGraphics().drawImage(Service.tint(Service.getImage("block-border"), t.team.color), 0, texture.getHeight() - 32, null);
            } else {
                texture.getGraphics().drawImage(Service.tint(Service.getImage(t.wall.teamRegion.toString()), t.team.color), 0, 0, null);
            }
        }
        return texture;
    }


}
