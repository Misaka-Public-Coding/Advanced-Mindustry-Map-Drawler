package uwu.misaka;

import arc.files.Fi;
import arc.struct.Seq;
import arc.struct.StringMap;
import arc.util.io.CounterInputStream;
import mindustry.content.Blocks;
import mindustry.gen.Building;
import mindustry.io.MapIO;
import mindustry.io.SaveIO;
import mindustry.io.SaveVersion;
import mindustry.maps.Map;
import mindustry.world.Block;
import mindustry.world.CachedTile;
import mindustry.world.Tile;
import mindustry.world.WorldContext;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

import static mindustry.Vars.content;
import static mindustry.Vars.world;

public class Service {
    public static Map loadMap(Fi file) throws IOException {

        Map map = MapIO.createMap(file, true);

        if (map.name() == null) {
            throw new IOException("Map name cannot be empty!");
        }
        return map;
    }

    public static Seq<FakeTile> loadMap(Map map) {
        return null;
    }

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

    @Deprecated
    public static Seq<FakeTile> readMap2(InputStream is) throws IOException {
        Seq<FakeTile> rtn = new Seq<>();
        try (InputStream ifs = new InflaterInputStream(is); CounterInputStream counter = new CounterInputStream(ifs); DataInputStream stream = new DataInputStream(counter)) {
            SaveIO.readHeader(stream);
            int version = stream.readInt();
            SaveVersion ver = SaveIO.getSaveWriter(version);
            StringMap[] metaOut = {null};
            ver.region("meta", stream, counter, in -> metaOut[0] = ver.readStringMap(in));
            StringMap meta = metaOut[0];
            int width = meta.getInt("width"), height = meta.getInt("height");
            ver.region("content", stream, counter, ver::readContentHeader);
            Entry.w = width;
            Entry.h = height;
            CachedTile tile = new CachedTile() {
                @Override
                public void setBlock(Block type) {
                    if (type != Blocks.air) {
                        System.out.println(type.name + " tile " + this.x + " " + this.y);
                    }
                }
            };

            ver.region("map", stream, counter, in -> ver.readMap(in, new WorldContext() {
                @Override
                public Tile tile(int index) {
                    tile.x = (short) (index % width);
                    tile.y = (short) (index / width);
                    tile.build = Building.create();
                    return tile;
                }

                @Override
                public void resize(int width, int height) {

                }

                @Override
                public Tile create(int x, int y, int floorID, int overlayID, int wallID) {
                    CachedTile t = new CachedTile();
                    rtn.add(new FakeTile(x, y, floorID, overlayID, wallID));
                    return t;
                }

                @Override
                public boolean isGenerating() {
                    return false;
                }

                @Override
                public void begin() {
                }

                @Override
                public void end() {
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rtn;
    }

    public static BufferedImage getMyPic(String rg) throws IOException {
        if (rg.equals("error")) {
            return ImageIO.read(Parser.imageFiles.get("block-border").file());
        }
        try {
            return ImageIO.read(Parser.imageFiles.get(rg).file());
        } catch (Exception e) {
            System.out.println(rg);
            return ImageIO.read(Parser.imageFiles.get("block-border").file());
        }
    }
}
