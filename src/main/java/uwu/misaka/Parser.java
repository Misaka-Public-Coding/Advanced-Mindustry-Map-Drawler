package uwu.misaka;

import arc.Core;
import arc.files.Fi;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureAtlas;
import arc.struct.ObjectMap;
import mindustry.Vars;
import mindustry.core.ContentLoader;
import mindustry.core.GameState;
import mindustry.core.Version;
import mindustry.core.World;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.world.Tile;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static mindustry.Vars.world;

public class Parser {
    public ObjectMap<String, BufferedImage> regions;
    public static ObjectMap<String, Fi> imageFiles = new ObjectMap<>();

    public Parser() {
        regions = new ObjectMap<>();
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

        world = new World() {
            public Tile tile(int x, int y) {
                return new Tile(x, y);
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

}