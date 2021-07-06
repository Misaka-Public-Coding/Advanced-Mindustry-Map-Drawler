package uwu.misaka;

import arc.graphics.Pixmap;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.world.blocks.defense.ForceProjector;
import mindustry.world.blocks.defense.MendProjector;
import mindustry.world.blocks.defense.OverdriveProjector;
import mindustry.world.blocks.defense.turrets.BaseTurret;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.distribution.MassDriver;
import mindustry.world.blocks.environment.TreeBlock;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.power.PowerGenerator;
import mindustry.world.blocks.production.GenericCrafter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Drawler {
    public Seq<FakeTile> tiles = new Seq<>();
    public int x_size;
    public int y_size;
    public static int offset = 32;

    public BufferedImage image;
    Pixmap pixmap;

    public Drawler(Seq<FakeTile> tt, int w, int h) {
        x_size = w;
        y_size = h - 1;
        tt.each(t -> tiles.add(t));
        System.out.println(x_size + " " + (y_size + 1));
        image = new BufferedImage(x_size * offset, (1 + y_size) * offset, BufferedImage.TYPE_INT_ARGB);
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

    public void drawBlocks() {
        for (FakeTile t : tiles) {
            if (t.wall == Blocks.air) {
                continue;
            }
            try {
                if (t.wall instanceof TreeBlock) {
                    image.getGraphics().drawImage(Service.getMyPic(t.wall.region.toString()), t.x * offset - getXOffset(10), (y_size * offset) - (t.y * offset) - getYOffset(10), null);
                    continue;
                }

                if (t.wall instanceof BaseTurret || t.wall instanceof MassDriver) {
                    image.getGraphics().drawImage(Service.getMyPic("block-" + t.wall.size), t.x * offset - getXOffset(t.wall.size), (y_size * offset) - (t.y * offset) - getYOffset(t.wall.size), null);
                }

                if (!t.wall.synthetic()) {
                    image.getGraphics().drawImage(Service.getMyPic(t.wall.region.toString()), t.x * offset - getXOffset(t.wall.size), (y_size * offset) - (t.y * offset) - getYOffset(t.wall.size), null);
                    continue;
                }

                if (t.wall != Blocks.plastaniumConveyor && (t.wall instanceof Conveyor)) {
                    image.getGraphics().drawImage(Service.getMyPic(t.wall.name + "-0-0"), t.x * offset - getXOffset(t.wall.size), (y_size * offset) - (t.y * offset) - getYOffset(t.wall.size), null);
                    continue;
                }
                if (t.wall instanceof Conduit) {
                    image.getGraphics().drawImage(Service.getMyPic("conduit-bottom-0"), t.x * offset - getXOffset(t.wall.size), (y_size * offset) - (t.y * offset) - getYOffset(t.wall.size), null);
                    image.getGraphics().drawImage(Service.getMyPic(t.wall.name + "-top-0"), t.x * offset - getXOffset(t.wall.size), (y_size * offset) - (t.y * offset) - getYOffset(t.wall.size), null);
                    continue;
                }

                if (Service.picExist(t.wall.region.toString() + "-bottom")) {
                    image.getGraphics().drawImage(Service.getMyPic(t.wall.region.toString() + "-bottom"), t.x * offset - getXOffset(t.wall.size), (y_size * offset) - (t.y * offset) - getYOffset(t.wall.size), null);
                }

                image.getGraphics().drawImage(Service.getMyPic(t.wall.region.toString()), t.x * offset - getXOffset(t.wall.size), (y_size * offset) - (t.y * offset) - getYOffset(t.wall.size), null);

                if (Service.picExist(t.wall.region.toString() + "-middle")) {
                    image.getGraphics().drawImage(Service.getMyPic(t.wall.region.toString() + "-middle"), t.x * offset - getXOffset(t.wall.size), (y_size * offset) - (t.y * offset) - getYOffset(t.wall.size), null);
                }
                if (Service.picExist(t.wall.region.toString() + "-rotator")) {
                    image.getGraphics().drawImage(Service.getMyPic(t.wall.region.toString() + "-rotator"), t.x * offset - getXOffset(t.wall.size), (y_size * offset) - (t.y * offset) - getYOffset(t.wall.size), null);
                }

                if (Service.picExist(t.wall.region.toString() + "-top") && (!(t.wall instanceof GenericCrafter) || t.wall == Blocks.cultivator) && !(t.wall instanceof PowerGenerator) && !(t.wall instanceof OverdriveProjector) && !(t.wall instanceof ForceProjector) && !(t.wall instanceof MendProjector)) {
                    image.getGraphics().drawImage(Service.getMyPic(t.wall.region.toString() + "-top"), t.x * offset - getXOffset(t.wall.size), (y_size * offset) - (t.y * offset) - getYOffset(t.wall.size), null);
                }

                if (t.wall == Blocks.plastaniumConveyor) {
                    image.getGraphics().drawImage(Service.getMyPic(t.wall.region.toString()), t.x * offset - getXOffset(t.wall.size), (y_size * offset) - (t.y * offset) - getYOffset(t.wall.size), null);
                }
                if ((t.wall.teamRegion != null || t.team != null)) {
                    if (t.wall.teamRegion.toString().equals("error")) {
                        image.getGraphics().drawImage(Service.tint(Service.getMyPic("block-border"), t.team.color), t.x * offset - getXOffset(t.wall.size), (y_size * offset) - (t.y * offset) - getYOffset(t.wall.size) + (t.wall.size - 1) * offset, null);
                    } else {
                        image.getGraphics().drawImage(Service.tint(Service.getMyPic(t.wall.teamRegion.toString()), t.team.color), t.x * offset - getXOffset(t.wall.size), (y_size * offset) - (t.y * offset) - getYOffset(t.wall.size), null);
                    }
                }
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}