package uwu.nekonya;

import arc.util.Nullable;
import mindustry.ctype.ContentType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.world.Block;

import static mindustry.Vars.content;

public class Tile {
    public Block floor;
    public Block overlay;
    public Block wall;
    public int x;
    public int y;
    @Nullable
    public Team team;
    @Nullable
    public Building build;

    public byte rotation(){
        if(build==null){
            return 0;
        }else{
            return (byte) build.rotation();
        }
    }

    public Object config(){
        if(build==null){
            return null;
        }else{
            return build.config();
        }
    }

    public Tile(int x, int y, int fId, int oId, int bId) {
        this.x = (short) x;
        this.y = (short) y;
        this.floor = content.getByID(ContentType.block, fId);
        this.overlay = content.getByID(ContentType.block, oId);
        this.wall = content.getByID(ContentType.block, bId);
    }
}
