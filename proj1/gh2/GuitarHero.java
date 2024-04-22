package gh2;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {


    private static final String KEYBOARD = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    public static final double CONCERT = 440.0;
    public static final double PARAM24 = 24.0;
    public static final double PARAM12 = 12.0;


    public static double concertKey(int index) {
        return CONCERT * Math.pow(2, (index - PARAM24) / PARAM12);
    }

    public static void main(String[] args) {

        GuitarString[] strings = new GuitarString[KEYBOARD.length()];

        for (int i = 0; i < KEYBOARD.length(); i++) {
            double newCONCERT = concertKey(i);
            strings[i] = new GuitarString(newCONCERT);
        }

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int index = KEYBOARD.indexOf(key);
                if (index >= 0) {
                    strings[index].pluck();
                }
            }

            double sample = 0.0;

            for (GuitarString index:strings) {
                sample = sample + index.sample();
            }


            StdAudio.play(sample);

            for (GuitarString index:strings) {
                index.tic();
            }

        }
    }



}
