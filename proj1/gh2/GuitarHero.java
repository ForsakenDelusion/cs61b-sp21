package gh2;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {


    private static final String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    public static final double CONCERT_A = 440.0;


    public static double CONCERTKEY(int index){
        return 440 * Math.pow(2, (index - 24) / 12);
    }

    public static void main(String[] args) {

        GuitarString[] strings = new GuitarString[keyboard.length()];

        for (int i = 0; i < keyboard.length(); i++) {
            double newCONCERT = CONCERTKEY(i);
            strings[i] = new GuitarString(newCONCERT);
        }

        while(true){
            if(StdDraw.hasNextKeyTyped()){
                char key = StdDraw.nextKeyTyped();
                int index = keyboard.indexOf(key);
                if(index>=0){
                    strings[index].pluck();
                }
            }

            double sample = 0.0;

            for(GuitarString index:strings){
                sample=sample+index.sample();
            }


            StdAudio.play(sample);

            for(GuitarString index:strings){
                index.tic();
            }

        }
    }



}
