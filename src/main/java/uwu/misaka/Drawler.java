package uwu.misaka;

import arc.graphics.Pixmap;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.SolidPump;
import mindustry.world.blocks.units.Reconstructor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Drawler {
    public Seq<FakeTile> tiles = new Seq<>();
    public int x_size;
    public int y_size;
    public int offset = 32;

    public BufferedImage image;
    Pixmap pixmap;

    public Drawler(Seq<FakeTile> tt, int w, int h) {
        x_size = w;
        y_size = h;
        tt.each(t -> tiles.add(t));
        System.out.println(x_size + " " + y_size);
        image = new BufferedImage(x_size * offset, y_size * offset, BufferedImage.TYPE_INT_ARGB);
        pixmap = new Pixmap(x_size * offset, y_size * offset);
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
                image.getGraphics().drawImage(Service.getMyPic(t.floor.region.toString()), t.x * offset, (y_size * offset) - (t.y * offset), null);
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
                image.getGraphics().drawImage(Service.getMyPic(t.overlay.region.toString()), t.x * offset, (y_size * offset) - (t.y * offset), null);
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
                int y_offset = 0;

                if (t.wall.size == 2) {
                    y_offset = 1 * offset;
                }
                if (t.wall.size == 3) {
                    y_offset = 1 * offset;
                }
                if (t.wall.size == 4) {
                    y_offset = 2 * offset;
                }
                if (t.wall.size == 5) {
                    y_offset = 2 * offset;
                }

                int x_offset = 0;

                if (t.wall.size == 3) {
                    x_offset = 1 * offset;
                }
                if (t.wall.size == 4) {
                    x_offset = 1 * offset;
                }
                if (t.wall.size == 5) {
                    x_offset = 2 * offset;
                }

                if (t.wall instanceof Turret) {
                    image.getGraphics().drawImage(Service.getMyPic("block-" + t.wall.size), t.x * offset - x_offset, (y_size * offset) - (t.y * offset) - y_offset, null);
                }

                image.getGraphics().drawImage(Service.getMyPic(t.wall.region.toString()), t.x * offset - x_offset, (y_size * offset) - (t.y * offset) - y_offset, null);

                if ((t.wall instanceof Reconstructor)) {
                    image.getGraphics().drawImage(Service.getMyPic(t.wall.region.toString() + "-top"), t.x * offset - x_offset, (y_size * offset) - (t.y * offset) - y_offset, null);
                } else if (t.wall instanceof Drill || t.wall instanceof SolidPump) {
                    image.getGraphics().drawImage(Service.getMyPic(t.wall.region.toString() + "-rotator"), t.x * offset - x_offset, (y_size * offset) - (t.y * offset) - y_offset, null);
                    image.getGraphics().drawImage(Service.getMyPic(t.wall.region.toString() + "-top"), t.x * offset - x_offset, (y_size * offset) - (t.y * offset) - y_offset, null);
                } else if (t.wall == Blocks.cultivator) {
                    image.getGraphics().drawImage(Service.getMyPic(t.wall.region.toString() + "-middle"), t.x * offset - x_offset, (y_size * offset) - (t.y * offset) - y_offset, null);
                    image.getGraphics().drawImage(Service.getMyPic(t.wall.region.toString() + "-top"), t.x * offset - x_offset, (y_size * offset) - (t.y * offset) - y_offset, null);
                } else if (t.wall instanceof Conveyor) {
                    if (t.wall != Blocks.plastaniumConveyor) {
                        image.getGraphics().drawImage(Service.getMyPic(t.wall.region.toString() + "-0-0"), t.x * offset - x_offset, (y_size * offset) - (t.y * offset) - y_offset, null);
                    } else {
                        image.getGraphics().drawImage(Service.getMyPic(t.wall.region.toString()), t.x * offset - x_offset, (y_size * offset) - (t.y * offset) - y_offset, null);
                    }
                }
                if ((t.wall.teamRegion != null || t.team != null) && t.wall.synthetic()) {
                    if (t.wall.teamRegion.toString().equals("error")) {
                        image.getGraphics().drawImage(Service.tint(Service.getMyPic(t.wall.teamRegion.toString()), t.team.color), t.x * offset - x_offset, (y_size * offset) - (t.y * offset) - y_offset + (t.wall.size - 1) * offset, null);
                    } else {
                        image.getGraphics().drawImage(Service.tint(Service.getMyPic(t.wall.teamRegion.toString()), t.team.color), t.x * offset - x_offset, (y_size * offset) - (t.y * offset) - y_offset, null);
                    }
                }
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}