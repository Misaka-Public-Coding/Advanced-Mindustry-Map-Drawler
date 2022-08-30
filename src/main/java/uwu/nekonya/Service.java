package uwu.nekonya;

import arc.Core;
import arc.files.Fi;
import arc.graphics.Color;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.SpriteBatch;
import arc.graphics.g2d.TextureAtlas;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.struct.StringMap;
import arc.util.io.CounterInputStream;
import mindustry.Vars;
import mindustry.core.ContentLoader;
import mindustry.core.GameState;
import mindustry.core.Version;
import mindustry.core.World;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.io.SaveIO;
import mindustry.io.SaveVersion;
import mindustry.world.Block;
import mindustry.world.CachedTile;
import mindustry.world.WorldContext;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.InflaterInputStream;

import static mindustry.Vars.content;
import static mindustry.Vars.world;

public class Service {

    public static Graphics2D currentGraphics;
    public static BufferedImage currentImage;
    public static ObjectMap<String, Fi> imageFiles = new ObjectMap<>();

    public static void initParser() {
        Version.enabled = false;
        Vars.content = new ContentLoader();
        Vars.content.createBaseContent();
        for (ContentType type : ContentType.all) {
            for (Content content : Vars.content.getBy(type)) {
                try {
                    content.init();
                } catch (Throwable ignored) {
                }
            }
        }

        new Fi("sprites_out").walk(f -> {
            if (f.extEquals("png")) {
                imageFiles.put(f.nameWithoutExtension(), f);
            }
        });

        Vars.state = new GameState();

        TextureAtlas.TextureAtlasData data = new TextureAtlas.TextureAtlasData(new Fi("sprites/sprites.aatls"), new Fi("sprites"), false);
        Core.atlas = new TextureAtlas();

        data.getPages().each(page -> {
            page.texture = Texture.createEmpty(null);
            page.texture.width = page.width;
            page.texture.height = page.height;
        });

        data.getRegions().each(reg -> Core.atlas.addRegion(reg.name, new TextureAtlas.AtlasRegion(reg.page.texture, reg.left, reg.top, reg.width, reg.height) {{
            name = reg.name;
            texture = reg.page.texture;
        }}));

        Core.atlas.setErrorRegion("error");

        for (ContentType type : ContentType.values()) {
            for (Content content : Vars.content.getBy(type)) {
                try {
                    content.load();
                    content.loadIcon();
                } catch (Throwable ignored) {
                }
            }
        }

        Draw.scl = 1f / 4f;

        Core.batch = new SpriteBatch(0) {
            @Override
            protected void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float rotation) {
                x += 4;
                y += 4;

                x *= 4;
                y *= 4;
                width *= 4;
                height *= 4;

                y = currentImage.getHeight() - (y + height / 2f) - height / 2f;

                AffineTransform at = new AffineTransform();
                at.translate(x, y);
                at.rotate(-rotation * Mathf.degRad, originX * 4, originY * 4);

                currentGraphics.setTransform(at);
                BufferedImage image = getImage(((TextureAtlas.AtlasRegion) region).name);
                if (!color.equals(Color.white)) {
                    image = tint(image, color);
                }

                currentGraphics.drawImage(image, 0, 0, (int) width, (int) height, null);
            }

            @Override
            protected void draw(Texture texture, float[] spriteVertices, int offset, int count) {
                //do nothing
            }
        };

        world = new World() {
            public mindustry.world.Tile tile(int x, int y) {
                return new mindustry.world.Tile(x, y);
            }
        };
        world.tile(1, 1);
    }

    public static InputStream download(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
            return connection.getInputStream();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage getImage(String name) {
        try {
            File file = imageFiles.get(name, imageFiles.get("error")).file();
            return ImageIO.read(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Seq<Tile> readMap(InputStream is) throws IOException {
        try (InputStream ifs = new InflaterInputStream(is); CounterInputStream counter = new CounterInputStream(ifs); DataInputStream stream = new DataInputStream(counter)) {
            Seq<Tile> rtn = new Seq<>();
            SaveIO.readHeader(stream);
            int version = stream.readInt();
            SaveVersion ver = SaveIO.getSaveWriter(version);
            StringMap[] metaOut = {null};
            ver.region("meta", stream, counter, in -> metaOut[0] = ver.readStringMap(in));

            StringMap meta = metaOut[0];


            int width = meta.getInt("width"), height = meta.getInt("height");

            CachedTile tile = new CachedTile() {
                @Override
                public void setBlock(Block type) {
                    super.setBlock(type);
                    Tile t = rtn.find(a -> a.x == this.x && a.y == this.y);
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
                        Tile t = rtn.find(a -> a.x == tile.x && a.y == tile.y);
                        if (t != null) {
                            t.build = tile.build;
                            if (tile.team() != null) {
                                t.team = tile.team();
                            }
                        }
                    }
                }

                @Override
                public mindustry.world.Tile tile(int index) {
                    tile.x = (short) (index % width);
                    tile.y = (short) (index / width);
                    return tile;
                }

                @Override
                public mindustry.world.Tile create(int x, int y, int floorID, int overlayID, int wallID) {
                    rtn.add(new Tile(x, y, floorID, overlayID, wallID));
                    return tile;
                }
            }));
            return rtn;
        } finally {
            content.setTemporaryMapper(null);
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
}
