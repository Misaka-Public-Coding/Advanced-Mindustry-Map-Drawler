package uwu.misaka;

import arc.struct.Seq;

import java.io.IOException;

public class Entry {
    public static int w, h;

    public static void main(String[] s) throws IOException {
        new Parser();
        //Seq<FakeTile> meme = Service.readMap(Parser.download("https://cdn.discordapp.com/attachments/831916991103107195/860949148471656488/baka.msav"));
        Seq<FakeTile> meme = Service.readMap(Parser.download("https://cdn.discordapp.com/attachments/416719902641225732/861537882019463168/mindustry_map_hard_death_flats_cause_too_hard_to_test_so_u_r_my_test_subjects_i_guess_and_meow_meow_.msav"));
        //Seq<FakeTile> meme = Service.readMap(Parser.download("https://cdn.discordapp.com/attachments/697934034017452073/862400695372873758/owoeweuwu.msav"));
        //Seq<FakeTile> meme = Service.readMap(Parser.download("https://cdn.discordapp.com/attachments/416719902641225732/861987396903239710/sector_alpha_the_tunnel_fixed.msav"));
        //Seq<FakeTile> meme = Service.readMap(new FileInputStream(new File("map_bb.msav")));
        new Drawler(meme, w, h);
    }
}