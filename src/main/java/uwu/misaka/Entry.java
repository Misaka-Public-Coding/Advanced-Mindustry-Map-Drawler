package uwu.misaka;

import arc.struct.Seq;

import java.io.IOException;

public class Entry {

    public static Parser parser = new Parser();
    public static int w, h;

    public static void main(String[] s) throws IOException {
        //new Drawler(Service.loadMap(new Fi("map_bb.msav")));
        Seq<FakeTile> meme = Service.readMap(Parser.download("https://cdn.discordapp.com/attachments/831916991103107195/860949148471656488/baka.msav"));
        new Drawler(meme, w, h);
    }
}
