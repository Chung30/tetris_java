package Model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class ImageLoader {

    public static BufferedImage loadImage(String string) {
        try {
        	return ImageIO.read(new File("data" + string));

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
 // loading the sound  
    public static Clip loadMusic(String dir) {  
	    try {  
	    	Clip clp = AudioSystem.getClip();  
	    	clp.open(AudioSystem.getAudioInputStream(ImageLoader.class.getResource(dir)));  
	    	return clp;  
	    } catch(Exception obj) {  
	    	obj.printStackTrace();  
	    }
	    return null;
    }  
}
