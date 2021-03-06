package uwu.misaka;

import arc.graphics.Color;
import arc.struct.Seq;
import arc.struct.StringMap;
import arc.util.io.CounterInputStream;
import mindustry.io.SaveIO;
import mindustry.io.SaveVersion;
import mindustry.world.Block;
import mindustry.world.CachedTile;
import mindustry.world.Tile;
import mindustry.world.WorldContext;
import mindustry.world.blocks.distribution.Conveyor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

import static mindustry.Vars.content;
import static mindustry.Vars.world;

public class Service {

    public static Seq<FakeTile> readMap(InputStream is) throws IOException {
        try (InputStream ifs = new InflaterInputStream(is); CounterInputStream counter = new CounterInputStream(ifs); DataInputStream stream = new DataInputStream(counter)) {
            Seq<FakeTile> rtn = new Seq<>();
            SaveIO.readHeader(stream);
            int version = stream.readInt();
            SaveVersion ver = SaveIO.getSaveWriter(version);
            StringMap[] metaOut = {null};
            ver.region("meta", stream, counter, in -> metaOut[0] = ver.readStringMap(in));

            StringMap meta = metaOut[0];


            int width = meta.getInt("width"), height = meta.getInt("height");

            Entry.w = width;
            Entry.h = height;

            CachedTile tile = new CachedTile() {
                @Override
                public void setBlock(Block type) {
                    super.setBlock(type);
                    FakeTile t = rtn.find(a -> a.x == this.x && a.y == this.y);
                    if (t != null) {
                        t.wall = type;
                    }
                }
            };

            ver.region("content", stream, counter, ver::readContentHeader);
            ver.region("preview_map", stream, counter, in -> ver.readMap(in, new WorldContext() {
                @Override
                public void resize(int width, int height) {
                }

                @Override
                public boolean isGenerating() {
                    return false;
                }

                @Override
                public void begin() {
                    world.setGenerating(true);
                }

                @Override
                public void end() {
                    world.setGenerating(false);
                }

                @Override
                public void onReadBuilding() {
                    //read team colors
                    if (tile.build != null) {
                        FakeTile t = rtn.find(a -> a.x == tile.x && a.y == tile.y);
                        if (t != null) {
                            t.build = tile.build;
                            if (tile.team() != null) {
                                t.team = tile.team();
                            }
                        }
                    }
                }

                @Override
                public Tile tile(int index) {
                    tile.x = (short) (index % width);
                    tile.y = (short) (index / width);
                    return tile;
                }

                @Override
                public Tile create(int x, int y, int floorID, int overlayID, int wallID) {
                    rtn.add(new FakeTile(x, y, floorID, overlayID, wallID));
                    return tile;
                }
            }));
            return rtn;
        } finally {
            content.setTemporaryMapper(null);
        }
    }

    public static boolean picExist(String regionName) {
        return Parser.imageFiles.get(regionName) != null;
    }

    public static BufferedImage getMyPic(String rg) throws IOException {
        if (rg.startsWith("error")) {
            return ImageIO.read(Parser.imageFiles.get("error").file());
        }
        try {
            return ImageIO.read(Parser.imageFiles.get(rg).file());
        } catch (Exception e) {
            System.out.println(rg);
            return ImageIO.read(Parser.imageFiles.get("error").file());
        }
    }

    public static BufferedImage tint(BufferedImage image, Color color) {
        BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Color tmp = new Color();
        for (int x = 0; x < copy.getWidth(); x++) {
            for (int y = 0; y < copy.getHeight(); y++) {
                int argb = image.getRGB(x, y);
                tmp.argb8888(argb);
                tmp.mul(color);
                copy.setRGB(x, y, tmp.argb8888());
            }
        }
        return copy;
    }

    public static BufferedImage rotate(BufferedImage image, double angle, GraphicsConfiguration gc) {
        double sin = Math.abs(Math.sin(angle)), cos = Math.abs(Math.cos(angle));
        int w = image.getWidth(), h = image.getHeight();
        int neww = (int) Math.floor(w * cos + h * sin), newh = (int) Math.floor(h
                * cos + w * sin);
        int transparency = image.getColorModel().getTransparency();
        BufferedImage result = gc.createCompatibleImage(neww, newh, transparency);
        Graphics2D g = result.createGraphics();
        g.translate((neww - w) / 2, (newh - h) / 2);
        g.rotate(angle, w / 2f, h / 2f);
        g.drawRenderedImage(image, null);
        return result;
    }

    public static String conveyorTextureIdGetter(FakeTile left, FakeTile right, FakeTile up, FakeTile down, FakeTile it) {
        FakeTile l = null, r = null, d = null;
        if (it.build != null) {
            if (it.build.rotation() == 0) {
                d = left;
                l = up;
                r = down;
            }
            if (it.build.rotation() == 1) {
                d = down;
                l = left;
                r = right;
            }
            if (it.build.rotation() == 2) {
                d = right;
                l = down;
                r = up;
            }
            if (it.build.rotation() == 3) {
                d = up;
                l = right;
                r = left;
            }
        }
        if (l != null && r != null && d != null) {
            if (l.wall instanceof Conveyor && checkRotation(it, l) && r.wall instanceof Conveyor && checkRotation(it, r) && d.wall instanceof Conveyor && checkRotation(it, d)) {
                return "-3-0";
            }
            if (l.wall instanceof Conveyor && checkRotation(it, l) && r.wall instanceof Conveyor && checkRotation(it, r)) {
                return "-4-0";
            }
            if (((l.wall instanceof Conveyor && checkRotation(it, l)) || (r.wall instanceof Conveyor && checkRotation(it, r))) && (d.wall instanceof Conveyor) && checkRotation(it, d)) {
                return "-2-0";
            }
            if ((l.wall instanceof Conveyor && checkRotation(it, l) || r.wall instanceof Conveyor && checkRotation(it, r))) {
                return "-1-0";
            }
        }
        return "-0-0";
    }

    private static boolean checkRotation(FakeTile c, FakeTile t) {

        if (c.x == t.x) {
            if (c.y > t.y) {
                if (t.build.rotation == 1) {
                    return true;
                }
            }
            if (c.y < t.y) {
                if (t.build.rotation == 3) {
                    return true;
                }
            }
        }
        if (c.y == t.y) {
            if (c.x > t.x) {
                if (t.build.rotation == 0) {
                    return true;
                }
            }
            if (c.x < t.x) {
                if (t.build.rotation == 2) {
                    return true;
                }
            }
        }
        return false;

    }

    ;
}