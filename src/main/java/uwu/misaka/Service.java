package uwu.misaka;

import arc.files.Fi;
import arc.graphics.g2d.TextureAtlas;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import arc.struct.StringMap;
import arc.util.io.CounterInputStream;
import mindustry.io.MapIO;
import mindustry.io.SaveIO;
import mindustry.io.SaveVersion;
import mindustry.maps.Map;
import mindustry.world.Block;
import mindustry.world.CachedTile;
import mindustry.world.Tile;
import mindustry.world.WorldContext;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

public class Service {
    public static Map loadMap(Fi file) throws IOException {

        Map map = MapIO.createMap(file, true);

        if (map.name() == null) {
            throw new IOException("Map name cannot be empty!");
        }
        return map;
    }

    public static Seq<FakeTile> readMap(InputStream is) throws IOException {
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
                    System.out.println(type.name + " tile " + this.x + " " + this.y);
                }
            };

            ver.region("map", stream, counter, in -> ver.readMap(in, new WorldContext() {
                @Override
                public Tile tile(int index) {
                    tile.x = (short) (index % width);
                    tile.y = (short) (index / width);
                    return tile;
                }

                @Override
                public void resize(int width, int height) {

                }

                @Override
                public Tile create(int x, int y, int floorID, int overlayID, int wallID) {
                    //System.out.println(x+" "+y+" "+floorID+" "+overlayID+" "+wallID);
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

    public static BufferedImage get(TextureRegion region) {
        System.out.println(((TextureAtlas.AtlasRegion) region).name);
        return Entry.parser.regions.get(((TextureAtlas.AtlasRegion) region).name);
    }

    public static BufferedImage get(String name) {
        return Entry.parser.regions.get(name);
    }
}
