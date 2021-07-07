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
import java.awt.*;
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
            BufferedImage texturka = new BufferedImage(t.wall.size * offset, t.wall.size * offset, BufferedImage.TYPE_INT_ARGB);
            try {
                if (t.wall instanceof TreeBlock) {
                    texturka = new BufferedImage(10 * offset, 10 * offset, 2);
                    texturka.getGraphics().drawImage(Service.getMyPic(t.wall.region.toString()), 0, 0, null);
                    image.getGraphics().drawImage(texturka, t.x * offset - getXOffset(10), (y_size * offset) - (t.y * offset) - getYOffset(10), null);
                    continue;
                }

                if (!t.wall.synthetic()) {
                    image.getGraphics().drawImage(Service.getMyPic(t.wall.region.toString()), t.x * offset - getXOffset(t.wall.size), (y_size * offset) - (t.y * offset) - getYOffset(t.wall.size), null);
                    continue;
                }

                if (t.wall instanceof BaseTurret || t.wall instanceof MassDriver) {
                    texturka.getGraphics().drawImage(Service.getMyPic("block-" + t.wall.size), 0, 0, null);
                }

                if (t.wall != Blocks.plastaniumConveyor && (t.wall instanceof Conveyor)) {
                    texturka.getGraphics().drawImage(Service.getMyPic(t.wall.name + Service.conveyorTextureIdGetter(tiles.find(a -> a.x + 1 == t.x && a.y == t.y), tiles.find(a -> a.x - 1 == t.x && a.y == t.y), tiles.find(a -> a.x == t.x && a.y - 1 == t.y), tiles.find(a -> a.x == t.x && a.y + 1 == t.y), t)), 0, 0, null);
                    texturka = Service.rotate(texturka, Math.toRadians(90 * t.build.rotation()), GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
                    texturka.getGraphics().drawImage(Service.tint(Service.getMyPic("block-border"), t.team.color), 0, texturka.getHeight() - 32, null);
                    image.getGraphics().drawImage(texturka, t.x * offset - getXOffset(t.wall.size), (y_size * offset) - (t.y * offset) - getYOffset(t.wall.size), null);
                    continue;
                }

                if (t.wall instanceof Conduit) {
                    texturka.getGraphics().drawImage(Service.getMyPic("conduit-bottom-0"), 0, 0, null);
                    texturka.getGraphics().drawImage(Service.getMyPic(t.wall.name + "-top-0"), 0, 0, null);
                    texturka = Service.rotate(texturka, Math.toRadians(90 * t.build.rotation()), GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
                    image.getGraphics().drawImage(texturka, t.x * offset - getXOffset(t.wall.size), (y_size * offset) - (t.y * offset) - getYOffset(t.wall.size), null);
                    continue;
                }

                if (Service.picExist(t.wall.region.toString() + "-bottom")) {
                    texturka.getGraphics().drawImage(Service.getMyPic(t.wall.region.toString() + "-bottom"), 0, 0, null);
                }

                texturka.getGraphics().drawImage(Service.getMyPic(t.wall.region.toString()), 0, 0, null);

                if (Service.picExist(t.wall.region.toString() + "-middle")) {
                    texturka.getGraphics().drawImage(Service.getMyPic(t.wall.region.toString() + "-middle"), 0, 0, null);
                }
                if (Service.picExist(t.wall.region.toString() + "-rotator")) {
                    texturka.getGraphics().drawImage(Service.getMyPic(t.wall.region.toString() + "-rotator"), 0, 0, null);
                }

                if (Service.picExist(t.wall.region.toString() + "-top") && (!(t.wall instanceof GenericCrafter) || t.wall == Blocks.cultivator) && !(t.wall instanceof PowerGenerator) && !(t.wall instanceof OverdriveProjector) && !(t.wall instanceof ForceProjector) && !(t.wall instanceof MendProjector)) {
                    texturka.getGraphics().drawImage(Service.getMyPic(t.wall.region.toString() + "-top"), 0, 0, null);
                }

                if (t.wall == Blocks.plastaniumConveyor) {
                    texturka.getGraphics().drawImage(Service.getMyPic(t.wall.region.toString()), 0, 0, null);
                    texturka = Service.rotate(texturka, Math.toRadians(90 * t.build.rotation()), GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
                }
                if (t.wall.rotate && t.build != null && t.build.rotation() != 0) {
                    texturka = Service.rotate(texturka, Math.toRadians(0), GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
                }
                if ((t.wall.teamRegion != null || t.team != null)) {
                    if (t.wall.teamRegion.toString().equals("error")) {
                        texturka.getGraphics().drawImage(Service.tint(Service.getMyPic("block-border"), t.team.color), 0, texturka.getHeight() - 32, null);
                    } else {
                        texturka.getGraphics().drawImage(Service.tint(Service.getMyPic(t.wall.teamRegion.toString()), t.team.color), 0, 0, null);
                    }
                }

                image.getGraphics().drawImage(texturka, t.x * offset - getXOffset(t.wall.size), (y_size * offset) - (t.y * offset) - getYOffset(t.wall.size), null);
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}