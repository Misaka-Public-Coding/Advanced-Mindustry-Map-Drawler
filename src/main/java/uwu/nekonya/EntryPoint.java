package uwu.nekonya;

import arc.struct.Seq;
import uwu.misaka.FakeTile;
import uwu.misaka.Parser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class EntryPoint {
    public static void main(String[] s) throws IOException {
        Service.initParser();
        Seq<Tile> tiles = Service.readMap(Service.download("https://cdn.discordapp.com/attachments/416719902641225732/861537882019463168/mindustry_map_hard_death_flats_cause_too_hard_to_test_so_u_r_my_test_subjects_i_guess_and_meow_meow_.msav"));
        DrawMachine machine = new DrawMachine(tiles);
        BufferedImage image = machine.processing();
        ImageIO.write(image, "png", new File("baka.png"));
    }
}
