package uwu.misaka;

import arc.util.Nullable;
import mindustry.ctype.ContentType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.world.Block;

import static mindustry.Vars.content;

public class FakeTile {
    public Block floor;
    public Block overlay;
    public Block wall;
    public int x;
    public int y;
    @Nullable
    public Team team;
    @Nullable
    public Building build;

    public FakeTile(int x, int y, int fId, int oId, int bId) {
        this.x = (short) x;
        this.y = (short) y;
        this.floor = content.getByID(ContentType.block, fId);
        this.overlay = content.getByID(ContentType.block, oId);
        this.wall = content.getByID(ContentType.block, bId);
    }
}
