package uwu.misaka;

import arc.struct.Seq;

import java.io.IOException;

public class Entry {
    public static int w, h;

    public static void main(String[] s) throws IOException {
        new Parser();
        Seq<FakeTile> meme = Service.readMap(Parser.download("https://cdn.discordapp.com/attachments/831916991103107195/860949148471656488/baka.msav"));
        new Drawler(meme, w, h);
    }
}